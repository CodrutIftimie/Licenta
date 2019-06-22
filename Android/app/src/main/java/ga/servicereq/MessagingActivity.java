package ga.servicereq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_opened);

        String nameS = getIntent().getStringExtra("name");
        TextView name = findViewById(R.id.message_receiverName);
        name.setText(nameS);
    }
}
