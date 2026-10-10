package com.example.OnlineExam.service;


import java.time.ZoneId;

import com.example.OnlineExam.dto.ExaminerDashboardExamResponse;
import com.example.OnlineExam.dto.ExaminerDashboardResponse;
import com.example.OnlineExam.dto.ExaminerStudentResultResponse;
import com.example.OnlineExam.model.Exam;
import com.example.OnlineExam.model.Result;
import com.example.OnlineExam.repository.ExamRepository;
import com.example.OnlineExam.repository.QuizRepository;
import com.example.OnlineExam.repository.ResultRepository;
import com.example.OnlineExam.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminerDashboardService {

    private final SubjectRepository subjectRepository;
    private final QuizRepository quizRepository;
    private final ExamRepository examRepository;
    private final ResultRepository resultRepository;


    // =========================================================
    // EXAMINER DASHBOARD
    // =========================================================

    public ExaminerDashboardResponse getDashboardData() {

        long managedSubjects = subjectRepository.count();

        long totalQuestions = quizRepository.count();

        List<Exam> exams = examRepository.findAll();

        LocalDateTime now =
        LocalDateTime.now(ZoneId.of("Asia/Kolkata"));)

        long activeExams = exams.stream()
                .filter(exam ->
                        (exam.getStartTime() == null
                                || !now.isBefore(exam.getStartTime()))
                                &&
                                (exam.getEndTime() == null
                                        || !now.isAfter(exam.getEndTime()))
                )
                .count();

        long evaluatedStudents =
                resultRepository.countDistinctStudents();

        List<ExaminerDashboardExamResponse> examResponses =
                exams.stream()
                        .map(exam -> new ExaminerDashboardExamResponse(
                                exam.getExamId(),
                                exam.getTitle(),
                                exam.getSubject().getSubjectName(),
                                exam.getTotalQuestions(),
                                exam.getDurationMinutes(),
                                exam.getStartTime(),
                                exam.getEndTime(),
                                getExamStatus(exam, now)
                        ))
                        .toList();

        return new ExaminerDashboardResponse(
                managedSubjects,
                totalQuestions,
                activeExams,
                evaluatedStudents,
                examResponses
        );
    }


    // =========================================================
    // STUDENT RESULTS FOR A PARTICULAR EXAM
    // =========================================================

    public List<ExaminerStudentResultResponse> getStudentResults(
            Long examId
    ) {

        List<Result> results =
                resultRepository.findByExamExamIdOrderByCreatedAtDesc(
                        examId
                );

        return results.stream()
                .map(result -> {

                    var student = result.getUser();

                    return new ExaminerStudentResultResponse(

                            result.getResultId(),

                            result.getExam().getExamId(),

                            student.getUserId(),

                            student.getUsername(),

                            student.getEmail(),

                            result.getTotalQuestions(),

                            result.getAttemptedQuestions(),

                            result.getCorrectAnswers(),

                            result.getWrongAnswers(),

                            result.getScore(),

                            result.getPercentage(),

                            result.getCreatedAt()

                    );

                })
                .toList();
    }


    // =========================================================
    // EXAM STATUS
    // =========================================================

    private String getExamStatus(
            Exam exam,
            LocalDateTime now
    ) {

        if (exam.getStartTime() == null
                && exam.getEndTime() == null) {

            return "Draft";
        }

        if (exam.getStartTime() != null
                && now.isBefore(exam.getStartTime())) {

            return "Scheduled";
        }

        if (exam.getEndTime() != null
                && now.isAfter(exam.getEndTime())) {

            return "Completed";
        }

        return "Live / Active";
    }
}
