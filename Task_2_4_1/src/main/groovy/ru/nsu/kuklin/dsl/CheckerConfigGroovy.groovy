package ru.nsu.kuklin.dsl

import org.codehaus.groovy.control.CompilerConfiguration

class CheckerConfigGroovy {
    ArrayList<Task> tasks = null
    ArrayList<Student> students = null

    private void task(Closure c) {
        if (!tasks) {
            tasks = new ArrayList<>()
        }
        Task task = new Task()
        c.setDelegate(task)
        c.setResolveStrategy(Closure.DELEGATE_FIRST)
        c.call()
        tasks.add(task)
    }

    private void student(Closure c) {
        if (!students) {
            students = new ArrayList<>()
        }
        var s = new Student()
        c.setDelegate(s)
        c.setResolveStrategy(Closure.DELEGATE_FIRST)
        c.call()
        students.add(s)
    }

    private void loadFile(String path) {
        CompilerConfiguration cc = new CompilerConfiguration()
        cc.setScriptBaseClass(DelegatingScript.class.getName())
        ClassLoader loader = Application.class.getClassLoader()
        GroovyShell sh = new GroovyShell(loader, new Binding(), cc)
        var inputStream = loader.getResourceAsStream(path)
        if (inputStream == null) {
            println("Error: File not found: " + path)
            return;
        }
        var script = (DelegatingScript)sh.parse(new BufferedReader(new InputStreamReader(inputStream)))
        script.setDelegate(this)
        script.run()
    }
}
