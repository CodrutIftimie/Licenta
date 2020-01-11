package ga.servicereq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MessagesAdapter {

    private final LinearLayout body;
    private Context context;
    public ArrayList<LinearLayout> messages;
    private static ArrayList<Message> messagesList = new ArrayList<>();
    private final Fragment fragment;
    private boolean updaterRunning = false;
    private SharedPreferences preferences;

    public MessagesAdapter(Context context2, View view, Fragment fragment) {
        context = context2;
        body = (LinearLayout) view;
        this.fragment = fragment;
        messages = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
    }

    private void add(final Message msg, final long viewTagId) {
        body.post(new Runnable() {
            @Override
            public void run() {
                final LinearLayout toBeAdded = (LinearLayout) View.inflate(context, R.layout.message_preview, null);
                TextView receiverName = toBeAdded.findViewById(R.id.messagepv_receiverName);
                TextView lastMessageTv = toBeAdded.findViewById(R.id.messagepv_msgPreview);
                Button endButton = toBeAdded.findViewById(R.id.messagepv_endButton);

                //ToDo: [DONE] rating layout fully working
                //ToDo: [DONE] for rating might need another class

                final LayoutInflater inflater = LayoutInflater.from(toBeAdded.getContext());

                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Rating.show(inflater, toBeAdded);
                        //ToDo: repair in database the rating (might need to rebuild the tables)
                    }
                });


                final String name = msg.firstName + " " + msg.lastName;
                receiverName.setText(name);
                lastMessageTv.setText(msg.lastMessage);

                toBeAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), MessagingActivity.class);
                        i.putExtra("fname", msg.firstName);
                        i.putExtra("lname", msg.lastName);
                        i.putExtra("senderId", msg.senderId);
                        fragment.startActivity(i);
                    }
                });

                View possibleExistingView = body.findViewWithTag(viewTagId);
                if (possibleExistingView != null) {
                    body.removeView(possibleExistingView);
                    Toast.makeText(Server.getAppContext(), "FOUND VIEW, DELETED!", Toast.LENGTH_LONG).show();
                } else toBeAdded.setTag(viewTagId);

                messages.add(toBeAdded);
                body.addView(toBeAdded, 1);
            }
        });
    }

    public void add(final Message msg) {
        Map<String, ?> convos = preferences.getAll();
        boolean found = false;
        for (Map.Entry<String, ?> convo : convos.entrySet())
            if(convo.getKey().length() > 5) {
                if (convo.getKey().substring(0, 5).equals("CONVO")) {
                    Conversation convoObj = new Gson().fromJson(convo.getValue().toString(), Conversation.class);
                    if (convoObj.senderId.equals(msg.senderId)) {
                        add(msg, convoObj.viewTag);
                        found = true;
                    }
                }
            }
        if (!found) {
            Conversation newConversation = new Conversation(msg.senderId, msg.firstName, msg.lastName);
            newConversation.viewTag = Conversation.conversationsCount++;
            add(msg, newConversation.viewTag);
        }
    }

    public void add(Conversation convo) {
        String latestMessage = convo.conversation.get(convo.conversation.size()-1).message;
        add(new Message(convo.senderId, convo.firstName, convo.lastName, latestMessage, ""), convo.viewTag);
    }

    public void runUpdater() {
        if (!updaterRunning) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            while (messagesList.size() > 0) {
                                add(messagesList.remove(0));
                            }
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
            updaterRunning = true;
        }
    }

    public static void serverAdd(final Message msg) {
        messagesList.add(msg);
    }
}
