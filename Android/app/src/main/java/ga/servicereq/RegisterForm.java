package ga.servicereq;

import android.widget.EditText;

public class RegisterForm {
    private EditText emailInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;

    private boolean emailSet = false;
    private boolean firstNameSet = false;
    private boolean lastNameSet = false;
    private boolean passwordSet = false;
    private boolean passwordConfirmSet = false;

    public RegisterForm() {}

    public EditText getEmailInput() throws Exception {
        if(!emailSet)
            throw new Exception("Email Input was not set!");
        return emailInput;
    }

    public EditText getFirstNameInput() throws Exception{
        if(!firstNameSet)
            throw new Exception("First Name Input was not set!");
        return firstNameInput;
    }

    public EditText getLastNameInput() throws Exception{
        if(!lastNameSet)
            throw new Exception("Last Name Input was not set!");
        return lastNameInput;
    }

    public EditText getPasswordInput() throws Exception{
        if(!passwordSet)
            throw new Exception("Password Input was not set!");
        return passwordInput;
    }

    public EditText getPasswordConfirmInput() throws Exception{
        if(!passwordConfirmSet)
            throw new Exception("Password Confirm Input was not set!");
        return passwordConfirmInput;
    }

    public void setEmailInput(EditText emailInput) {
        if(!emailSet) {
            this.emailInput = emailInput;
            emailSet = true;
        }
    }

    public void setFirstNameInput(EditText firstNameInput) {
        if(!firstNameSet) {
            this.firstNameInput = firstNameInput;
            firstNameSet = true;
        }
    }

    public void setLastNameInput(EditText lastNameInput) {
        if(!lastNameSet) {
            this.lastNameInput = lastNameInput;
            lastNameSet = true;
        }
    }

    public void setPasswordInput(EditText passwordInput) {
        if(!passwordSet) {
            this.passwordInput = passwordInput;
            passwordSet = true;
        }
    }

    public void setPasswordConfirmInput(EditText passwordConfirmInput) {
        if(!passwordConfirmSet) {
            this.passwordConfirmInput = passwordConfirmInput;
            passwordConfirmSet = true;
        }
    }

    public boolean isConfirmPasswordCorrect() {
        if(passwordInput != null && passwordConfirmInput != null) {
            return passwordConfirmInput.getText().toString().equals(passwordInput.getText().toString());
        }
        return false;
    }

    public boolean isValidName(EditText nameInput) {
        if(nameInput != null) {
            String name = nameInput.getText().toString();
            return name.matches("[A-Z][a-z]+");
        }
        return false;
    }
}