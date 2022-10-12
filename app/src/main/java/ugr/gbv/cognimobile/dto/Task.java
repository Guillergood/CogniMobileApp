package ugr.gbv.cognimobile.dto;

import java.util.List;


public class Task {
    private Long id;

    private TaskType taskType;

    private String hour;
    
    private List<String> answer;

    private List<String> images;

    private String times;
    
    private List<String> words;
    
    private List<Integer> numbers_forward;
    
    private List<Integer> numbers_backward;

    private List<String> letters;

    private String target_letter;

    private int minuend;

    private int subtracting;

    private List<String> phrases;

    private int number_words;
    
    private List<String> questions;

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<Integer> getNumbers_forward() {
        return numbers_forward;
    }

    public void setNumbers_forward(List<Integer> numbers_forward) {
        this.numbers_forward = numbers_forward;
    }

    public List<Integer> getNumbers_backward() {
        return numbers_backward;
    }

    public void setNumbers_backward(List<Integer> numbers_backward) {
        this.numbers_backward = numbers_backward;
    }

    public List<String> getLetters() {
        return letters;
    }

    public void setLetters(List<String> letters) {
        this.letters = letters;
    }

    public String getTarget_letter() {
        return target_letter;
    }

    public void setTarget_letter(String target_letter) {
        this.target_letter = target_letter;
    }

    public int getMinuend() {
        return minuend;
    }

    public void setMinuend(int minuend) {
        this.minuend = minuend;
    }

    public int getSubtracting() {
        return subtracting;
    }

    public void setSubtracting(int subtracting) {
        this.subtracting = subtracting;
    }

    public List<String> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<String> phrases) {
        this.phrases = phrases;
    }

    public int getNumber_words() {
        return number_words;
    }

    public void setNumber_words(int number_words) {
        this.number_words = number_words;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
}
