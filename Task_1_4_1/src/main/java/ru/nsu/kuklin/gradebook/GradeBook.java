package ru.nsu.kuklin.gradebook;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Gradebook implementation designed for FIT.
 */
public class GradeBook {
    /**
     * Create empty gradebook.
     */
    public GradeBook() {
        subjectMapping = new HashMap();
        currentSemester = 0;
        subjects = new ArrayList();
    }

    /**
     * Starts new semestr.
     * All mark for the current semester are cemented and you won't be able to change.
     */
    public GradeBook nextSemester() {
        currentSemester++;
        return this;
    }

    /**
     * Adds a mark for the subject in a current semester.
     * This will override the mark if it is already been defined before.
     */
    public GradeBook withMark(String subject, Mark mark) {
        if (!subjectMapping.containsKey(subject)) {
            subjectMapping.put(subject, subjects.size());
            subjects.add(new Subject());
        }
        var id = subjectMapping.get(subject);
        var marks = subjects.get(id).marks;
        if (marks.size() > 0 && marks.get(marks.size() - 1).semester == currentSemester) {
            marks.set(
                marks.size() - 1,
                new SubjectMark(mark, currentSemester, true)
            );
        } else {
            marks.add(new SubjectMark(mark, currentSemester, false));
        }
        return this;
    }

    /**
     * Return total average of all marks recieved before.
     * Returns Float.NaN if gradebook is empty.
     */
    public float average() {
        int sum = subjects
            .stream()
            .flatMap((subj) -> subj.marks.stream())
            .mapToInt((m) -> m.mark.getMark())
            .sum();
        int count = subjects.stream().mapToInt((subj) -> subj.marks.size()).sum();
        return (float) sum / count;
    }

    /**
     * Calculates whether student with this gradebook will receive honors diploma.
     * Honors diploma requires student to have >= 75% of marks for all subjects to be 5
     */
    public boolean canGetHonorsDiploma() {
        if (subjects.size() == 0) {
            return false;
        }
        int fiveCount = (int) subjects
            .stream()
            .map((subj) -> subj.marks.get(subj.marks.size() - 1))
            .filter((m) -> m.mark == Mark.FIVE)
            .count();
        return fiveCount >= subjects.size() * 3.0f / 4.0f;
    }

    /**
     * Whether student with this gradebook will receive bigger scholarship in current semester.
     * This requires student to have all 5s for the previous semeter.
     */
    public boolean canGetBiggerScholarship() {
        if (currentSemester == 0) {
            return false;
        }
        int sem = currentSemester - 1;
        return subjects
            .stream()
            .allMatch(
                (subj) -> {
                    return subj
                        .marks
                        .stream()
                        .filter((m) -> m.semester == sem)
                        .filter((m) -> m.retest || m.mark != Mark.FIVE)
                        .findFirst()
                        .isEmpty();
                }
            );
    }

    /**
     * Convert a gradebook to string representation.
     * This function will assume 8-space tab character
     */
    public String toString() {
        var header = "Semester\t" + subjectMapping
            .entrySet()
            .stream()
            .sorted(Entry.<String, Integer>comparingByValue())
            .map((e1) -> e1.getKey())
            .collect(Collectors.joining("\t"));

        int[] columnWidths = subjectMapping
            .entrySet()
            .stream()
            .sorted(Entry.<String, Integer>comparingByValue())
            .mapToInt((e1) -> e1.getKey().length())
            .toArray();

        var body = new StringBuilder();
        for (int i = 0; i < currentSemester; i++) {
            // Need to copy the value for lambda in streams
            final int sem = i;
            body.append(sem + 1);
            body.append("\t\t");

            Streams.mapWithIndex(subjects
                .stream(),
                (subj, index) -> {
                    int tabCount = (int) (columnWidths[(int) index] / 8) + 1;
                    return subj
                        .marks
                        .stream()
                        .filter(m -> m.semester == sem)
                        .map(m -> m.mark.toString())
                        .findFirst()
                        .orElse("-") + "\t".repeat(tabCount);
                })
                .forEach(mark -> body.append(mark));

            body.append("\n");
        }
 
        return header + "\n" + body;
    }

    private static class Subject {
        public List<SubjectMark> marks; 

        public Subject() {
            marks = new ArrayList<SubjectMark>();
        }
    }

    private static record SubjectMark(Mark mark, int semester, boolean retest) {}

    private Map<String, Integer> subjectMapping;
    private List<Subject> subjects;
    private int currentSemester;
}

