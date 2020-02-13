package ga.servicereq;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MessagingActivity extends AppCompatActivity {

    private boolean updaterRunning = false;
    private static ArrayList<Message> senderMessages;
    private RoundDrawable receiverImageDrawable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_opened);
        senderMessages = new ArrayList<>();
        runUpdater();

        final ImageButton sendButton = findViewById(R.id.message_send_button);
        final EditText message = findViewById(R.id.message_input);
        final LinearLayout exchangedMessages = findViewById(R.id.exchanged_messages);
        final ScrollView scrollArea = findViewById(R.id.message_scrollArea);
        final ImageView helperIcon = findViewById(R.id.message_helper);
        final LayoutInflater inflater = LayoutInflater.from(exchangedMessages.getContext());
        final ImageView profilePicture = findViewById(R.id.message_receiverAvatar);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
        final String currentUserId = preferences.getString("gid", "");
        final String currentUserPicture = preferences.getString("img", "NONE");
        final String receiverId = getIntent().getStringExtra("receiverId");
        final String firstName = getIntent().getStringExtra("fname");
        final String lastName = getIntent().getStringExtra("lname");
        final String picture = getIntent().getStringExtra("image");
        final boolean isHelper = getIntent().getBooleanExtra("helper", false);
        final String conversationId = "CONVO" + receiverId;

        RoundDrawable currentUserImageDrawable = null;

        if(!picture.equals("NONE")) {
            String[] byteValues = picture.substring(1, picture.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];

            for (int i = 0, len = bytes.length; i < len; i++) {
                bytes[i] = Byte.parseByte(byteValues[i]);
            }
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image = Bitmap.createScaledBitmap(image, 200, 200, true);
            receiverImageDrawable = new RoundDrawable(image);
            profilePicture.setImageDrawable(receiverImageDrawable);
        }

        if(!currentUserPicture.equals("NONE")) {
            String[] byteValues = currentUserPicture.substring(1, currentUserPicture.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];

            for (int i = 0, len = bytes.length; i < len; i++) {
                bytes[i] = Byte.parseByte(byteValues[i]);
            }
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image = Bitmap.createScaledBitmap(image, 200, 200, true);
            currentUserImageDrawable = new RoundDrawable(image);
        }

        if (!isHelper)
            helperIcon.setImageDrawable(null);

        TextView name = findViewById(R.id.message_receiverName);
        String fullName = firstName + " " + lastName;
        name.setText(Server.convertBackSpecialCharacters(fullName));

        final Conversation conversation;
        String savedConversation = preferences.getString(conversationId, "CONVO NOT FOUND");
        if (savedConversation.equals("CONVO NOT FOUND"))
            conversation = new Conversation(receiverId, firstName, lastName);
        else {
            conversation = new Gson().fromJson(savedConversation, Conversation.class);
        }

        for (ExchangedMessage msg : conversation.conversation) {
            RelativeLayout savedMessage;
            TextView messageText;
            ImageView userPicture;
            if (msg.senderId.equals(currentUserId)) {
                savedMessage = (RelativeLayout) inflater.inflate(R.layout.message_sent, null);
                messageText = savedMessage.findViewById(R.id.messagesent_senderText);
                userPicture = savedMessage.findViewById(R.id.messagesent_senderAvatar);
                if(currentUserImageDrawable != null)
                    userPicture.setImageDrawable(currentUserImageDrawable);
                messageText.setText(Server.convertBackSpecialCharacters(msg.message));
            } else {
                savedMessage = (RelativeLayout) inflater.inflate(R.layout.message_received, null);
                messageText = savedMessage.findViewById(R.id.messagerecv_senderText);
                userPicture = savedMessage.findViewById(R.id.messagerecv_senderAvatar);
                if(receiverImageDrawable != null)
                    userPicture.setImageDrawable(receiverImageDrawable);
                messageText.setText(Server.convertBackSpecialCharacters(msg.message));
            }
            exchangedMessages.addView(savedMessage);
        }
//        runUpdater();

        final RoundDrawable finalUserImage = currentUserImageDrawable;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Send message to server
                if (message.getText().length() > 0) {
                    RelativeLayout newMessage = (RelativeLayout) inflater.inflate(R.layout.message_sent, null);
                    TextView messageText = newMessage.findViewById(R.id.messagesent_senderText);
                    ImageView userPicture = newMessage.findViewById(R.id.messagesent_senderAvatar);
                    final String msg = message.getText().toString();
                    messageText.setText(message.getText().toString());
                    if(finalUserImage != null)
                        userPicture.setImageDrawable(finalUserImage);

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
                            Server.sendMessage("M;" + currentUserId + ";" + receiverId + ";" + Server.formatSpecialCharacters(msg) + ";;");
                        }
                    });
                    message.setText("");
                    scrollArea.fullScroll(View.FOCUS_DOWN);
                    boolean isHelper = !(preferences.getString("cat", "").equals(""));
                    Message m = new Message(receiverId, firstName, lastName, Server.formatSpecialCharacters(msg), "", picture, isHelper);
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
        ImageView receiverPicture;
        queueMessage = (RelativeLayout) inflater.inflate(R.layout.message_received, null);
        messageText = queueMessage.findViewById(R.id.messagerecv_senderText);
        receiverPicture = queueMessage.findViewById(R.id.messagerecv_senderAvatar);
        messageText.setText(Server.convertBackSpecialCharacters(m.lastMessage));
        if(receiverImageDrawable != null)
            receiverPicture.setImageDrawable(receiverImageDrawable);
        exchangedMessages.post(new Runnable() {
            @Override
            public void run() {
                exchangedMessages.addView(queueMessage);
                scrollArea.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void runUpdater() {
        if (!updaterRunning) {
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
        String currentUserId = Objects.requireNonNull(preferences.getString("gid", ""));
        Map<String, ?> convos = preferences.getAll();
        boolean found = false;
        for (Map.Entry<String, ?> convo : convos.entrySet())
            if (convo.getKey().length() > 5) {
                if (convo.getKey().substring(0, 5).equals("CONVO")) {
                    Conversation convoObj = new Gson().fromJson(convo.getValue().toString(), Conversation.class);
                    if (convoObj.receiverId.equals(m.senderId)) {
                        if (m.activityAdded)
                            convoObj.addMessage(new ExchangedMessage(currentUserId, m.lastMessage, m.date));
                        else
                            convoObj.addMessage(new ExchangedMessage(m.senderId, m.lastMessage, m.date));

                        String convoString = new Gson().toJson(convoObj, Conversation.class);
                        editor.putString("CONVO" + convoObj.receiverId, convoString);
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
            if (m.activityAdded)
                newConversation.addMessage(new ExchangedMessage(currentUserId, m.lastMessage, m.date));
            else
                newConversation.addMessage(new ExchangedMessage(m.senderId, m.lastMessage, m.date));
            String convoString = new Gson().toJson(newConversation, Conversation.class);
            editor.putString("CONVO" + newConversation.receiverId, convoString);
            editor.apply();
        }
        if (senderMessages != null)
            senderMessages.add(m);
    }
}
