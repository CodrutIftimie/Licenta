package ga.servicereq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private Button editProfile;
    private Button logout;
    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());

        editProfile = Objects.requireNonNull(getView()).findViewById(R.id.profile_editButton);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        logout = view.findViewById(R.id.profile_logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor prefEdit = pref.edit();
                PostsAdapter.clearPosts();
                prefEdit.clear();
                prefEdit.apply();
                Server.sendMessage("O;");
                Intent intent = new Intent(getContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        TextView name = view.findViewById(R.id.profile_Name);
        String nameS = pref.getString("fn","") + " " + pref.getString("ln","");
        name.setText(nameS);

        RatingBar rating = view.findViewById(R.id.profile_rating);
        rating.setRating(pref.getFloat("rtg",0f));

    }
}
