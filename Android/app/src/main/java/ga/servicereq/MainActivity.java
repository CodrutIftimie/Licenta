package ga.servicereq;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public FeedFragment feedFragment = new FeedFragment();
    public ProfileFragment profileFragment = new ProfileFragment();
    public MessagesFragment messagesFragment = new MessagesFragment();

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
                            selectedFragment = profileFragment;
                            break;
                        case R.id.nav_feed:
                            selectedFragment = feedFragment;
                            break;
                        case R.id.nav_messages:
                            selectedFragment = messagesFragment;
                    }
                    if(selectedFragment != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
