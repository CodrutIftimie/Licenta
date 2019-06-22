package ga.servicereq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessagesAdapter {

    private final LinearLayout body;
    private Context context;
    public ArrayList<LinearLayout> messages;
    private final Fragment fragment;

    public MessagesAdapter(Context context2, View view, Fragment fragment) {
        context = context2;
        body = (LinearLayout) view;
        this.fragment = fragment;
        messages = new ArrayList<>();
    }

    public void add(String firstName, String lastName, String lastMessage) {

        LinearLayout toBeAdded = (LinearLayout)View.inflate(context, R.layout.message_preview, null);
        TextView receiverName = toBeAdded.findViewById(R.id.messagepv_receiverName);
        TextView lastMessageTv = toBeAdded.findViewById(R.id.messagepv_msgPreview);


        final LayoutInflater inflater = LayoutInflater.from(toBeAdded.getContext());
        Button b = toBeAdded.findViewById(R.id.messagepv_endButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog deleteDialog = new AlertDialog.Builder(view.getContext()).create();
                final View deleteDialogView = inflater.inflate(R.layout.layout_rating, null);
                deleteDialog.setView(deleteDialogView);
                deleteDialog.show();
            }
        });


        final String name = firstName + " " + lastName;
        receiverName.setText(name);
        lastMessageTv.setText(lastMessage);

        toBeAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),MessagingActivity.class);
                i.putExtra("name", name);
                fragment.startActivity(i);
            }
        });

        messages.add(toBeAdded);
        body.addView(toBeAdded);
    }
}
