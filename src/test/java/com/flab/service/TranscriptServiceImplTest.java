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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceImplTest {

    private static final Course KOREAN = new Course().setId(1).setName("korean");
    private static final Course ENGLISH = new Course().setId(2).setName("english");
    private static final Course MATH = new Course().setId(3).setName("math");
    private static final Course SCIENCE = new Course().setId(4).setName("science");

    // newly added to eliminate duplicate codes & magic numbers
    private static final int KOREAN_COURSE_ID = KOREAN.getId();
    private static final int ENGLISH_COURSE_ID = ENGLISH.getId();
    private static final int MATH_COURSE_ID = MATH.getId();
    private static final int SCIENCE_COURSE_ID = SCIENCE.getId();

    // newly added to eliminate duplicate codes
    private static final Student TREY = new Student().setId(1).setName("Trey").setMajor("Computer Engineering").setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));
    private static final Student AIDEN = new Student().setId(2).setName("Aiden").setMajor("Economics").setCourses(List.of(KOREAN, ENGLISH, MATH));
    private static final Student YOUNG = new Student().setId(3).setName("Young").setMajor("Art").setCourses(List.of(KOREAN, ENGLISH));

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
        final int studentID1 = TREY.getId();
        final int studentID2 = YOUNG.getId();

        Mockito.when(studentRepository.getStudent(studentID1))
                .thenReturn(Optional.of(TREY));

        Mockito.when(scoreRepository.getScore(studentID1, KOREAN_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(100)));
        Mockito.when(scoreRepository.getScore(studentID1, ENGLISH_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(90)));
        Mockito.when(scoreRepository.getScore(studentID1, MATH_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(MATH).setScore(80)));
        Mockito.when(scoreRepository.getScore(studentID1, SCIENCE_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(SCIENCE).setScore(70)));

        Mockito.when(studentRepository.getStudent(studentID2))
                .thenReturn(Optional.of(YOUNG));

        Mockito.when(scoreRepository.getScore(studentID2, KOREAN_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(95)));
        Mockito.when(scoreRepository.getScore(studentID2, ENGLISH_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(85)));

        // when
        transcriptService.getAverageScore(studentID1);
        transcriptService.getAverageScore(studentID2);

        // then
        Mockito.verify(studentRepository, times(2)).getStudent(anyInt());
        Mockito.verify(scoreRepository, times(6)).getScore(anyInt(), anyInt());

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    @DisplayName("scoreRepository로부터 학생의 Score를 하나라도 찾을 수 없는 경우, getAverageScore()는 NoSuchScoreException을 Throw 한다.")
    void testGetAverageScore_ScoreNotExist_ThrowNoSuchScoreException_Error() {
        // TODO:

        // given
        final int studentID = TREY.getId();

        Mockito.when(studentRepository.getStudent(studentID))
                .thenReturn(Optional.of(TREY));

        Mockito.when(scoreRepository.getScore(studentID, KOREAN_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(100)));
        Mockito.when(scoreRepository.getScore(studentID, ENGLISH_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(90)));
        Mockito.when(scoreRepository.getScore(studentID, MATH_COURSE_ID))
                .thenReturn(Optional.of(new Score().setCourse(MATH).setScore(80)));
        Mockito.when(scoreRepository.getScore(studentID, SCIENCE_COURSE_ID))
                .thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(NoSuchScoreException.class, () -> transcriptService.getAverageScore(studentID));

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    @DisplayName("getRankedStudentAsc()를 호출하면, 입력으로 주어진 course를 수강하는 모든 학생들의 리스트를 성적의 내림차순으로 리턴한다.")
    void testGetRankedStudentsAsc_HappyCase_VerifyReturnedValueAndInteractions_Success() {
        // TODO:

        // given
        Mockito.when(courseRepository.getCourse(KOREAN_COURSE_ID)).thenReturn(Optional.of(KOREAN));
        Mockito.when(scoreRepository.getScores(KOREAN_COURSE_ID)).thenReturn(Map.of(
                TREY.getId(), (new Score().setCourse(KOREAN).setScore(80)),
                AIDEN.getId(), (new Score().setCourse(KOREAN).setScore(90)),
                YOUNG.getId(), (new Score().setCourse(KOREAN).setScore(100))
        ));
        Mockito.when(studentRepository.getAllStudents()).thenReturn(List.of(TREY, AIDEN, YOUNG));

        // when
        List<Student> rankedStudentsAsc = transcriptService.getRankedStudentsAsc(KOREAN_COURSE_ID);

        // then
        Assertions.assertEquals(List.of(YOUNG, AIDEN, TREY), rankedStudentsAsc);

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    @DisplayName("courseRepository에서 입력으로 주어진 courseID로 course를 조회할 수 없으면 NoSuchCourseException을 Throw 한다.")
    void testGetRankedStudentsAsc_CourseNotExist_ThrowNoSuchCourseException_Error() {
        // TODO:

        // given
        Mockito.when(courseRepository.getCourse(KOREAN_COURSE_ID)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(NoSuchCourseException.class, () -> transcriptService.getRankedStudentsAsc(KOREAN_COURSE_ID));

        // throw new UnsupportedOperationException("Not implemented yet");
    }
}
