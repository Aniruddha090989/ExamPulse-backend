package com.example.OnlineExam.service;

import java.time.ZoneId;

import com.example.OnlineExam.dto.StudentDashboardResponse;
import com.example.OnlineExam.model.Credential;
import com.example.OnlineExam.model.Exam;
import com.example.OnlineExam.model.Result;
import com.example.OnlineExam.repository.CredentialRepository;
import com.example.OnlineExam.repository.ExamAttemptRepository;
import com.example.OnlineExam.repository.ExamRepository;
import com.example.OnlineExam.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final CredentialRepository credentialRepository;
    private final ResultRepository resultRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public StudentDashboardResponse getDashboard(Long userId) {

        Credential user = credentialRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<Result> results =
                resultRepository.findByUserUserIdOrderByCreatedAtDesc(userId);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))

        /*
         * Exams currently available.
         */
        List<Exam> activeExams = examRepository.findAll()
                .stream()
                .filter(exam ->
                        (exam.getStartTime() == null ||
                                !now.isBefore(exam.getStartTime()))
                                &&
                                (exam.getEndTime() == null ||
                                        !now.isAfter(exam.getEndTime()))
                )
                .toList();

        /*
         * Number of completed exams.
         */
        long examsTaken = results.size();

        /*
         * Average percentage.
         */
        BigDecimal averagePercentage;

        if (results.isEmpty()) {
            averagePercentage = BigDecimal.ZERO;
        } else {
            BigDecimal totalPercentage = results.stream()
                    .map(Result::getPercentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            averagePercentage = totalPercentage
                    .divide(
                            BigDecimal.valueOf(results.size()),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        /*
         * Total questions attempted.
         */
        long attemptedQuestions = results.stream()
                .mapToLong(result ->
                        result.getAttemptedQuestions() == null
                                ? 0
                                : result.getAttemptedQuestions()
                )
                .sum();

        /*
         * Active + upcoming exams.
         *
         * We return all exams that have not ended.
         */
        List<Exam> dashboardExams = examRepository.findAll()
                .stream()
                .filter(exam ->
                        exam.getEndTime() == null ||
                                !now.isAfter(exam.getEndTime())
                )
                .sorted((a, b) -> {

                    if (a.getStartTime() == null) return -1;
                    if (b.getStartTime() == null) return 1;

                    return a.getStartTime()
                            .compareTo(b.getStartTime());
                })
                .toList();

        List<StudentDashboardResponse.DashboardExamResponse> examResponses =
                dashboardExams.stream()
                        .map(exam ->
                                new StudentDashboardResponse.DashboardExamResponse(
                                        exam.getExamId(),
                                        exam.getTitle(),
                                        exam.getSubject().getSubjectName(),
                                        exam.getDurationMinutes(),
                                        exam.getTotalQuestions(),
                                        exam.getStartTime(),
                                        exam.getEndTime()
                                )
                        )
                        .toList();

        /*
         * Recent results.
         */
        List<StudentDashboardResponse.DashboardResultResponse> resultResponses =
                results.stream()
                        .limit(5)
                        .map(result ->
                                new StudentDashboardResponse.DashboardResultResponse(
                                        result.getResultId(),
                                        result.getAttempt().getAttemptId(),
                                        result.getExam().getTitle(),
                                        result.getPercentage(),
                                        result.getAttempt().getSubmitAt()
                                )
                        )
                        .toList();

        return new StudentDashboardResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                examsTaken,
                averagePercentage,
                activeExams.size(),
                attemptedQuestions,
                examResponses,
                resultResponses
        );
    }
}
