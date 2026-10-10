package com.example.OnlineExam.service;

import com.example.OnlineExam.model.Result;
import com.example.OnlineExam.model.StudentReport;
import com.example.OnlineExam.repository.ExamRepository;
import com.example.OnlineExam.repository.ResultRepository;
import com.example.OnlineExam.repository.StudentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StudentReportService {
    private final StudentReportRepository reportRepository;
    private final ResultRepository resultRepository;
    private final ExamRepository examRepository;

    public void updateReport(Result result) {
        Long userId = result.getUser().getUserId();
        Long subjectId = result.getExam().getSubject().getSubjectId();

        StudentReport report = reportRepository.findByUserUserIdAndSubjectSubjectId(userId, subjectId)
                .orElseGet(() -> {
                    StudentReport r = new StudentReport();
                    r.setUser(result.getUser());
                    r.setSubject(result.getExam().getSubject());
                    return r;
                });

        var results = resultRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(r -> r.getExam().getSubject().getSubjectId().equals(subjectId)).toList();

        report.setTotalExams(examRepository.findBySubjectSubjectId(subjectId).size());
        report.setTotalAttempted(results.size());
        BigDecimal totalScore = results.stream().map(Result::getScore).reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setTotalScore(totalScore);
        report.setAverageScore(results.isEmpty() ? BigDecimal.ZERO : totalScore.divide(BigDecimal.valueOf(results.size()), 2, RoundingMode.HALF_UP));
        BigDecimal totalPercentage = results.stream().map(Result::getPercentage).reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setAveragePercentage(results.isEmpty() ? BigDecimal.ZERO : totalPercentage.divide(BigDecimal.valueOf(results.size()), 2, RoundingMode.HALF_UP));
        reportRepository.save(report);
    }
}
