package de.hawhamburg.budgettracker.ui;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hawhamburg.budgettracker.R;

public class AboutActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Button backButton = findViewById(R.id.btn_settings);
        backButton.setOnClickListener(v -> finish());
    }
}
