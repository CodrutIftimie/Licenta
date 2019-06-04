package ga.servicereq;

import android.widget.EditText;

public class LoginForm {
    private EditText emailInput;
    private EditText passwordInput;

    private boolean emailSet = false;
    private boolean passwordSet = false;

    LoginForm() { }

    public EditText getEmailInput() throws Exception {
        if(!emailSet)
            throw new Exception("Email Input was not set!");
        return emailInput;
    }

    public EditText getPasswordInput() throws Exception {
        if(!passwordSet)
            throw new Exception("Password Input was not set!");
        return passwordInput;
    }

    void setEmailInput(EditText newEmailInput) {
        if (!emailSet) {
            emailInput = newEmailInput;
            emailSet = true;
        }
    }

    void setPasswordInput(EditText newPasswordInput) {
        if (!passwordSet) {
            passwordInput = newPasswordInput;
            passwordSet = true;
        }
    }
}
