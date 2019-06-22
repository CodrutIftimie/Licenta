package ga.servicereq;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public FeedFragment feedFragment;
    public ProfileFragment profileFragment;
    public MessagesFragment messagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(feedFragment == null)
            feedFragment = new FeedFragment();
        if(profileFragment == null)
            profileFragment = new ProfileFragment();
        if(messagesFragment == null)
            messagesFragment = new MessagesFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, feedFragment).commit();

        navigation.setOnNavigationItemSelectedListener(listener);
        navigation.setSelectedItemId(R.id.nav_feed);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_profile:
                            if(profileFragment == null)
                                profileFragment =  new ProfileFragment();
                            selectedFragment = profileFragment;
                            break;
                        case R.id.nav_feed:
                            if(feedFragment == null)
                                feedFragment =  new FeedFragment();
                            selectedFragment = feedFragment;
                            break;
                        case R.id.nav_messages:
                            if(messagesFragment == null)
                                messagesFragment =  new MessagesFragment();
                            selectedFragment = messagesFragment;
                    }
                    if(selectedFragment != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, selectedFragment).commit();
                    return true;
                }
            };
}
