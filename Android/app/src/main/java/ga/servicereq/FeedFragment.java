package ga.servicereq;

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

public class FeedFragment extends Fragment {

    private PostsAdapter posts;
    private Button addPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_feed,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPost = Objects.requireNonNull(getView()).findViewById(R.id.main_addPost);

        LinearLayout mainContent = Objects.requireNonNull(getView()).findViewById(R.id.main_content);
        posts = new PostsAdapter(this.getContext(), mainContent);
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));
        posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));
        posts.add(new Post("Codrut", "Iftimie","12 minutes","To discover all of the alternative widget styles available from the support library, look at the R.style reference for fields that begin with Widget. (Ignore the styles that begin with Base_Widget.) Remember to replace all underscores with periods when using the style name in your resources."));

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });
    }
}
