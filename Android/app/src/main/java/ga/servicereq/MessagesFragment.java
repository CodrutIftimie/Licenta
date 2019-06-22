package ga.servicereq;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Objects;

public class MessagesFragment extends Fragment {

    LinearLayout messages;
    MessagesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messages = Objects.requireNonNull(getView()).findViewById(R.id.preview_messages);

        adapter = new MessagesAdapter(getContext(),messages,this);

        adapter.add("Prenume","Nume","Ultimul mesaj");
        adapter.add("Cineva","Altcineva","Mesaj");

        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.message_preview, null);
        Button b = v.findViewById(R.id.messagepv_endButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
                final View deleteDialogView = inflater.inflate(R.layout.layout_rating, null);
                deleteDialog.setView(deleteDialogView);
                deleteDialog.show();
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MessagingActivity.class);
                startActivity(intent);
            }
        });

        messages.addView(v);

//        View message = getView().findViewById(R.id.message_one);
//        message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view2) {
//                inflater.inflate(R.layout.message_opened, (ViewGroup)getView().getParent());
//            }
//        });
    }
}
