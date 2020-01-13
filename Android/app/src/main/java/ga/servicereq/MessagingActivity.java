package ga.servicereq;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_opened);

        //ToDo: [DONE - need fix] server-side send message to receiver (similar to post broadcast but check client name (implement that))

        //ToDo: [DONE] message adding like in Posts (most likely with an adapter)
        //ToDo: [DONE] messages distinctive from sender to receiver (most likely with a different layout for each type of message)
        //ToDo: [DONE] send button to work (with onClickButton possible being the easiest method)

        final ImageButton sendButton = findViewById(R.id.message_send_button);
        final EditText message = findViewById(R.id.message_input);
        final LinearLayout exchangedMessages = findViewById(R.id.exchanged_messages);
        final ScrollView scrollArea = findViewById(R.id.message_scrollArea);
        final LayoutInflater inflater = LayoutInflater.from(exchangedMessages.getContext());

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
        final String currentUserId = preferences.getString("gid","");
        final String receiverId = getIntent().getStringExtra("receiverId");
        final String firstName = getIntent().getStringExtra("fname");
        final String lastName = getIntent().getStringExtra("lname");
        final String conversationId = "CONVO"+receiverId;

        TextView name = findViewById(R.id.message_receiverName);
        String fullName = firstName + " " + lastName;
        name.setText(fullName);

        final Conversation conversation;
        String savedConversation = preferences.getString(conversationId, "CONVO NOT FOUND");
        if(savedConversation.equals("CONVO NOT FOUND"))
            conversation = new Conversation(receiverId, firstName, lastName);
        else {
            conversation = new Gson().fromJson(savedConversation, Conversation.class);
        }

        for(ExchangedMessage msg : conversation.conversation) {
            RelativeLayout savedMessage;
            TextView messageText;
            if(msg.senderId.equals(currentUserId)) {
                savedMessage = (RelativeLayout) inflater.inflate(R.layout.message_sent, null);
                messageText = savedMessage.findViewById(R.id.messagesent_senderText);
                messageText.setText(msg.message);
            }
            else {
                savedMessage = (RelativeLayout) inflater.inflate(R.layout.message_received, null);
                messageText = savedMessage.findViewById(R.id.messagerecv_senderText);
                messageText.setText(msg.message);
            }
            exchangedMessages.addView(savedMessage);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Send message to server
                if(message.getText().length() > 0) {
                    RelativeLayout newMessage = (RelativeLayout) inflater.inflate(R.layout.message_sent, null);
                    TextView messageText = newMessage.findViewById(R.id.messagesent_senderText);
                    final String msg = message.getText().toString();
                    messageText.setText(message.getText());

                    exchangedMessages.addView(newMessage);
                    ExchangedMessage exchMsgObj = new ExchangedMessage(currentUserId, message.getText().toString());
                    conversation.addMessage(exchMsgObj);
                    SharedPreferences.Editor prefsEditor = preferences.edit();
                    String exchMsg = new Gson().toJson(conversation);
                    prefsEditor.putString(conversationId, exchMsg);
                    prefsEditor.apply();
                    scrollArea.fullScroll(View.FOCUS_DOWN);
                    sendButton.post(new Runnable() {
                        @Override
                        public void run() {
                            Server.sendMessage("M;"+currentUserId+";"+receiverId+";"+msg+";;");
                        }
                    });
                    message.setText("");
                    Message m = new Message(currentUserId,receiverId,firstName,lastName,msg,"");
                    m.activityAdded = true;
                    MessagesAdapter.serverAdd(m);
                }
            }
        });
    }
}
