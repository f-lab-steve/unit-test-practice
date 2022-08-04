package com.flab.service;

import com.flab.exception.NoSuchCourseException;
import com.flab.exception.NoSuchScoreException;
import com.flab.exception.NoSuchStudentException;
import com.flab.model.Course;
import com.flab.model.Score;
import com.flab.model.Student;
import com.flab.repository.CourseRepository;
import com.flab.repository.ScoreRepository;
import com.flab.repository.StudentRepository;
import com.flab.service.impl.TranscriptServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceImplTest {

    private static final Course KOREAN = new Course().setId(1).setName("korean");
    private static final Course ENGLISH = new Course().setId(2).setName("english");
    private static final Course MATH = new Course().setId(3).setName("math");
    private static final Course SCIENCE = new Course().setId(4).setName("science");

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private TranscriptServiceImpl transcriptService;


    @Test
    void testGetAverageScore_HappyCase_VerifyReturnedValue_Success() {
        // given
        final int studentID = 1;
        final Student trey = new Student().setId(studentID).setName("Trey").setMajor("Computer Engineering")
                .setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));

        Mockito.when(studentRepository.getStudent(1))
                .thenReturn(Optional.of(trey));

        Mockito.when(scoreRepository.getScore(studentID, 1))
                .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(100)));
        Mockito.when(scoreRepository.getScore(studentID, 2))
                .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(90)));
        Mockito.when(scoreRepository.getScore(studentID, 3))
                .thenReturn(Optional.of(new Score().setCourse(MATH).setScore(80)));
        Mockito.when(scoreRepository.getScore(studentID, 4))
                .thenReturn(Optional.of(new Score().setCourse(SCIENCE).setScore(70)));

        // when
        final double averageScore = transcriptService.getAverageScore(studentID);

        // then
        Assertions.assertEquals(85.0, averageScore);
    }

    @Test
    @DisplayName("주어진 studentID에 해당되는 Student가 없을 때, getAverageScore()는 NoSuchStudentException을 Throw 한다.")
    void testGetAverageScore_StudentNotExist_ThrowNoSuchStudentException_Error() {
        // given
        final int studentID = 1;
        Mockito.when(studentRepository.getStudent(studentID))
                .thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(NoSuchStudentException.class, () -> transcriptService.getAverageScore(studentID));
    }

    @Test
    @DisplayName("getAverageScore()에서 studentRepository.getStudent()를 한 번, scoreRepository.getScore()를 student의 course 개수만큼 호출한다.")
    void testGetAverageScore_HappyCase_VerifyNumberOfInteractions_Success() {
        // TODO:
        // Hint: Mockito.verify() 사용

        // given
        final int studentID = 1;
        final Student trey = new Student().setId(studentID).setName("Trey").setMajor("Computer Engineering")
                .setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));

        Mockito.when(studentRepository.getStudent(1))
                .thenReturn(Optional.of(trey));

        Mockito.when(scoreRepository.getScore(studentID, 1))
                .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(100)));
        Mockito.when(scoreRepository.getScore(studentID, 2))
                .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(90)));
        Mockito.when(scoreRepository.getScore(studentID, 3))
                .thenReturn(Optional.of(new Score().setCourse(MATH).setScore(80)));
        Mockito.when(scoreRepository.getScore(studentID, 4))
                .thenReturn(Optional.of(new Score().setCourse(SCIENCE).setScore(70)));

        // when
        transcriptService.getAverageScore(studentID);

        // then
        Mockito.verify(studentRepository, Mockito.times(1)).getStudent(studentID);
        Mockito.verify(scoreRepository, Mockito.times(4)).getScore(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("scoreRepository로부터 학생의 Score를 하나라도 찾을 수 없는 경우, getAverageScore()는 NoSuchScoreException을 Throw 한다.")
    void testGetAverageScore_ScoreNotExist_ThrowNoSuchScoreException_Error() {
        // TODO:

        // given
        final int studentID = 1;
        final Student trey = new Student().setId(studentID).setName("Trey").setMajor("Computer Engineering")
                .setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));

        Mockito.when(studentRepository.getStudent(1))
                .thenReturn(Optional.of(trey));

        Mockito.when(scoreRepository.getScore(eq(studentID), ArgumentMatchers.anyInt()))
                .thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(NoSuchScoreException.class, () -> transcriptService.getAverageScore(studentID));
    }

    @Test
    @DisplayName("getRankedStudentAsc()를 호출하면, 입력으로 주어진 course를 수강하는 모든 학생들의 리스트를 성적의 내림차순으로 리턴한다.")
    void testGetRankedStudentsAsc_HappyCase_VerifyReturnedValueAndInteractions_Success() {
        // TODO:

        // given
        final int studentID_1 = 1;
        final Student trey = new Student().setId(studentID_1).setName("Trey").setMajor("Computer Engineering")
                .setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));

        final int studentID_2 = 2;
        final Student spring = new Student().setId(studentID_2).setName("Spring").setMajor("Computer Engineering")
                .setCourses(List.of(KOREAN, ENGLISH, MATH));

        final int studentID_3 = 3;
        final Student summer = new Student().setId(studentID_3).setName("Summer").setMajor("Computer Engineering")
                .setCourses(List.of(KOREAN, ENGLISH));

        Mockito.when(courseRepository.getCourse(1))
                .thenReturn(Optional.of(KOREAN));

        Mockito.when(scoreRepository.getScores(1))
                .thenReturn(new HashMap<>() {{
                        put(1, new Score().setCourse(KOREAN).setScore(100));
                        put(2, new Score().setCourse(KOREAN).setScore(70));
                        put(3, new Score().setCourse(KOREAN).setScore(80));
                    }});

        Mockito.when(studentRepository.getAllStudents())
                .thenReturn(new LinkedList<>() {{
                    add(trey);
                    add(spring);
                    add(summer);
                }});

        // when
        List<Student> rankedStudents = transcriptService.getRankedStudentsAsc(1);

        //then
        Assertions.assertAll(
                () -> {
                    Assertions.assertEquals(rankedStudents.get(0).getId(), studentID_1);
                    Assertions.assertEquals(rankedStudents.get(1).getId(), studentID_3);
                    Assertions.assertEquals(rankedStudents.get(2).getId(), studentID_2);
                });
    }

    @Test
    @DisplayName("courseRepository에서 입력으로 주어진 courseID로 course를 조회할 수 없으면 NoSuchCourseException을 Throw 한다.")
    void testGetRankedStudentsAsc_CourseNotExist_ThrowNoSuchCourseException_Error() {
        // TODO:

        // given
        Mockito.when(courseRepository.getCourse(1))
                .thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(NoSuchCourseException.class, () -> courseRepository.getCourse(1));
    }
}
