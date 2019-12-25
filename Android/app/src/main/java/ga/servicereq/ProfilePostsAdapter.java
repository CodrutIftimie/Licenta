package ga.servicereq;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfilePostsAdapter {

    private final LinearLayout body;
    private Context context;
    private ArrayList<LinearLayout> posts;

    public ProfilePostsAdapter(Context context2, View view) {
        context = context2;
        body = (LinearLayout) view;
        posts = new ArrayList<>();
    }

    public void add(final Post post) {
        body.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout toBeAdded = (LinearLayout) View.inflate(context, R.layout.main_post, null);

                TextView posterName = toBeAdded.findViewById(R.id.post_posterName);
                TextView postDate = toBeAdded.findViewById(R.id.post_time);
                TextView postDescription = toBeAdded.findViewById(R.id.post_description);

                String name = post.getFirstName() + " " + post.getLastName();
                posterName.setText(name);
                postDate.setText(post.getPostDate());
                postDescription.setText(post.getDescription());

                posts.add(toBeAdded);
                body.addView(toBeAdded, 0);
            }
        });
    }
}
