package ru.nsu.kuklin.gradebook;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.*;

/**
 * Class for tests.
 */
public class GradebookTest {
    @Test
    public void testEmptyGradebook() {
        var empty = new GradeBook();
        assertEquals(false, empty.canGetBiggerScholarship());
        assertEquals(false, empty.canGetHonorsDiploma());
        assertTrue(Float.isNaN(empty.average()));
    }

    @Test
    public void testOneSemestr() {
        var book = new GradeBook()
            .withMark("Матан", Mark.FOUR)
            .withMark("Дискретка", Mark.THREE)
            .withMark("Императивка", Mark.TWO)
            .withMark("Декларативка", Mark.FOUR)
            .nextSemester();

        assertEquals(false, book.canGetBiggerScholarship());
        assertEquals(false, book.canGetHonorsDiploma());
        assertEquals(13.0f / 4.0f, book.average());
    }

    @Test
    public void testScholarship() {
        var book = new GradeBook()
            .withMark("Матан", Mark.FIVE)
            .withMark("Дискретка", Mark.FIVE)
            .withMark("Императивка", Mark.FIVE)
            .withMark("Декларативка", Mark.FIVE)
            .nextSemester();

        assertEquals(true, book.canGetBiggerScholarship());
        assertEquals(true, book.canGetHonorsDiploma());
        assertEquals(20.0f / 4.0f, book.average());
    }

    @Test
    public void testScholarshipButNotDiploma() {
        var book = new GradeBook()
            .withMark("Матан", Mark.TWO)
            .withMark("Дискретка", Mark.TWO)
            .withMark("Императивка", Mark.TWO)
            .nextSemester()
            .withMark("Декларативка", Mark.FIVE)
            .nextSemester();

        assertEquals(true, book.canGetBiggerScholarship());
        assertEquals(false, book.canGetHonorsDiploma());
        assertEquals(11.0f / 4.0f, book.average());
    }

    @Test
    public void testNoScholarshipButDiploma() {
        var book = new GradeBook()
            .withMark("Матан", Mark.FIVE)
            .withMark("Дискретка", Mark.FIVE)
            .withMark("Императивка", Mark.FIVE)
            .nextSemester()
            .withMark("Декларативка", Mark.TWO)
            .nextSemester();

        assertEquals(false, book.canGetBiggerScholarship());
        assertEquals(true, book.canGetHonorsDiploma());
        assertEquals(17.0f / 4.0f, book.average());
    }

    @Test
    public void testNoScholarshipButDiplomaMultipleSemesters() {
        var book = new GradeBook()
            .withMark("Матан", Mark.TWO)
            .withMark("Дискретка", Mark.FIVE)
            .withMark("Императивка", Mark.FIVE)
            .nextSemester()
            .withMark("Декларативка", Mark.TWO)
            .withMark("Матан", Mark.FIVE)
            .nextSemester();

        assertEquals(false, book.canGetBiggerScholarship());
        assertEquals(true, book.canGetHonorsDiploma());
        assertEquals(19.0f / 5.0f, book.average());
    }

    @Test
    public void testNoScholarshipButDiplomaReassign() {
        var book = new GradeBook()
            .withMark("Матан", Mark.TWO)
            .withMark("Дискретка", Mark.FIVE)
            .withMark("Императивка", Mark.FIVE)
            .nextSemester()
            .withMark("Декларативка", Mark.TWO)
            .withMark("Матан", Mark.TWO)
            .withMark("Матан", Mark.FIVE)
            .nextSemester();

        assertEquals(false, book.canGetBiggerScholarship());
        assertEquals(true, book.canGetHonorsDiploma());
        assertEquals(19.0f / 5.0f, book.average());
    }

    @Test
    public void testNoScholarshipButDiplomaRetest() {
        var book = new GradeBook()
            .withMark("Матан", Mark.FIVE)
            .withMark("Дискретка", Mark.FIVE)
            .withMark("Императивка", Mark.FIVE)
            .nextSemester()
            .withMark("Декларативка", Mark.FIVE)
            .withMark("Матан", Mark.TWO)
            .withMark("Матан", Mark.FIVE)
            .nextSemester();

        assertEquals(false, book.canGetBiggerScholarship());
        assertEquals(true, book.canGetHonorsDiploma());
        assertEquals(25.0f / 5.0f, book.average());
    }
}
