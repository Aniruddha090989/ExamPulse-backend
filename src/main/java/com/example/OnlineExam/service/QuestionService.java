package com.example.OnlineExam.service;

import com.example.OnlineExam.dto.ChoiceResponse;
import com.example.OnlineExam.dto.QuestionRequest;
import com.example.OnlineExam.dto.QuestionResponse;
import com.example.OnlineExam.model.Quiz;
import com.example.OnlineExam.model.QuizChoice;
import com.example.OnlineExam.model.Subject;
import com.example.OnlineExam.repository.QuizRepository;
import com.example.OnlineExam.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuizRepository quizRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public QuestionResponse create(QuestionRequest request) {

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Subject not found"));

        validateCorrectIndexes(request);

        Quiz quiz = new Quiz();
        quiz.setQuestion(request.question());
        quiz.setType(request.type());
        quiz.setSubject(subject);

        List<QuizChoice> choices = new ArrayList<>();

        for (int i = 0; i < request.choices().size(); i++) {
            QuizChoice choice = new QuizChoice();

            choice.setChoice(request.choices().get(i));
            choice.setIsCorrect(request.correctIndexes().contains(i));
            choice.setQuiz(quiz);

            choices.add(choice);
        }

        quiz.setChoices(choices);

        Quiz saved = quizRepository.save(quiz);

        updateSubjectQuestionCount(subject);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getAll() {
        return quizRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionResponse getById(Long id) {
        return toResponse(
                quizRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Question not found"))
        );
    }

    @Transactional(readOnly = true)
    public Quiz getEntity(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found"));
    }

    @Transactional
    public void delete(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found"));

        Subject subject = quiz.getSubject();

        quizRepository.delete(quiz);

        updateSubjectQuestionCount(subject);
    }

    @Transactional
    public QuestionResponse update(Long id, QuestionRequest request) {

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found"));

        Subject previousSubject = quiz.getSubject();

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Subject not found"));

        validateCorrectIndexes(request);

        // Update the question details.
        quiz.setQuestion(request.question());
        quiz.setType(request.type());
        quiz.setSubject(subject);

        List<QuizChoice> existingChoices = quiz.getChoices();
        List<String> requestedChoices = request.choices();

        int existingCount = existingChoices.size();
        int requestedCount = requestedChoices.size();
        int commonCount = Math.min(existingCount, requestedCount);

        // Update existing choices without changing their IDs.
        for (int i = 0; i < commonCount; i++) {

            QuizChoice choice = existingChoices.get(i);

            choice.setChoice(requestedChoices.get(i));
            choice.setIsCorrect(
                    request.correctIndexes().contains(i)
            );
        }

        // Add new choices if the request contains more choices.
        for (int i = existingCount; i < requestedCount; i++) {

            QuizChoice choice = new QuizChoice();

            choice.setChoice(requestedChoices.get(i));
            choice.setIsCorrect(
                    request.correctIndexes().contains(i)
            );
            choice.setQuiz(quiz);

            existingChoices.add(choice);
        }

        /*
         * Existing surplus choices are deliberately not deleted because
         * answer records may reference their IDs.
         */

        Quiz saved = quizRepository.save(quiz);

        updateSubjectQuestionCount(subject);

        // If the question was moved to another subject, refresh both counts.
        if (!previousSubject.getSubjectId().equals(subject.getSubjectId())) {
            updateSubjectQuestionCount(previousSubject);
        }

        return toResponse(saved);
    }

    private void validateCorrectIndexes(QuestionRequest request) {

        if (request.choices() == null || request.choices().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one choice is required");
        }

        if (request.correctIndexes() == null
                || request.correctIndexes().stream()
                .anyMatch(i -> i < 0 || i >= request.choices().size())) {

            throw new IllegalArgumentException(
                    "Invalid correct answer index");
        }
    }

    private void updateSubjectQuestionCount(Subject subject) {

        subject.setTotalQuestions(
                quizRepository.findBySubjectSubjectId(
                        subject.getSubjectId()
                ).size()
        );

        subjectRepository.save(subject);
    }

    public QuestionResponse toResponse(Quiz quiz) {

        return new QuestionResponse(
                quiz.getQuizId(),
                quiz.getQuestion(),
                quiz.getType(),
                quiz.getSubject().getSubjectId(),
                quiz.getSubject().getSubjectName(),
                quiz.getChoices()
                        .stream()
                        .map(c -> new ChoiceResponse(
                                c.getChoiceId(),
                                c.getChoice(),
                                c.getIsCorrect()
                        ))
                        .toList()
        );
    }
}