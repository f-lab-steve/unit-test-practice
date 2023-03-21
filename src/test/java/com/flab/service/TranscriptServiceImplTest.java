package com.flab.service;

import com.flab.exception.NoSuchStudentException;
import com.flab.exception.NoSuchScoreException;
import com.flab.exception.NoSuchCourseException;
import com.flab.model.Course;
import com.flab.model.Score;
import com.flab.model.Student;
import com.flab.repository.CourseRepository;
import com.flab.repository.ScoreRepository;
import com.flab.repository.StudentRepository;
import com.flab.service.impl.TranscriptServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceImplTest {

    private static final Course KOREAN = new Course().setId(1).setName("korean");
    private static final Course ENGLISH = new Course().setId(2).setName("english");
    private static final Course MATH = new Course().setId(3).setName("math");
    private static final Course SCIENCE = new Course().setId(4).setName("science");

    private static final Student trey = new Student().setId(1).setName("Trey").setMajor("Computer Engineering")
            .setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));
    private static final Student lee = new Student().setId(2).setName("Lee").setMajor("Business")
            .setCourses(List.of(KOREAN, ENGLISH, MATH, SCIENCE));


    private static final int koreanID = KOREAN.getId();
    private static final int englishID = ENGLISH.getId();
    private static final int mathID = MATH.getId();
    private static final int scienceID = SCIENCE.getId();

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private TranscriptServiceImpl transcriptService;

    void injectionTestEnv(int testCase){
        int firstStudentID = trey.getId();
        //int secondStudentID = lee.getId();

        switch(testCase) {
            case 1:
                Mockito.when(studentRepository.getStudent(firstStudentID))
                    .thenReturn(Optional.of(trey));

                Mockito.when(scoreRepository.getScore(firstStudentID, koreanID))
                        .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(100)));
                Mockito.when(scoreRepository.getScore(firstStudentID, englishID))
                        .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(90)));
                Mockito.when(scoreRepository.getScore(firstStudentID, mathID))
                        .thenReturn(Optional.of(new Score().setCourse(MATH).setScore(80)));
                Mockito.when(scoreRepository.getScore(firstStudentID, scienceID))
                        .thenReturn(Optional.of(new Score().setCourse(SCIENCE).setScore(70)));
                break;
            case 2:
                Mockito.when(studentRepository.getStudent(firstStudentID))
                        .thenReturn(Optional.of(trey));

                Mockito.when(scoreRepository.getScore(firstStudentID, koreanID))
                        .thenReturn(Optional.of(new Score().setCourse(KOREAN).setScore(100)));
                Mockito.when(scoreRepository.getScore(firstStudentID, englishID))
                        .thenReturn(Optional.of(new Score().setCourse(ENGLISH).setScore(90)));
                Mockito.when(scoreRepository.getScore(firstStudentID, mathID))
                        .thenReturn(Optional.of(new Score().setCourse(MATH).setScore(80)));
                Mockito.when(scoreRepository.getScore(firstStudentID, scienceID))
                        .thenReturn(Optional.empty());
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    @Test
    void testGetAverageScore_HappyCase_VerifyReturnedValue_Success() {
        // given
        int testCase = 1;
        injectionTestEnv(testCase);
        int studentID = trey.getId();

        // when
        final double averageScore = transcriptService.getAverageScore(studentID);
        // then
        Assertions.assertEquals(85.0, averageScore);
    }

    @Test
    @DisplayName("주어진 studentID에 해당되는 Student가 없을 때, getAverageScore()는 NoSuchStudentException을 Throw 한다.")
    void testGetAverageScore_StudentNotExist_ThrowNoSuchStudentException_Error() {
        // given
        int studentID = trey.getId();
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
        //given
        int testCase = 1;
        injectionTestEnv(testCase);
        int studentID = trey.getId();

        //when
        transcriptService.getAverageScore(studentID);

        //then
        Mockito.verify(studentRepository,Mockito.times(1)).getStudent(studentID);
        Mockito.verify(scoreRepository,Mockito.times(4)).getScore(Mockito.anyInt(),Mockito.anyInt());

        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    @DisplayName("scoreRepository로부터 학생의 Score를 하나라도 찾을 수 없는 경우, getAverageScore()는 NoSuchScoreException을 Throw 한다.")
    void testGetAverageScore_ScoreNotExist_ThrowNoSuchScoreException_Error() {
        // TODO:
        //given
        int studentID = trey.getId();
        int testCase = 2;
        injectionTestEnv(testCase);

        // when & then
        Assertions.assertThrows(NoSuchScoreException.class, () -> transcriptService.getAverageScore(studentID));
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    @DisplayName("getRankedStudentsAsc()를 호출하면, 입력으로 주어진 course를 수강하는 모든 학생들의 리스트를 성적의 내림차순으로 리턴한다.")
    void testGetRankedStudentsAsc_HappyCase_VerifyReturnedValueAndInteractions_Success() {
        // TODO:

    }

    @Test
    @DisplayName("courseRepository에서 입력으로 주어진 courseID로 course를 조회할 수 없으면 NoSuchCourseException을 Throw 한다.")
    void testGetRankedStudentsAsc_CourseNotExist_ThrowNoSuchCourseException_Error() {
        // TODO:
        // given
        Mockito.when(courseRepository.getCourse(koreanID)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(NoSuchCourseException.class, () -> transcriptService.getRankedStudentsAsc(koreanID));
    }
}
