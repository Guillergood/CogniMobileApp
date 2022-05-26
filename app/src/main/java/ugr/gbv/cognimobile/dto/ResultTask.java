package ugr.gbv.cognimobile.dto;

import java.util.List;


public class ResultTask {
    private Long id;
    private List<Integer> patternSequence;
    private TaskType taskType;
    private int height;
    private int width;
    private List<Double> pointsSequence;
    private List<String> answerSequence;
    private int score;
    private List<Double> erasedPaths;
    private int times_wipe_canvas;
    private List<String> expectedAnswers;
    private List<String> answer;
    private List<String> answerBackwards;
    private List<String> expectedAnswerBackwards;
    private List<String> letters;
    private int occurrences;
    private String target_letter;
    private int errors;
    private List<String> words;
    private List<String> expectedAnswer;
    private List<String> questions;

    public ResultTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Integer> getPatternSequence() {
        return patternSequence;
    }

    public void setPatternSequence(List<Integer> patternSequence) {
        this.patternSequence = patternSequence;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<Double> getPointsSequence() {
        return pointsSequence;
    }

    public void setPointsSequence(List<Double> pointsSequence) {
        this.pointsSequence = pointsSequence;
    }

    public List<String> getAnswerSequence() {
        return answerSequence;
    }

    public void setAnswerSequence(List<String> answerSequence) {
        this.answerSequence = answerSequence;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Double> getErasedPaths() {
        return erasedPaths;
    }

    public void setErasedPaths(List<Double> erasedPaths) {
        this.erasedPaths = erasedPaths;
    }

    public int getTimes_wipe_canvas() {
        return times_wipe_canvas;
    }

    public void setTimes_wipe_canvas(int times_wipe_canvas) {
        this.times_wipe_canvas = times_wipe_canvas;
    }

    public List<String> getExpectedAnswers() {
        return expectedAnswers;
    }

    public void setExpectedAnswers(List<String> expectedAnswers) {
        this.expectedAnswers = expectedAnswers;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public List<String> getAnswerBackwards() {
        return answerBackwards;
    }

    public void setAnswerBackwards(List<String> answerBackwards) {
        this.answerBackwards = answerBackwards;
    }

    public List<String> getExpectedAnswerBackwards() {
        return expectedAnswerBackwards;
    }

    public void setExpectedAnswerBackwards(List<String> expectedAnswerBackwards) {
        this.expectedAnswerBackwards = expectedAnswerBackwards;
    }

    public List<String> getLetters() {
        return letters;
    }

    public void setLetters(List<String> letters) {
        this.letters = letters;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public String getTarget_letter() {
        return target_letter;
    }

    public void setTarget_letter(String target_letter) {
        this.target_letter = target_letter;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<String> getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(List<String> expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
}


