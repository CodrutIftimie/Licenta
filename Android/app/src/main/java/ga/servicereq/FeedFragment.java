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

    private View rootView;
    private PostsAdapter posts;
    private Button addPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_feed, container, false);
            LinearLayout mainContent = Objects.requireNonNull(rootView).findViewById(R.id.main_content);
            posts = PostsAdapter.getInstance(this.getContext());
            posts.setFeedBody(mainContent);

            addPost = Objects.requireNonNull(rootView).findViewById(R.id.main_addPost);
            addPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), NewPostActivity.class);
                    startActivity(intent);
                }
            });
            posts.runUpdater();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
