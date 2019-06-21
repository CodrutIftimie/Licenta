package ga.servicereq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity {

    LoginForm loginForm;
    RegisterForm registerForm;
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       if(!Server.isActiveConnection())
           new Thread(new Server()).start();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!preferences.getString("usr","def").equals("def")){
            Button btn = findViewById(R.id.login_loginButton);
            EditText userInput = findViewById(R.id.login_emailInput);
            EditText passInput = findViewById(R.id.login_passwordInput);

            btn.setEnabled(false);

            String usr,psd;
            usr = preferences.getString("usr", "");
            psd = preferences.getString("psd", "");

            userInput.setText(usr);
            passInput.setText(psd);

            String message = "L;" +
                    usr +
                    ";" +
                    psd;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    while(!Server.isActiveConnection()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Server.sendMessage(message);

            login(((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0),usr,psd);
        }
        createLoginForm();
    }

    public void loginClicked(View view) {

        final Context context = this;
        final Button btn = findViewById(R.id.login_loginButton);
        final TextView error = findViewById(R.id.login_errorMessage);
        try {
            boolean validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(loginForm.getEmailInput().getText().toString()).matches();

            if (validEmail) {
                if (loginForm.getPasswordInput().getText().toString().length() > 0) {
                    if (Server.isActiveConnection()) {

                        btn.setEnabled(false);
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        byte[] encryptedPassword = messageDigest.digest(loginForm.getPasswordInput().getText().toString().getBytes());
                        StringBuilder pass = new StringBuilder();
                        for (byte b:encryptedPassword) {
                            pass.append(b);
                        }

                        String message = "L;" +
                                loginForm.getEmailInput().getText().toString() +
                                ";" +
                                pass.toString();
                        Server.sendMessage(message);

                        login(((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0),loginForm.getEmailInput().getText().toString(), pass.toString());

                    }
                } else error.setText(R.string.login_emptyPassword);
            } else error.setText(R.string.register_invalidEmail);
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
        final TextView errorMessage = findViewById(R.id.register_errorMessage);
        final Context context = this;
        final Button btn = findViewById(R.id.register_registerButton);

        btn.setEnabled(false);

        try {
            String email = registerForm.getEmailInput().getText().toString();
            boolean validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (validEmail) {
                if (registerForm.isPasswordValid()) {
                    if (registerForm.isConfirmPasswordCorrect()) {
                        if (registerForm.isValidName(registerForm.getFirstNameInput())) {
                            if (registerForm.isValidName(registerForm.getLastNameInput())) {

                                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                                byte[] encryptedPassword = messageDigest.digest(registerForm.getPasswordInput().getText().toString().getBytes());
                                StringBuilder pass = new StringBuilder();
                                for (byte b:encryptedPassword) {
                                    pass.append(b);
                                }

                                String info = "R;" +
                                        registerForm.getEmailInput().getText().toString() + ";" +
                                        registerForm.getFirstNameInput().getText().toString() + ";" +
                                        registerForm.getLastNameInput().getText().toString() + ";" +
                                        pass.toString();

                                Server.sendMessage(info);

                                final String usr,psd;
                                usr = registerForm.getEmailInput().getText().toString();
                                psd = pass.toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (!Server.hasMessage) {
                                                Thread.sleep(100);
                                            }
                                            String msg = Server.getLatestMessage();
                                            String[] msgs = msg.split(";");

                                            if (msgs[0].equals("SUCCESS")) {

                                                preferencesEditor = preferences.edit();
                                                preferencesEditor.putString("usr",usr);
                                                preferencesEditor.putString("psd",psd);
                                                preferencesEditor.putString("gid",msgs[1]);
                                                preferencesEditor.putString("fn",msgs[2]);
                                                preferencesEditor.putString("ln",msgs[3]);
                                                preferencesEditor.putFloat("rtg",Float.valueOf(msgs[4]));
                                                preferencesEditor.apply();

                                                Intent intent = new Intent(context, MainActivity.class);
                                                startActivity(intent);
                                            } else if(msgs[0].equals("EXISTING")){
                                                btn.setEnabled(true);
                                                errorMessage.setText(R.string.register_existing);
                                            }
                                            else {
                                                btn.setEnabled(true);
                                                errorMessage.setText(R.string.defaultErrorMessage);
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } else errorMessage.setText(R.string.register_invalidLastName);
                        } else errorMessage.setText(R.string.register_invalidFirstName);
                    } else errorMessage.setText(R.string.register_passwordsDontMatch);
                } else errorMessage.setText(R.string.register_invalidPassword);
            } else errorMessage.setText(R.string.register_invalidEmail);
        } catch (Exception e) {
            errorMessage.setText(R.string.defaultErrorMessage);
        }

        btn.setEnabled(true);
    }

    private void login(final View v, final String usr, final String psd) {
        final Button btn = v.findViewById(R.id.login_loginButton);

        final CountDownLatch latch = new CountDownLatch(1);
        final int[] val = new int[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Server.hasMessage) {
                        Thread.sleep(100);
                    }
                    String[] msgs = Server.getLatestMessage().split(";");
                    if (msgs[0].equals("SUCCESS")) {
                        preferencesEditor = preferences.edit();
                        preferencesEditor.putString("usr",usr);
                        preferencesEditor.putString("psd",psd);
                        preferencesEditor.putString("gid",msgs[1]);
                        preferencesEditor.putString("fn",msgs[2]);
                        preferencesEditor.putString("ln",msgs[3]);
                        preferencesEditor.putFloat("rtg",Float.valueOf(msgs[4]));
                        preferencesEditor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        btn.setEnabled(true);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void registerGoBackClicked(View v) {
        setContentView(R.layout.activity_login);
        registerForm = null;
        createLoginForm();
    }

    private void createLoginForm() {
        loginForm = new LoginForm();
        loginForm.setEmailInput((EditText) findViewById(R.id.login_emailInput));
        loginForm.setPasswordInput((EditText) findViewById(R.id.login_passwordInput));
    }

    private void createRegisterForm() {
        registerForm = new RegisterForm();
        registerForm.setEmailInput((EditText) findViewById(R.id.register_emailInput));
        registerForm.setFirstNameInput((EditText) findViewById(R.id.register_firstNameInput));
        registerForm.setLastNameInput((EditText) findViewById(R.id.register_lastNameInput));
        registerForm.setPasswordInput((EditText) findViewById(R.id.register_passwordInput));
        registerForm.setPasswordConfirmInput((EditText) findViewById(R.id.register_cPasswordInput));
    }
}
