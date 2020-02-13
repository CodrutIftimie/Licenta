package ga.servicereq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MessagesAdapter {

    private final LinearLayout body;
    private Context context;
    private ArrayList<LinearLayout> messages;
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

    private void add(final Message msg, final String viewTagId) {
        body.post(new Runnable() {
            @Override
            public void run() {
                final LinearLayout toBeAdded = (LinearLayout) View.inflate(context, R.layout.message_preview, null);
                TextView receiverName = toBeAdded.findViewById(R.id.messagepv_receiverName);
                TextView lastMessageTv = toBeAdded.findViewById(R.id.messagepv_msgPreview);
                Button endButton = toBeAdded.findViewById(R.id.messagepv_endButton);
                ImageView helperIcon = toBeAdded.findViewById(R.id.messagepv_helper);
                ImageView profilePicture = toBeAdded.findViewById(R.id.messagepv_receiverAvatar);

                if(!msg.picture.equals("NONE")) {
                    String[] byteValues = msg.picture.substring(1, msg.picture.length() - 1).split(",");
                    byte[] bytes = new byte[byteValues.length];

                    for (int i = 0, len = bytes.length; i < len; i++) {
                        bytes[i] = Byte.parseByte(byteValues[i]);
                    }
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image = Bitmap.createScaledBitmap(image, 400, 400, true);
                    RoundDrawable roundDrawable = new RoundDrawable(image);
                    profilePicture.setImageDrawable(roundDrawable);
                }
                if(!msg.isHelper)
                    helperIcon.setImageDrawable(null);

                final LayoutInflater inflater = LayoutInflater.from(toBeAdded.getContext());

                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Rating.show(inflater, toBeAdded);
                    }
                });


                final String name = msg.firstName + " " + msg.lastName;
                receiverName.setText(Server.convertBackSpecialCharacters(name));
                String lM;
                if(Server.convertBackSpecialCharacters(msg.lastMessage).length()>34)
                    lM = msg.lastMessage.substring(0,35) + "...";
                else lM = msg.lastMessage;
                lastMessageTv.setText(Server.convertBackSpecialCharacters(lM));

                toBeAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), MessagingActivity.class);
                        i.putExtra("fname", msg.firstName);
                        i.putExtra("lname", msg.lastName);
                        i.putExtra("receiverId", msg.senderId);
                        i.putExtra("image", msg.picture);
                        i.putExtra("helper", msg.isHelper);
                        fragment.startActivity(i);
                    }
                });

                View possibleExistingView = body.findViewWithTag(viewTagId);
                if (possibleExistingView != null) {
                    body.removeView(possibleExistingView);
                }

                toBeAdded.setTag(viewTagId);
                messages.add(toBeAdded);
                body.addView(toBeAdded, 1);
            }
        });
    }

    public void add(final Message msg) {
        add(msg,msg.senderId);
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
