package de.hawhamburg.budgettracker.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hawhamburg.budgettracker.R;

public class ResetActivity extends AppCompatActivity {

    private TextInputEditText mEmailReset, mOldPassword, mNewPassword;
    private TextInputLayout oldPasswordLayout, newPasswordLayout;
    private Button btnContinue, btnResetPassword;
    private TextView backToLogin;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean darkMode = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark_mode", false);
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        mEmailReset = findViewById(R.id.email_reset);
        mOldPassword = findViewById(R.id.old_password);
        mNewPassword = findViewById(R.id.new_password);
        oldPasswordLayout = findViewById(R.id.old_password_layout);
        newPasswordLayout = findViewById(R.id.new_password_layout);

        btnContinue = findViewById(R.id.btn_continue);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        backToLogin = findViewById(R.id.back_to_login);

        btnContinue.setOnClickListener(v -> {
            email = mEmailReset.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmailReset.setError("Email required");
                return;
            }

            oldPasswordLayout.setVisibility(View.VISIBLE);
            newPasswordLayout.setVisibility(View.VISIBLE);
            btnResetPassword.setVisibility(View.VISIBLE);

            // Email + Continue Button ausblenden
            mEmailReset.setEnabled(false);
            btnContinue.setVisibility(View.GONE);
        });

        // Schritt 2: Passwort ändern
        btnResetPassword.setOnClickListener(v -> {
            String oldPass = mOldPassword.getText().toString().trim();
            String newPass = mNewPassword.getText().toString().trim();

            if (TextUtils.isEmpty(oldPass)) {
                mOldPassword.setError("Old password required");
                return;
            }
            if (TextUtils.isEmpty(newPass)) {
                mNewPassword.setError("New password required");
                return;
            }
            if (newPass.length() < 6) {
                mNewPassword.setError("Password must be at least 6 characters");
                return;
            }

            mDialog.setMessage("Updating password...");
            mDialog.show();

            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null) {
                mAuth.signInWithEmailAndPassword(email, oldPass)
                        .addOnCompleteListener(signInTask -> {
                            if (signInTask.isSuccessful()) {
                                FirebaseUser loggedInUser = mAuth.getCurrentUser();
                                updatePassword(loggedInUser, newPass);
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(ResetActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                updatePassword(user, newPass);
            }
        });

        backToLogin.setOnClickListener(v -> finish());
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        String oldPass = mOldPassword.getText().toString().trim();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

        user.reauthenticate(credential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    mDialog.dismiss();
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(ResetActivity.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ResetActivity.this, "Failed to update password", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(ResetActivity.this, "Reauthentication failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
