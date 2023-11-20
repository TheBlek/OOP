package ru.nsu.kuklin.gradebook;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

public class GradeBook {
    public GradeBook() {
        subjectMapping = new HashMap();
        currentSemestr = 0;
        subjects = new ArrayList();
    }

    public GradeBook nextSemestr() {
        currentSemestr++;
        return this;
    }

    public GradeBook withMark(String subject, Mark mark) {
        if (!subjectMapping.containsKey(subject)) {
            subjectMapping.put(subject, subjects.size());
            subjects.add(new Subject());
        }
        var id = subjectMapping.get(subject);
        var marks = subjects.get(id).marks;
        if (marks.size() > 0 && marks.get(marks.size() - 1).semestr == currentSemestr) {
            marks.set(marks.size() - 1, new SubjectMark(mark, currentSemestr));
        } else {
            marks.add(new SubjectMark(mark, currentSemestr));
        }
        return this;
    }

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

    public boolean canGetBiggerScholarship() {
        if (currentSemestr == 0) {
            return false;
        }
        int sem = currentSemestr - 1;
        for (var subject : subjects) {
            var mark = subject
                .marks
                .stream()
                .filter((m) -> m.semestr == sem)
                .findFirst();
            if (mark.isPresent() && mark.get().mark != Mark.FIVE) {
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

    private static record SubjectMark(Mark mark, int semestr) {};

    private Map<String, Integer> subjectMapping;
    private ArrayList<Subject> subjects;
    private int currentSemestr;
}

