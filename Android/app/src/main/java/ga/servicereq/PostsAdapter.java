package ga.servicereq;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PostsAdapter{

    private final LinearLayout body;
    private Context context;
    private ArrayList<LinearLayout> posts;
    private static ArrayList<Post> toAdd;

    public PostsAdapter(Context context2, View view) {
        context = context2;
        body = (LinearLayout) view;
        posts = new ArrayList<>();

        if(toAdd != null) {
            for (Post post : toAdd) {
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
    }

    public void add(Post post) {

//        if(toAdd != null) {
//            for (Post spost : toAdd) {
//                LinearLayout toBeAdded = (LinearLayout)View.inflate(context, R.layout.main_post, null);
//
//                TextView posterName = toBeAdded.findViewById(R.id.post_posterName);
//                TextView postDate = toBeAdded.findViewById(R.id.post_time);
//                TextView postDescription = toBeAdded.findViewById(R.id.post_description);
//
//                String name = spost.getFirstName() + " " + spost.getLastName();
//                posterName.setText(name);
//                postDate.setText(spost.getPostDate());
//                postDescription.setText(spost.getDescription());
//
//                posts.add(toBeAdded);
//                body.addView(toBeAdded);
//            }
//        }

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

    public static void serverAdd(Post post) {
        if(toAdd == null)
            toAdd = new ArrayList<>();
        toAdd.add(post);
    }

    public static void clearPosts() {
        toAdd = null;
    }
}
