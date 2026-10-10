package com.example.OnlineExam.service;

import java.time.ZoneId;

import com.example.OnlineExam.dto.ExamRequest;
import com.example.OnlineExam.dto.ExamResponse;
import com.example.OnlineExam.model.Exam;
import com.example.OnlineExam.model.ExamAttempt;
import com.example.OnlineExam.model.Quiz;
import com.example.OnlineExam.model.Subject;
import com.example.OnlineExam.repository.ExamAttemptRepository;
import com.example.OnlineExam.repository.ExamRepository;
import com.example.OnlineExam.repository.QuizRepository;
import com.example.OnlineExam.repository.ResultRepository;
import com.example.OnlineExam.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    private final SubjectRepository subjectRepository;

    private final QuizRepository quizRepository;

    private final QuestionService questionService;

    private final ResultRepository resultRepository;

    private final ExamAttemptRepository examAttemptRepository;


    // =========================================================
    // GET ALL EXAMS
    // =========================================================

    public List<ExamResponse> getAll() {

        return examRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // GET AVAILABLE EXAMS
    // =========================================================

    public List<ExamResponse> getAvailable() {

        LocalDateTime now =
        LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        return examRepository.findAll()
                .stream()
                .filter(e ->
                        (e.getStartTime() == null
                                || !now.isBefore(e.getStartTime()))
                                &&
                                (e.getEndTime() == null
                                        || !now.isAfter(e.getEndTime()))
                )
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // GET EXAM BY ID
    // =========================================================

    public ExamResponse getById(Long id) {

        return toResponse(
                examRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Exam not found"
                                )
                        )
        );
    }


    // =========================================================
    // CREATE EXAM
    // =========================================================

    public Exam create(ExamRequest request) {

        Subject subject =
                subjectRepository.findById(request.subjectId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subject not found"
                                )
                        );


        List<Quiz> questions =
                request.questionIds()
                        .stream()
                        .map(id ->
                                quizRepository.findById(id)
                                        .orElseThrow(() ->
                                                new IllegalArgumentException(
                                                        "Question not found: "
                                                                + id
                                                )
                                        )
                        )
                        .toList();


        Exam exam = new Exam();

        exam.setTitle(request.title());

        exam.setSubject(subject);

        exam.setDurationMinutes(
                request.durationMinutes()
        );

        exam.setTotalQuestions(
                questions.size()
        );

        exam.setStartTime(
                request.startTime()
        );

        exam.setEndTime(
                request.endTime()
        );

        exam.setQuestions(
                questions
        );


        return examRepository.save(exam);
    }


    // =========================================================
    // UPDATE EXAM
    // =========================================================

    public Exam update(
            Long id,
            ExamRequest request
    ) {

        Exam exam =
                examRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Exam not found"
                                )
                        );


        Subject subject =
                subjectRepository.findById(
                                request.subjectId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subject not found"
                                )
                        );


        List<Quiz> questions =
                request.questionIds()
                        .stream()
                        .map(questionId ->
                                quizRepository.findById(
                                                questionId
                                        )
                                        .orElseThrow(() ->
                                                new IllegalArgumentException(
                                                        "Question not found: "
                                                                + questionId
                                                )
                                        )
                        )
                        .toList();


        exam.setTitle(
                request.title()
        );

        exam.setSubject(
                subject
        );

        exam.setDurationMinutes(
                request.durationMinutes()
        );

        exam.setTotalQuestions(
                questions.size()
        );

        exam.setStartTime(
                request.startTime()
        );

        exam.setEndTime(
                request.endTime()
        );


        // Keep Hibernate-managed collection
        exam.getQuestions().clear();

        exam.getQuestions().addAll(
                questions
        );


        return examRepository.save(exam);
    }


    // =========================================================
    // DELETE EXAM
    // =========================================================

    @Transactional
    public void delete(Long examId) {

        // -----------------------------------------------------
        // 1. Find exam
        // -----------------------------------------------------

        Exam exam =
                examRepository.findById(examId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Exam not found with id: "
                                                + examId
                                )
                        );


        // -----------------------------------------------------
        // 2. Delete results first
        //
        // result.exam -> exam
        //
        // Therefore Result must be deleted before Exam.
        // -----------------------------------------------------

        resultRepository.deleteByExamId(
                examId
        );


        // -----------------------------------------------------
        // 3. Delete exam attempts
        //
        // ExamAttempt has:
        //
        // exam_id -> exam
        //
        // and Answer records depend on ExamAttempt.
        //
        // ExamAttempt.answers has CascadeType.ALL +
        // orphanRemoval, so deleting attempts also removes
        // their answers.
        // -----------------------------------------------------

        List<ExamAttempt> attempts =
                examAttemptRepository.findByExamExamId(
                        examId
                );


        if (!attempts.isEmpty()) {

            examAttemptRepository.deleteAll(
                    attempts
            );

        }


        // -----------------------------------------------------
        // 4. Delete exam_questions relationships
        //
        // This DOES NOT delete Quiz questions.
        //
        // It only removes:
        //
        // exam <-> question
        //
        // associations.
        // -----------------------------------------------------

        examRepository.deleteExamQuestionLinks(
                examId
        );


        // -----------------------------------------------------
        // 5. Finally delete the exam itself
        // -----------------------------------------------------

        examRepository.delete(
                exam
        );
    }


    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    public ExamResponse toResponse(
            Exam exam
    ) {

        return new ExamResponse(

                exam.getExamId(),

                exam.getTitle(),

                exam.getSubject().getSubjectId(),

                exam.getSubject().getSubjectName(),

                exam.getDurationMinutes(),

                exam.getTotalQuestions(),

                exam.getStartTime(),

                exam.getEndTime(),

                exam.getQuestions()
                        .stream()
                        .map(questionService::toResponse)
                        .toList()
        );
    }
}
