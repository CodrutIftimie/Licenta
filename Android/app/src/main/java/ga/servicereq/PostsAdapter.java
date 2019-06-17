package ga.servicereq;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PostsAdapter{

    private final LinearLayout body;
    private final Context context;
    private ArrayList<LinearLayout> posts;

    public PostsAdapter(Context context, View view) {
        this.context = context;
        body = (LinearLayout) view;
        posts = new ArrayList<>();
    }

    public void add(Post post) {
        LinearLayout toBeAdded = (LinearLayout)View.inflate(context, R.layout.main_post, null);

        TextView posterName = toBeAdded.findViewById(R.id.post_posterName);
        TextView postDate = toBeAdded.findViewById(R.id.post_time);
        TextView postDescription = toBeAdded.findViewById(R.id.post_description);

        String name = post.getFirstName() + " " + post.getLastName();
        posterName.setText(name);
        postDate.setText(post.getPostDate());
        postDescription.setText(post.getDescription());


        posts.add(toBeAdded);
        body.addView(toBeAdded);
    }
}
