package ga.servicereq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PostsAdapter posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mainContent = findViewById(R.id.main_content);
        posts = new PostsAdapter(this, mainContent);
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
    }
}
