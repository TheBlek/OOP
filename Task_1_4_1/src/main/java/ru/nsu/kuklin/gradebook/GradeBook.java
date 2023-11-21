package ru.nsu.kuklin.gradebook;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

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
        if (marks.size() > 0 && marks.get(marks.size() - 1).semestr == currentSemester) {
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
        int sum = 0;
        int count = 0;
        for (var subject : subjects) {
            for (var mark : subject.marks) {
                sum += mark.mark.getMark();
            }
            count += subject.marks.size();
        }
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
        int fiveCount = 0;
        for (var subject : subjects) {
            var marks = subject.marks;
            if (marks.get(marks.size() - 1).mark.equals(Mark.FIVE)) {
                fiveCount++;
            }
        }
        return fiveCount >= subjects.size() * 3.0f / 4.0f;
    }

    /**
     * Whether student with this gradebook will receive bigger scholarship in current semester.
     * This requires student to have all 5s for the previous semeter.
     */
    // Пересдача
    public boolean canGetBiggerScholarship() {
        if (currentSemester == 0) {
            return false;
        }
        int sem = currentSemester - 1;
        for (var subject : subjects) {
            var mark = subject
                .marks
                .stream()
                .filter((m) -> m.semestr == sem)
                .findFirst();
            if (mark.isPresent() 
                && (mark.get().retest || mark.get().mark != Mark.FIVE)) {
                return false;
            }
        }
        return true;
    }

    private static class Subject {
        public List<SubjectMark> marks; 

        public Subject() {
            marks = new ArrayList<SubjectMark>();
        }
    }

    private static record SubjectMark(Mark mark, int semestr, boolean retest) {}

    private Map<String, Integer> subjectMapping;
    private List<Subject> subjects;
    private int currentSemester;
}

