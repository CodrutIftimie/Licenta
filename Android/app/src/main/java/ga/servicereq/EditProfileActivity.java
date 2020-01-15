package ga.servicereq;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.nio.file.StandardWatchEventKinds;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final CheckBox enableHelper = findViewById(R.id.editprofile_check_helper);
        final LinearLayout helperOptions = findViewById(R.id.editprofile_categories);
        final Button saveButton = findViewById(R.id.editproifle_btn_save);
        enableHelper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 21; i++) {
                        CheckBox helpOption = new CheckBox(enableHelper.getContext());
                        helpOption.setText(Services.getById(i).toString());
                        helpOption.setTextColor(Color.WHITE);
                        helpOption.setTag(Services.getById(i).toString());
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
                //TODO: send to server updated information
            }
        });
    }
}
