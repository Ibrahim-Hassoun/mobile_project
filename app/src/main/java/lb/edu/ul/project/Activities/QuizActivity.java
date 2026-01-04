package lb.edu.ul.project.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lb.edu.ul.project.Domain.QuizQuestion;
import lb.edu.ul.project.R;

public class QuizActivity extends AppCompatActivity {
    private TextView questionNumberText;
    private TextView questionText;
    private TextView scoreText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private ProgressBar progressBar;
    
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedAnswerIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Movie Trivia Quiz");
        }

        initViews();
        loadQuestions();
        displayQuestion();

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        questionNumberText = findViewById(R.id.questionNumberText);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.quizProgressBar);

        nextButton.setOnClickListener(v -> handleNextButton());

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    selectedAnswerIndex = i;
                    break;
                }
            }
        });
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        
        questions.add(new QuizQuestion(
            "Which movie won the Academy Award for Best Picture in 2020?",
            Arrays.asList("1917", "Joker", "Parasite", "Once Upon a Time in Hollywood"),
            2, "Awards"
        ));

        questions.add(new QuizQuestion(
            "Who directed 'The Shawshank Redemption'?",
            Arrays.asList("Steven Spielberg", "Frank Darabont", "Martin Scorsese", "Christopher Nolan"),
            1, "Directors"
        ));

        questions.add(new QuizQuestion(
            "In which year was the first 'Star Wars' movie released?",
            Arrays.asList("1975", "1977", "1979", "1980"),
            1, "Classics"
        ));

        questions.add(new QuizQuestion(
            "What is the highest-grossing movie of all time (unadjusted)?",
            Arrays.asList("Titanic", "Avatar", "Avengers: Endgame", "Avatar: The Way of Water"),
            1, "Box Office"
        ));

        questions.add(new QuizQuestion(
            "Which actor played Iron Man in the Marvel Cinematic Universe?",
            Arrays.asList("Chris Evans", "Robert Downey Jr.", "Chris Hemsworth", "Mark Ruffalo"),
            1, "Actors"
        ));

        questions.add(new QuizQuestion(
            "What is the name of the fictional African country in 'Black Panther'?",
            Arrays.asList("Zamunda", "Wakanda", "Genovia", "Latveria"),
            1, "Movies"
        ));

        questions.add(new QuizQuestion(
            "Who composed the iconic score for 'Star Wars'?",
            Arrays.asList("Hans Zimmer", "John Williams", "Ennio Morricone", "Howard Shore"),
            1, "Music"
        ));

        questions.add(new QuizQuestion(
            "In 'The Matrix', what color pill does Neo take?",
            Arrays.asList("Blue", "Red", "Green", "Yellow"),
            1, "Sci-Fi"
        ));

        questions.add(new QuizQuestion(
            "Which movie features the quote 'Here's looking at you, kid'?",
            Arrays.asList("Gone with the Wind", "The Maltese Falcon", "Casablanca", "Citizen Kane"),
            2, "Quotes"
        ));

        questions.add(new QuizQuestion(
            "How many 'Lord of the Rings' movies are there?",
            Arrays.asList("2", "3", "4", "5"),
            1, "Trilogies"
        ));

        Collections.shuffle(questions);
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showResults();
            return;
        }

        QuizQuestion question = questions.get(currentQuestionIndex);
        
        questionNumberText.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
        questionText.setText(question.getQuestion());
        scoreText.setText("Score: " + score);
        
        progressBar.setMax(questions.size());
        progressBar.setProgress(currentQuestionIndex);

        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();
        selectedAnswerIndex = -1;

        for (int i = 0; i < question.getOptions().size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(question.getOptions().get(i));
            radioButton.setTextColor(getResources().getColor(R.color.white));
            radioButton.setTextSize(16);
            radioButton.setPadding(16, 16, 16, 16);
            optionsGroup.addView(radioButton);
        }

        nextButton.setText(currentQuestionIndex == questions.size() - 1 ? "Finish" : "Next");
    }

    private void handleNextButton() {
        if (selectedAnswerIndex == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
        
        if (selectedAnswerIndex == currentQuestion.getCorrectAnswerIndex()) {
            score++;
            Toast.makeText(this, "Correct! ✓", Toast.LENGTH_SHORT).show();
        } else {
            String correctAnswer = currentQuestion.getOptions().get(currentQuestion.getCorrectAnswerIndex());
            Toast.makeText(this, "Wrong! Correct answer: " + correctAnswer, Toast.LENGTH_LONG).show();
        }

        currentQuestionIndex++;
        
        new android.os.Handler().postDelayed(() -> displayQuestion(), 1000);
    }

    private void showResults() {
        String message = "Quiz Complete!\n\nYour Score: " + score + "/" + questions.size();
        int percentage = (score * 100) / questions.size();
        
        String rating;
        if (percentage >= 90) {
            rating = "🎉 Outstanding! Movie Expert!";
        } else if (percentage >= 70) {
            rating = "👏 Great job! Movie Buff!";
        } else if (percentage >= 50) {
            rating = "👍 Good effort! Keep watching!";
        } else {
            rating = "📚 Keep learning about movies!";
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Quiz Results")
            .setMessage(message + "\n\n" + rating)
            .setPositiveButton("Retry", (dialog, which) -> {
                currentQuestionIndex = 0;
                score = 0;
                Collections.shuffle(questions);
                displayQuestion();
            })
            .setNegativeButton("Exit", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }
}
