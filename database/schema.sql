CREATE DATABASE IF NOT EXISTS `online-exam`;
USE `online-exam`;

CREATE TABLE IF NOT EXISTS credentials (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subject (
  subject_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  subject_name VARCHAR(100) NOT NULL UNIQUE,
  category VARCHAR(100),
  total_questions INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS quiz (
  quiz_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  question TEXT NOT NULL,
  type ENUM('MCQ','MULTIPLE_CHOICE','TRUE_FALSE') NOT NULL,
  subject_id BIGINT NOT NULL,
  CONSTRAINT fk_quiz_subject FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_choices (
  choice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  quiz_id BIGINT NOT NULL,
  choice TEXT NOT NULL,
  is_correct BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_choice_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS exam (
  exam_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  subject_id BIGINT NOT NULL,
  duration_minutes INT NOT NULL,
  total_questions INT NOT NULL,
  start_time DATETIME NULL,
  end_time DATETIME NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_exam_subject FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS exam_questions (
  exam_id BIGINT NOT NULL,
  quiz_id BIGINT NOT NULL,
  PRIMARY KEY (exam_id, quiz_id),
  CONSTRAINT fk_exam_questions_exam FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_exam_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS exam_attempt (
  attempt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  exam_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  start_at DATETIME NOT NULL,
  submit_at DATETIME NULL,
  status ENUM('IN_PROGRESS','SUBMITTED','AUTO_SUBMITTED','EXPIRED') NOT NULL DEFAULT 'IN_PROGRESS',
  CONSTRAINT fk_attempt_exam FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_attempt_user FOREIGN KEY (user_id) REFERENCES credentials(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS answer (
  answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  attempt_id BIGINT NOT NULL,
  quiz_id BIGINT NOT NULL,
  selected_choice_id BIGINT NULL,
  is_correct BOOLEAN NOT NULL DEFAULT FALSE,
  marks_obtained DECIMAL(5,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_answer_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempt(attempt_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_answer_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_answer_choice FOREIGN KEY (selected_choice_id) REFERENCES quiz_choices(choice_id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT uq_attempt_question UNIQUE (attempt_id, quiz_id)
);

CREATE TABLE IF NOT EXISTS result (
  result_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  attempt_id BIGINT NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  exam_id BIGINT NOT NULL,
  total_questions INT NOT NULL,
  attempted_questions INT NOT NULL DEFAULT 0,
  correct_answers INT NOT NULL DEFAULT 0,
  wrong_answers INT NOT NULL DEFAULT 0,
  score DECIMAL(6,2) NOT NULL DEFAULT 0,
  percentage DECIMAL(5,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_result_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempt(attempt_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_result_user FOREIGN KEY (user_id) REFERENCES credentials(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_result_exam FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS student_report (
  report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  total_exams INT NOT NULL DEFAULT 0,
  total_attempted INT NOT NULL DEFAULT 0,
  total_score DECIMAL(10,2) NOT NULL DEFAULT 0,
  average_score DECIMAL(6,2) NOT NULL DEFAULT 0,
  average_percentage DECIMAL(5,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES credentials(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_report_subject FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT uq_student_subject_report UNIQUE (user_id, subject_id)
);
