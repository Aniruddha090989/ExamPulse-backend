package com.example.OnlineExam.service;


import java.time.ZoneId;
import com.example.OnlineExam.dto.AnswerRequest;
import com.example.OnlineExam.dto.ResultResponse;
import com.example.OnlineExam.model.*;
import com.example.OnlineExam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttemptService {
    private final ExamAttemptRepository attemptRepository;
    private final CredentialRepository credentialRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final QuizRepository quizRepository;
    private final QuizChoiceRepository choiceRepository;
    private final ResultRepository resultRepository;
    private final StudentReportService studentReportService;

    @Transactional
    public ExamAttempt start(Long examId, Long userId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Exam not found"));

        Credential user = credentialRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        /*
         * ---------------------------------------------------------
         * CHECK IF STUDENT ALREADY HAS AN ACTIVE ATTEMPT
         * ---------------------------------------------------------
         */

        Optional<ExamAttempt> existingAttempt =
                attemptRepository
                        .findFirstByExamExamIdAndUserUserIdAndStatusOrderByStartAtDesc(
                                examId,
                                userId,
                                AttemptStatus.IN_PROGRESS
                        );

        if (existingAttempt.isPresent()) {

            ExamAttempt attempt = existingAttempt.get();

            /*
             * Check whether the existing attempt has expired.
             */

            LocalDateTime deadline =
                    attempt.getStartAt()
                            .plusMinutes(exam.getDurationMinutes());

            if (LocalDateTime.now().isAfter(deadline)) {

                submit(
                        attempt.getAttemptId(),
                        userId,
                        true
                );

                throw new IllegalArgumentException(
                        "Exam time has expired"
                );
            }

            /*
             * Existing valid attempt.
             * Return it instead of creating another one.
             */

            return attempt;
        }

        /*
         * ---------------------------------------------------------
         * CHECK EXAM AVAILABILITY
         * ---------------------------------------------------------
         */

        LocalDateTime now =LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        if (exam.getStartTime() != null
                && now.isBefore(exam.getStartTime())) {

            throw new IllegalArgumentException(
                    "Exam has not started yet"
            );
        }

        if (exam.getEndTime() != null
                && now.isAfter(exam.getEndTime())) {

            throw new IllegalArgumentException(
                    "Exam is no longer available"
            );
        }

        /*
         * ---------------------------------------------------------
         * CREATE NEW ATTEMPT
         * ---------------------------------------------------------
         */

        ExamAttempt attempt = new ExamAttempt();

        attempt.setExam(exam);
        attempt.setUser(user);
        attempt.setStartAt(now);
        attempt.setStatus(
                AttemptStatus.IN_PROGRESS
        );

        return attemptRepository.save(attempt);
    }

    @Transactional
    public Answer saveAnswer(Long attemptId, Long userId, AnswerRequest request) {
        ExamAttempt attempt = getAttempt(attemptId, userId);
        ensureInProgress(attempt);
        checkExpired(attempt);

        Quiz quiz = quizRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        QuizChoice choice = request.choiceId() == null ? null : choiceRepository.findById(request.choiceId())
                .orElseThrow(() -> new IllegalArgumentException("Choice not found"));

        if (attempt.getExam().getQuestions().stream().noneMatch(q -> q.getQuizId().equals(quiz.getQuizId()))) {
            throw new IllegalArgumentException("Question does not belong to this exam");
        }
        if (choice != null && !choice.getQuiz().getQuizId().equals(quiz.getQuizId())) {
            throw new IllegalArgumentException("Choice does not belong to this question");
        }

        Answer answer = answerRepository.findByAttemptAttemptIdAndQuizQuizId(attemptId, quiz.getQuizId())
                .orElseGet(Answer::new);
        answer.setAttempt(attempt);
        answer.setQuiz(quiz);
        answer.setSelectedChoice(choice);
        answer.setIsCorrect(false);
        answer.setMarksObtained(BigDecimal.ZERO);
        return answerRepository.save(answer);
    }

    @Transactional
    public ResultResponse submit(Long attemptId, Long userId, boolean auto) {
        ExamAttempt attempt = getAttempt(attemptId, userId);
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            Result existing = resultRepository.findByAttemptAttemptId(attemptId)
                    .orElseThrow(() -> new IllegalArgumentException("Attempt already submitted"));
            return toResponse(existing);
        }

        List<Answer> answers = answerRepository.findAll().stream()
                .filter(a -> a.getAttempt().getAttemptId().equals(attemptId)).toList();

        int attempted = answers.size();
        int correct = 0;
        BigDecimal score = BigDecimal.ZERO;
        for (Answer answer : answers) {
            boolean isCorrect = answer.getSelectedChoice() != null && Boolean.TRUE.equals(answer.getSelectedChoice().getIsCorrect());
            answer.setIsCorrect(isCorrect);
            answer.setMarksObtained(isCorrect ? BigDecimal.ONE : BigDecimal.ZERO);
            if (isCorrect) {
                correct++;
                score = score.add(BigDecimal.ONE);
            }
            answerRepository.save(answer);
        }

        int total = attempt.getExam().getTotalQuestions();
        int wrong = attempted - correct;
        BigDecimal percentage = total == 0 ? BigDecimal.ZERO :
                score.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        attempt.setSubmitAt(LocalDateTime.now());
        attempt.setStatus(auto ? AttemptStatus.AUTO_SUBMITTED : AttemptStatus.SUBMITTED);
        attemptRepository.save(attempt);

        Result result = new Result();
        result.setAttempt(attempt);
        result.setUser(attempt.getUser());
        result.setExam(attempt.getExam());
        result.setTotalQuestions(total);
        result.setAttemptedQuestions(attempted);
        result.setCorrectAnswers(correct);
        result.setWrongAnswers(wrong);
        result.setScore(score);
        result.setPercentage(percentage);
        result = resultRepository.save(result);

        studentReportService.updateReport(result);
        return toResponse(result);
    }

    public List<ResultResponse> getResults(Long userId) {
        return resultRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    public ResultResponse getResult(Long attemptId, Long userId) {
        Result result = resultRepository.findByAttemptAttemptId(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));
        if (!result.getUser().getUserId().equals(userId)) throw new IllegalArgumentException("Access denied");
        return toResponse(result);
    }

    private ExamAttempt getAttempt(Long attemptId, Long userId) {
        return attemptRepository.findByAttemptIdAndUserUserId(attemptId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
    }

    private void ensureInProgress(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) throw new IllegalArgumentException("Attempt is already submitted");
    }

    private void checkExpired(ExamAttempt attempt) {
        LocalDateTime deadline = attempt.getStartAt().plusMinutes(attempt.getExam().getDurationMinutes());
        if (LocalDateTime.now().isAfter(deadline)) {
            submit(attempt.getAttemptId(), attempt.getUser().getUserId(), true);
            throw new IllegalArgumentException("Exam time has expired");
        }
    }

    private ResultResponse toResponse(Result result) {
        return new ResultResponse(result.getResultId(), result.getAttempt().getAttemptId(), result.getExam().getTitle(),
                result.getTotalQuestions(), result.getAttemptedQuestions(), result.getCorrectAnswers(),
                result.getWrongAnswers(), result.getScore(), result.getPercentage(), result.getAttempt().getSubmitAt());
    }




    public boolean hasAlreadyAttempted(Long examId, Long userId) {

        return resultRepository.existsByUserUserIdAndExamExamId(
                userId,
                examId
        );
    }

    public Result getPreviousResult(Long examId, Long userId) {

        return resultRepository
                .findByUserUserIdAndExamExamId(userId, examId)
                .orElse(null);
    }
}
