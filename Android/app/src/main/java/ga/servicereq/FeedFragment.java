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
        //posts.add(new Post("Codrut", "Iftimie","10 minutes","Hahahahah asdfasdf asdf asdf a"));

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });
    }
}
