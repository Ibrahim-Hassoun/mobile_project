package lb.edu.ul.project.Domain;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    private String question;
    private List<String> options;
    private int correctAnswerIndex;
    private String category;

    public QuizQuestion() {
    }

    public QuizQuestion(String question, List<String> options, int correctAnswerIndex, String category) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
