package ga.servicereq;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final CheckBox enableHelper = findViewById(R.id.editprofile_check_helper);
        final LinearLayout helperOptions = findViewById(R.id.editprofile_categories);
        final Button saveButton = findViewById(R.id.editprofile_btn_save);
        final Button cancelButton = findViewById(R.id.editprofile_btn_cancel);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
        final String categories = preferences.getString("cat", "");

        if (categories != null && !categories.equals("")) {
            enableHelper.setChecked(true);
            for (int i = 0; i < 21; i++) {
                CheckBox helpOption = new CheckBox(enableHelper.getContext());
                helpOption.setText(Services.getById(i).toString().replace("_", " "));
                helpOption.setTextColor(Color.WHITE);
                helpOption.setTag(Services.getById(i).toEnglishString());
                if (categories.contains(Services.getById(i).toEnglishString()))
                    helpOption.setChecked(true);
                helperOptions.addView(helpOption);
            }

        }

        enableHelper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 21; i++) {
                        CheckBox helpOption = new CheckBox(enableHelper.getContext());
                        helpOption.setText(Services.getById(i).toString().replace("_", " "));
                        helpOption.setTextColor(Color.WHITE);
                        helpOption.setTag(Services.getById(i).toEnglishString());
                        if (categories != null && categories.contains(Services.getById(i).toEnglishString()))
                            helpOption.setChecked(true);
                        helperOptions.addView(helpOption);
                    }
                } else {
                    for (int i = 0; i < helperOptions.getChildCount() + 10; i++) {
                        try {
                            CheckBox view = (CheckBox) helperOptions.getChildAt(i);
                            if (view != null) {
                                String viewTag = view.getTag().toString();
                                if (Services.isService(viewTag))
                                    helperOptions.removeViewAt(i--);
                            }
                        } catch (ClassCastException ignored) {
                        }
                    }
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
                StringBuilder updateString = new StringBuilder("U;").append(preferences.getString("gid", "")).append(";");
                String firstName = preferences.getString("fn", "");
                String lastName = preferences.getString("ln", "");
                String psd = preferences.getString("psd", "");
                EditText newFn = findViewById(R.id.editprofile_firstNameInput);
                EditText newLn = findViewById(R.id.editprofile_lastNameInput);
                EditText newPsd = findViewById(R.id.editprofile_passwordInput);
                EditText newCpsd = findViewById(R.id.editprofile_cPasswordInput);
                List<String> helpOptions = new ArrayList<>();
                byte numberOfOptions = 0;
                if (enableHelper.isChecked()) {
                    for (int i = 0; i < helperOptions.getChildCount() + 10; i++) {
                        try {
                            CheckBox view = (CheckBox) helperOptions.getChildAt(i);
                            if (view != null) {
                                String viewTag = view.getTag().toString();
                                if (Services.isService(viewTag))
                                    if (view.isChecked()) {
                                        helpOptions.add(viewTag);
                                        numberOfOptions++;
                                    }
                            }
                        } catch (ClassCastException ignored) {
                        }
                    }
                }
                updateString.append(numberOfOptions).append(";");
                if (!firstName.equals(newFn.getText().toString()) && newFn.getText().length() > 0) {
                    updateString.append(Server.formatSpecialCharacters(newFn.getText().toString())).append(";");
                } else updateString.append("_;");
                if (!lastName.equals(newLn.getText().toString()) && newLn.getText().length() > 0) {
                    updateString.append(Server.formatSpecialCharacters(newLn.getText().toString())).append(";");
                } else updateString.append("_;");
                if (newPsd.getText().length() > 0) {
                    if (newPsd.getText().equals(newCpsd.getText())) {
                        try {
                            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                            byte[] encryptedPassword = messageDigest.digest(newPsd.getText().toString().getBytes());
                            StringBuilder pass = new StringBuilder();
                            for (byte b : encryptedPassword) {
                                pass.append(b);
                            }
                            if (!psd.equals(pass.toString())) {
                                updateString.append(pass).append(";");
                            } else updateString.append("_;");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    } else updateString.append("_;");
                } else updateString.append("_;");
                StringBuilder categories = new StringBuilder();
                if (numberOfOptions > 0) {
                    for (int i = 0; i < numberOfOptions; i++) {
                        updateString.append(helpOptions.get(i)).append(";");
                        categories.append(helpOptions.get(i)).append(";");
                    }
                }
                SharedPreferences.Editor prefEdit = preferences.edit();
                if (categories.length() > 1)
                    prefEdit.putString("cat", categories.substring(0, categories.length() - 1));
                else prefEdit.putString("cat", "");
                prefEdit.apply();
                updateString.append(";");
                if (!updateString.toString().equals("U;" + preferences.getString("gid", "") + "0;_;_;_;;")) {
                    Server.sendMessage(updateString.toString());
                    Toast.makeText(Server.getAppContext(), "Moficări aplicate! La repornirea aplicației veți vedea modificările.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
