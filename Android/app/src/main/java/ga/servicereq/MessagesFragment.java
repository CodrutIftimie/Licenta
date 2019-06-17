package ga.servicereq;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MessagesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View message = getView().findViewById(R.id.message_one);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                inflater.inflate(R.layout.message_opened, (ViewGroup)getView().getParent());
            }
        });
    }
}
