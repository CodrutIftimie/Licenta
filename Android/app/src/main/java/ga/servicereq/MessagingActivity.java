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

import java.util.ArrayList;
import java.util.Map;

public class MessagingActivity extends AppCompatActivity {

    private boolean updaterRunning = false;
    private static ArrayList<Message> senderMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_opened);
        runUpdater();
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
//        runUpdater();

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
                    sendButton.post(new Runnable() {
                        @Override
                        public void run() {
                            Server.sendMessage("M;"+currentUserId+";"+receiverId+";"+msg+";;");
                        }
                    });
                    message.setText("");
                    scrollArea.fullScroll(View.FOCUS_DOWN);
                    Message m = new Message(receiverId,firstName,lastName,msg,"");
                    m.activityAdded = true;
                    MessagesAdapter.serverAdd(m);
                }
            }
        });
    }

    private void addFromQueue(Message m) {
        final LinearLayout exchangedMessages = findViewById(R.id.exchanged_messages);
        final ScrollView scrollArea = findViewById(R.id.message_scrollArea);
        final LayoutInflater inflater = LayoutInflater.from(exchangedMessages.getContext());
        final RelativeLayout queueMessage;
        TextView messageText;
        queueMessage = (RelativeLayout) inflater.inflate(R.layout.message_received, null);
        messageText = queueMessage.findViewById(R.id.messagerecv_senderText);
        messageText.setText(m.lastMessage);
        exchangedMessages.post(new Runnable() {
            @Override
            public void run() {
                exchangedMessages.addView(queueMessage);
                scrollArea.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void runUpdater() {
        if(!updaterRunning) {
            updaterRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (updaterRunning) {
                            while (senderMessages.size() > 0) {
                                addFromQueue(senderMessages.remove(0));
                            }
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
        }
    }

    public static void staticAdd(Message m) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        Map<String, ?> convos = preferences.getAll();
        boolean found = false;
        for (Map.Entry<String, ?> convo : convos.entrySet())
            if(convo.getKey().length() > 5) {
                if (convo.getKey().substring(0, 5).equals("CONVO")) {
                    Conversation convoObj = new Gson().fromJson(convo.getValue().toString(), Conversation.class);
                    if (convoObj.receiverId.equals(m.senderId)) {
                        convoObj.addMessage(new ExchangedMessage(m.senderId,m.lastMessage,m.date));
                        String convoString = new Gson().toJson(convoObj,Conversation.class);
                        editor.putString("CONVO"+convoObj.receiverId,convoString);
                        editor.apply();
                        found = true;
                    }
                }
            }
        if (!found) {
            Conversation newConversation = new Conversation(m.senderId, m.firstName, m.lastName);
            newConversation.receiverId = m.senderId;
            newConversation.firstName = m.firstName;
            newConversation.lastName = m.lastName;
            newConversation.addMessage(new ExchangedMessage(m.senderId,m.lastMessage,m.date));
            String convoString = new Gson().toJson(newConversation,Conversation.class);
            editor.putString("CONVO"+newConversation.receiverId,convoString);
            editor.apply();
        }
        senderMessages.add(m);
    }
}
