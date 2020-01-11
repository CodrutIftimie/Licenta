package ga.servicereq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PostsAdapter {

    private LinearLayout feedBody;
    private LinearLayout profileBody;
    private Context context;
    private ArrayList<LinearLayout> posts;
    private static ArrayList<Post> toAdd;
    private static ArrayList<Post> profileToAdd;
    private boolean updaterRunning = false;

    private static PostsAdapter instance;

    public synchronized static PostsAdapter getInstance(Context context) {
        if (instance == null)
            instance = new PostsAdapter(context);
        return instance;
    }

    private PostsAdapter() {
    }

    private PostsAdapter(Context context2) {
        context = context2;
        posts = new ArrayList<>();
    }

    public void setFeedBody(View view) {
        this.feedBody = (LinearLayout) view;
    }

    public void setProfileBody(View view) {
        this.profileBody = (LinearLayout) view;
    }

    public void add(final Post post) {
        feedBody.post(new Runnable() {
            @Override
            public void run() {
                final LinearLayout toBeAdded = (LinearLayout) View.inflate(context, R.layout.main_post, null);

                TextView posterName = toBeAdded.findViewById(R.id.post_posterName);
                TextView postDate = toBeAdded.findViewById(R.id.post_time);
                TextView postDescription = toBeAdded.findViewById(R.id.post_description);

                final String name = post.getFirstName() + " " + post.getLastName();
                posterName.setText(name);
                postDate.setText(post.getPostDate());
                postDescription.setText(post.getDescription());

                toBeAdded.setTag(post.getPosterId());

                posts.add(toBeAdded);

                toBeAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
                        String senderId = preferences.getString("gid", "");
                        Intent i = new Intent(feedBody.getContext(), MessagingActivity.class);
                        i.putExtra("senderId",(String)toBeAdded.getTag());
                        i.putExtra("fname", post.getFirstName());
                        i.putExtra("lname", post.getLastName());
                        context.startActivity(i);
                    }
                });
                feedBody.addView(toBeAdded, 2);
            }
        });
    }

    public boolean profileAdd(final Post post) {
        if (profileBody != null) {
            profileBody.post(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
                    String userFName = preferences.getString("fn", "");
                    String userLName = preferences.getString("ln", "");

                    if (userFName.equals(post.getFirstName()) && userLName.equals(post.getLastName())) {
                        LinearLayout toBeAdded = (LinearLayout) View.inflate(context, R.layout.main_post, null);

                        TextView posterName = toBeAdded.findViewById(R.id.post_posterName);
                        TextView postDate = toBeAdded.findViewById(R.id.post_time);
                        TextView postDescription = toBeAdded.findViewById(R.id.post_description);

                        final String name = post.getFirstName() + " " + post.getLastName();
                        posterName.setText(name);
                        postDate.setText(post.getPostDate());
                        postDescription.setText(post.getDescription());

                        toBeAdded.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context,"Test onClick " + name + " " + post.getPostDate(), Toast.LENGTH_LONG).show();
                            }
                        });

                        profileBody.addView(toBeAdded, 2);
                    }
                }
            });
            return true;
        }
        return false;
    }

    public static void serverAdd(Post post) {
        if (toAdd == null) {
            toAdd = new ArrayList<>();
            profileToAdd = new ArrayList<>();
        }
        toAdd.add(post);
        profileToAdd.add(post);
    }

    public void runUpdater() {
        if (!updaterRunning) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (toAdd != null) {
                                while (!toAdd.isEmpty())
                                    add(toAdd.remove(0));
                            }
                            if (profileToAdd != null) {
                                while (!profileToAdd.isEmpty())
                                    if (profileAdd(profileToAdd.get(0)))
                                        profileToAdd.remove(0);
                                    else break;
                            }
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            updaterRunning = true;
        }
    }

    public static void clearPosts() {
        toAdd = null;
    }
}
