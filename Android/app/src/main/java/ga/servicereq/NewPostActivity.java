package ga.servicereq;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class NewPostActivity extends AppCompatActivity {

    Button post;
    EditText description;
    Spinner category;
    RadioGroup radios;
    byte location;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        post = findViewById(R.id.newpost_postButton);
        description = findViewById(R.id.newpost_editText);
        category = findViewById(R.id.newpost_spinner);

        radios = findViewById(R.id.newpost_radiogroup);
        radios.check(R.id.newpost_homeRadio);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (description.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "The description cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    RadioButton radio = findViewById(radios.getCheckedRadioButtonId());
                    if (radio.getText().toString().split(" ")[0].equals("Only"))
                        location = 0;
                    else location = 1;

                    String message = "N;" +
                            prefs.getString("gid", "") +
                            ";" +
                            description.getText().toString() +
                            ";" +
                            "mechanic" +
                            //category.getSelectedItem().toString() +
                            ";" +
                            location +
                            ";NONE;";
                    post.setEnabled(false);

                    Server.sendMessage(message);

                    post.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (Server.messagesCount() == 0) {
                                    Thread.sleep(50);
                                }
                                String msg = Server.getMessage(Server.messagesCount()-1);
                                String[] msgs = msg.split(";");

                                if (msgs[0].equals("SUCCESS") || (msgs[0].equals("P") && msgs[8].equals("SUCCESS")))
                                    Toast.makeText(getApplicationContext(), "Post added!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Failed to add the post!", Toast.LENGTH_SHORT).show();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            post.setEnabled(true);
                        }
                    });
                }
            }
        });
    }
}