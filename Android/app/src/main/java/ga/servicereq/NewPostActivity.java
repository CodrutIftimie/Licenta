package ga.servicereq;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
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
    static AsyncTask<String, String, String> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        prefs = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());

        post = findViewById(R.id.newpost_postButton);
        description = findViewById(R.id.newpost_editText);
        category = findViewById(R.id.newpost_spinner);

        radios = findViewById(R.id.newpost_radiogroup);
        radios.check(R.id.newpost_homeRadio);

        post.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
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
                            ";NONE;;";
                    post.setEnabled(false);

                    Server.sendMessage(message);

//                    post.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            int count = Server.messagesCount();
//                            while (count == Server.messagesCount()) {
//                                SystemClock.sleep(100);
//                            }
//                            String response = Server.getMessage(Server.messagesCount() - 1);
//
//                            if ((response.equals("SUCCESS")))
//                                Toast.makeText(getApplicationContext(), "Post added!", Toast.LENGTH_SHORT).show();
//                            else
//                                Toast.makeText(getApplicationContext(), "Failed to add the post!", Toast.LENGTH_SHORT).show();
//                            post.setEnabled(true);
//                        }
//                    });

                    task = new AsyncTask<String, String, String>() {
                        @Override
                        protected void onPreExecute() {
                            post.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    post.setEnabled(true);
                                }
                            },200);
                        }

                        @Override
                        protected String doInBackground(String... params) {
                            int count = Server.messagesCount();
                            while (count == Server.messagesCount()) {
                                SystemClock.sleep(100);
                            }
                            final String response = Server.getMessage(Server.messagesCount() - 1);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if ((response.equals("SUCCESS")))
                                        Toast.makeText(NewPostActivity.this, "Post added!", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(NewPostActivity.this, "Failed to add the post!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return null;
                        }
                    }.execute();
                }
            }
        });
    }
}