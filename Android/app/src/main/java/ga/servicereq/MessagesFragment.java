package ga.servicereq;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

public class MessagesFragment extends Fragment {

    LinearLayout messages;
    MessagesAdapter adapter;
    SharedPreferences preferences;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_messages, container, false);
            messages = Objects.requireNonNull(rootView).findViewById(R.id.preview_messages);
            preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
            adapter = new MessagesAdapter(getContext(),messages,this);
            int convsC = 0;

            Map<String, ?> convos = preferences.getAll();
            for(Map.Entry<String, ?> convo : convos.entrySet())
                if(convo.getKey().startsWith("CONVO")) {
                    Conversation convoObj = new Gson().fromJson(convo.getValue().toString(), Conversation.class);
                    adapter.add(convoObj);
                }

            adapter.runUpdater();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView();
    }
}
