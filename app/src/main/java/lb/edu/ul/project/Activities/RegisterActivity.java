package lb.edu.ul.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import lb.edu.ul.project.Database.DatabaseHelper;
import lb.edu.ul.project.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEdt, passwordEdt;
    private Button registerBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        initView();
    }

    private void initView() {
        usernameEdt = findViewById(R.id.usernameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(v -> {
            String username = usernameEdt.getText().toString().trim();
            String password = passwordEdt.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.registerUser(username, password)) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        });
    }
}
