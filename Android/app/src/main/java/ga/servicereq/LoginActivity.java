package ga.servicereq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity {

    //FIXME login layout to show when automatically logging in (AGAIN)

    LoginForm loginForm;
    RegisterForm registerForm;
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!Server.isActiveConnection())
            new Thread(new Server(getApplicationContext())).start();

        preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
        createLoginForm();
        if (!preferences.getString("usr", "def").equals("def")) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Button btn = findViewById(R.id.login_loginButton);
                    btn.setEnabled(false);

                    String usr, psd;
                    usr = preferences.getString("usr", "");
                    psd = preferences.getString("psd", "");

                    try {
                        loginForm.getEmailInput().setText(usr);
                        loginForm.getPasswordInput().setText(psd);
                    }
                    catch (Exception e) { e.printStackTrace(); }

                    String message = "L;" + usr + ";" + psd;
                    Server.sendMessage(message);
                    login(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), usr, psd);
                }
            });
        }

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
                        for (byte b : encryptedPassword) {
                            pass.append(b);
                        }

                        String message = "L;" +
                                loginForm.getEmailInput().getText().toString() +
                                ";" +
                                pass.toString();
                        Server.sendMessage(message);

                        login(((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0), loginForm.getEmailInput().getText().toString(), pass.toString());

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
                                for (byte b : encryptedPassword) {
                                    pass.append(b);
                                }

                                String info = "R;" +
                                        registerForm.getEmailInput().getText().toString() + ";" +
                                        registerForm.getFirstNameInput().getText().toString() + ";" +
                                        registerForm.getLastNameInput().getText().toString() + ";" +
                                        pass.toString();

                                Server.sendMessage(info);

                                final String usr, psd;
                                usr = registerForm.getEmailInput().getText().toString();
                                psd = pass.toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (Server.messagesCount() == 0) {
                                                Thread.sleep(50);
                                            }
                                            String status = Server.getMessage(0);
                                            if (status.equals("RSUCCESS")) {
                                                String[] userData = Server.getMessage(0).split(";");
                                                preferencesEditor = preferences.edit();
                                                preferencesEditor.putString("usr", usr);
                                                preferencesEditor.putString("psd", psd);
                                                preferencesEditor.putString("gid", userData[0]);
                                                preferencesEditor.putString("fn", userData[1]);
                                                preferencesEditor.putString("ln", userData[2]);
                                                preferencesEditor.putString("img", "NONE");
                                                preferencesEditor.putFloat("rtg", Float.valueOf(userData[3]));
                                                preferencesEditor.apply();

                                                Intent intent = new Intent(context, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else if (status.equals("REXISTING")) {
                                                btn.setEnabled(true);
                                                errorMessage.setText(R.string.register_existing);
                                            } else {
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
        btn.post(new Runnable() {
            @Override
            public void run() {
                while(Server.messagesCount() == 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String fMessage = Server.getMessage(0);
                Log.e("LOGIN",fMessage);
                if (fMessage.equals("LSUCCESS")) {
                    if(Server.messagesCount() == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String[] userData = Server.getMessage(0).split(";");
                    preferencesEditor = preferences.edit();
                    preferencesEditor.putString("usr", usr);
                    preferencesEditor.putString("psd", psd);
                    preferencesEditor.putString("gid", userData[0]);
                    preferencesEditor.putString("fn", userData[1]);
                    preferencesEditor.putString("ln", userData[2]);
                    preferencesEditor.putString("img", userData[4]);
                    preferencesEditor.putFloat("rtg", Float.valueOf(userData[3]));
                    preferencesEditor.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    btn.setEnabled(true);
                }
            }
        });
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
