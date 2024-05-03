package ru.nsu.kuklin.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import com.puppycrawl.tools.checkstyle.Main;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;

public class Application {
    public static void main(String[] args) {
        // Read configuration
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        ClassLoader loader = Application.class.getClassLoader();
        GroovyShell sh = new GroovyShell(loader, new Binding(), cc);
        var inputStream = loader.getResourceAsStream("config.groovy");
        if (inputStream == null) {
            System.out.println("Error: No config found.");
            return;
        }
        var script = (DelegatingScript)sh.parse(new BufferedReader(new InputStreamReader(inputStream)));
        CheckerConfigGroovy config = new CheckerConfigGroovy();
        script.setDelegate(config);
        script.run();

        // Do the stuff
        String repoPrefix = "src/main/resources/repoes";
        GradleConnector connector = GradleConnector.newConnector();
        var results = new ArrayList<ArrayList<TaskResult>>();

        if (updateStudentsRepos(config, repoPrefix)) return;

        for (var task : config.getTasks()) {
            /* Check all the tasks from this student */
            var taskResults = new ArrayList<TaskResult>();
            for (var student : config.getStudents()) {
                System.out.println("Checking task " + task.getName() + "@" + student.getNickname());
                var projectFile = new File(String.format("%s/%s/%s", repoPrefix, student.getNickname(), task.getName()));

                if (!projectFile.exists()) {
                    System.out.println("Project directory not found");
                    continue;
                }

                var connection = connector.forProjectDirectory(projectFile).connect();
                var builds = runTask(connection, new TaskRunConfig("build").withClean().withExcludeTests());
                var tests = runTask(connection, new TaskRunConfig("test"));

                TestCounts counts = getTestCounts(repoPrefix, student, task);

                int coveragePercent = 0;
                if (tests.isSuccess()) {
                    coveragePercent = getCoveragePercentage(connection, repoPrefix, student, task);
                }

                CheckstyleResult checkstyle = getCheckstyleResult(task, student, repoPrefix);

                runTask(connection, new TaskRunConfig("javadoc"));

                taskResults.add(new TaskResult(student, builds.isSuccess(), counts.total(), counts.fail(), counts.skip(), coveragePercent, checkstyle));
                connection.close();
            }
            results.add(taskResults);
        }
        System.out.println(results);

        generateReport(results, config);
    }

    private static void generateReport(ArrayList<ArrayList<TaskResult>> results, CheckerConfigGroovy config) {
        var engine = new TemplateEngine();
        engine.setTemplateResolver(new FileTemplateResolver());
        var ctx = new Context();
        ctx.setVariable("results", results);
        ctx.setVariable("tasks", config.getTasks());

        var report = new File("report.html");
        try (var writer = new FileOutputStream(report)) {
            var result = engine.process("src/main/resources/reportTemplate.html", ctx);
            writer.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Failed to write report: " + e);
        }
    }

    private static CheckstyleResult getCheckstyleResult(Task task, Student student, String repoPrefix) {
        CheckstyleResult checkstyle = CheckstyleResult.CLEAN;
        try {
            var outFile = "checkstyle.txt";
            int exitCode = catchSystemExit(() -> {
                try {
                    String configPath = String.format("%s/%s/.github/google_checks.xml", repoPrefix, student.getNickname());
                    var mainSourcePath = String.format("%s/%s/%s/src/main/java/", repoPrefix, student.getNickname(), task.getName());
                    var testSourcePath = String.format("%s/%s/%s/src/test/java/", repoPrefix, student.getNickname(), task.getName());
                    Main.main("-c", configPath, "-o", outFile, mainSourcePath, testSourcePath);
                } catch (IOException e) {
                    System.out.println("Failed to call checkstyle: " + e);
                }
            });

            int warnCount = 0;
            try (var reader = new FileInputStream(outFile)) {
                var scanner = new Scanner(reader);
                int cnt = 0;
                while (scanner.hasNextLine()) {
                    scanner.nextLine();
                    cnt += 1;
                }
                warnCount = cnt - 2;
            } catch (IOException e) {
                System.out.println("Failed to read checkstyle's output");
            }
            if (exitCode != 0) {
                checkstyle = CheckstyleResult.ERROR;
            } else if (warnCount > 0) {
                checkstyle = CheckstyleResult.WARNING;
            }
        } catch (Exception e) {
            System.out.println("Something weird happened... Exit should have been called... No checkstyle data, sry");
        }
        return checkstyle;
    }

    private static int getCoveragePercentage(ProjectConnection connection, String repoPrefix, Student student, Task task ) {
        runTask(connection, new TaskRunConfig("jacocoTestReport"));

        File jacocoFile = new File(
            String.format(
                "%s/%s/%s/build/reports/jacoco/test/jacocoTestReport.xml",
                repoPrefix,
                student.getNickname(),
                task.getName()
            )
        );

        try {
            Document report = Jsoup.parse(jacocoFile, "UTF-8", "", Parser.xmlParser());
            report.getElementsByTag("package").remove();
            float total = 0.0f;
            for (var el : report.getElementsByTag("counter")) {
                var cov = Integer.parseInt(el.attribute("covered").getValue());
                var miss = Integer.parseInt(el.attribute("missed").getValue());
                total += (float) cov / (cov + miss);
            }
            float coverage = total / report.getElementsByTag("counter").size();
            return Math.round(coverage * 100.0f);
        } catch (IOException e) {
            System.out.println("Failed to find jacoco test report: " + e);
            return 0;
        }
    }

    private static TestCounts getTestCounts(String repoPrefix, Student student, Task task) {
        File reportFile = new File(
            String.format(
                "%s/%s/%s/build/reports/tests/test/index.html",
                repoPrefix,
                student.getNickname(),
                task.getName()
            )
        );
        try {
            Document report = Jsoup.parse(reportFile, "UTF-8");
            Element el = report.getElementById("summary");
            int testCount = Integer.parseInt(
                report.getElementById("tests").getElementsByClass("counter").get(0).text()
            );
            int failCount = Integer.parseInt(
                report.getElementById("failures").getElementsByClass("counter").get(0).text()
            );
            int skipCount = Integer.parseInt(
                report.getElementById("ignored").getElementsByClass("counter").get(0).text()
            );
            return new TestCounts(testCount, failCount, skipCount);
        } catch (IOException e) {
            System.out.println("Failed to find test report file: " + reportFile);
            return new TestCounts(0, 0, 0);
        }
    }

    private static boolean updateStudentsRepos(CheckerConfigGroovy config, String repoPrefix) {
        for (var student : config.getStudents()) {
            /* Bring student repository up to date */
            try {
                var repoFile = new File(String.format("%s/%s", repoPrefix, student.getNickname()));
                Git repo;
                if (!repoFile.exists()) {
                    repo = Git.cloneRepository()
                        .setURI(String.format("https://github.com/%s/OOP.git", student.getNickname()))
                        .setDirectory(repoFile)
                        .call();
                } else {
                    try {
                        repo = Git.open(repoFile);
                    } catch (IOException e) {
                        System.out.println("Uhhhhhhhhhh");
                        return true;
                    }
                }
                assert repo != null;
                repo.pull().call();
            } catch (GitAPIException e) {
                System.out.println("Failed to clone: " + e);
            }
        }
        return false;
    }

    private static TaskRunResult runTask(ProjectConnection conn, TaskRunConfig config) {
        Exception error = null;
        var stream = new OutputStream() {
            private final StringBuilder stringBuilder = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                stringBuilder.append((char)b);
            }

            public String toString() {
                return stringBuilder.toString();
            }
        };
        try {
            System.out.printf("Running %s...", config.task());
            var builder = conn.newBuild();
            if (config.clean()) {
                builder = builder.forTasks("clean", config.task());
            } else {
                builder = builder.forTasks(config.task());
            }
            if (config.excludeTests()) {
                builder = builder.addArguments("-x",  "test");
            }
            if (config.getStdOut()) {
                builder = builder.setStandardOutput(stream);
            }
            builder.run();
            System.out.println("Success");
        } catch (Exception e) {
            error = e;
            System.out.println("Failure: " + error);
        }
        return new TaskRunResult(error == null, stream.toString());
    }
}