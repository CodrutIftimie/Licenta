package ga.servicereq;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    LoginForm loginForm;
    RegisterForm registerForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new Thread(new Server()).start();

        createLoginForm();
    }

    public void loginClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("login_state", "success");
        //startActivity(intent);

        try {
            StringBuilder message = new StringBuilder();
            message.append("L;");
            message.append(loginForm.getEmailInput().getText().toString());
            message.append(";");
            message.append(loginForm.getPasswordInput().getText().toString());
            Server.sendMessage(message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerClicked(View v) {
        setContentView(R.layout.register_layout);
        loginForm = null;
        createRegisterForm();
    }

    public void registerButtonClicked(View v) {
        TextView errorMessage = findViewById(R.id.register_errorMessage);
        try {
            String email = registerForm.getEmailInput().getText().toString();
            boolean validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if(validEmail) {
                if(registerForm.isConfirmPasswordCorrect()) {
                    if(registerForm.isValidName(registerForm.getFirstNameInput())) {
                        if(registerForm.isValidName(registerForm.getLastNameInput())) {
                            //TODO: register the user and jump to mainActivity
                            setContentView(R.layout.activity_main);
                        }
                        else errorMessage.setText(R.string.register_invalidLastName);
                    }
                    else errorMessage.setText(R.string.register_invalidFirstName);
                }
                else errorMessage.setText(R.string.register_passwordsDontMatch);
            }
            else errorMessage.setText(R.string.register_invalidEmail);
        }
        catch (Exception e) {
            errorMessage.setText(R.string.defaultErrorMessage);
        }
    }

    public void registerGoBackClicked(View v) {
        setContentView(R.layout.activity_login);
        registerForm = null;
        createLoginForm();
    }

    private void createLoginForm() {
        loginForm = new LoginForm();
        loginForm.setEmailInput((EditText)findViewById(R.id.login_emailInput));
        loginForm.setPasswordInput((EditText)findViewById(R.id.login_passwordInput));
    }

    private void createRegisterForm() {
        registerForm = new RegisterForm();
        registerForm.setEmailInput((EditText)findViewById(R.id.register_emailInput));
        registerForm.setFirstNameInput((EditText)findViewById(R.id.register_firstNameInput));
        registerForm.setLastNameInput((EditText)findViewById(R.id.register_lastNameInput));
        registerForm.setPasswordInput((EditText)findViewById(R.id.register_passwordInput));
        registerForm.setPasswordConfirmInput((EditText)findViewById(R.id.register_cPasswordInput));
    }
}
