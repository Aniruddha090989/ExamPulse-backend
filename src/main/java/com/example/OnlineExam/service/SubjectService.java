package com.example.OnlineExam.service;

import com.example.OnlineExam.model.Exam;
import com.example.OnlineExam.model.Subject;
import com.example.OnlineExam.repository.ExamRepository;
import com.example.OnlineExam.repository.QuizRepository;
import com.example.OnlineExam.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    private final QuizRepository quizRepository;

    private final ExamRepository examRepository;


    // =========================================================
    // GET ALL SUBJECTS
    // =========================================================

    public List<Subject> getAll() {

        return subjectRepository.findAll();

    }


    // =========================================================
    // GET SUBJECT BY ID
    // =========================================================

    public Subject getById(Long id) {

        return subjectRepository.findById(id)

                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Subject not found"
                        )
                );

    }


    // =========================================================
    // CREATE SUBJECT
    // =========================================================

    public Subject create(Subject subject) {

        // -----------------------------------------------------
        // Validate subject name
        // -----------------------------------------------------

        if (
                subject.getSubjectName() == null
                        ||
                        subject.getSubjectName()
                                .trim()
                                .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Subject name is required"
            );

        }


        String subjectName =
                subject.getSubjectName()
                        .trim();


        // -----------------------------------------------------
        // Check duplicate
        // -----------------------------------------------------

        if (
                subjectRepository
                        .findBySubjectName(subjectName)
                        .isPresent()
        ) {

            throw new IllegalArgumentException(
                    "Subject name already exists"
            );

        }


        // -----------------------------------------------------
        // Prepare subject
        // -----------------------------------------------------

        subject.setSubjectId(null);

        subject.setSubjectName(
                subjectName
        );


        if (
                subject.getCategory() != null
        ) {

            subject.setCategory(
                    subject.getCategory().trim()
            );

        }


        if (
                subject.getTotalQuestions() == null
        ) {

            subject.setTotalQuestions(0);

        }


        // -----------------------------------------------------
        // Save to database
        // -----------------------------------------------------

        return subjectRepository.save(subject);

    }


    // =========================================================
    // DELETE SUBJECT
    // =========================================================

    @Transactional
    public void delete(Long subjectId) {

        // -----------------------------------------------------
        // Find subject
        // -----------------------------------------------------

        Subject subject =
                subjectRepository.findById(subjectId)

                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subject not found with id: "
                                                + subjectId
                                )
                        );


        // -----------------------------------------------------
        // Check questions
        //
        // IMPORTANT:
        //
        // quiz.subject_id -> subject.subject_id
        // ON DELETE CASCADE
        //
        // Therefore we DO NOT allow deletion of a subject
        // which still contains questions.
        // -----------------------------------------------------

        long questionCount =
                quizRepository.countBySubjectSubjectId(
                        subjectId
                );


        if (questionCount > 0) {

            throw new IllegalStateException(

                    "Cannot delete subject '" +
                            subject.getSubjectName() +
                            "'. It contains " +
                            questionCount +
                            " question(s). " +
                            "Remove those questions first."

            );

        }


        // -----------------------------------------------------
        // Check examinations
        //
        // exam.subject_id -> subject.subject_id
        // ON DELETE RESTRICT
        //
        // Therefore deletion is blocked if examinations
        // use this subject.
        // -----------------------------------------------------

        List<Exam> exams =
                examRepository.findBySubjectSubjectId(
                        subjectId
                );


        if (!exams.isEmpty()) {

            throw new IllegalStateException(

                    "Cannot delete subject '" +
                            subject.getSubjectName() +
                            "'. It is used by " +
                            exams.size() +
                            " examination(s). " +
                            "Delete those examinations first."

            );

        }


        // -----------------------------------------------------
        // Delete subject
        // -----------------------------------------------------

        subjectRepository.delete(subject);

    }

}