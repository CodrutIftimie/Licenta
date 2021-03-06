package ga.servicereq;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

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
                ImageView posterImage = toBeAdded.findViewById(R.id.post_posterAvatar);
                ImageView postImage = toBeAdded.findViewById(R.id.post_image);
                ImageView helperIcon = toBeAdded.findViewById(R.id.post_helper);
                ImageView solvedIcon = toBeAdded.findViewById(R.id.post_solved_icon);
                TextView location = toBeAdded.findViewById(R.id.post_location);

                if(post.isSolved())
                    solvedIcon.setImageResource(R.drawable.ic_solved);

                if(!post.isHelper())
                    helperIcon.setImageDrawable(null);

                if(!post.getLocation().equals("Not found"))
                    location.setText(Server.convertBackSpecialCharacters(post.getLocation()));
                else location.setText("");

                final String name = post.getFirstName() + " " + post.getLastName();
                posterName.setText(Server.convertBackSpecialCharacters(name));
                postDate.setText(post.getPostDate());
                postDescription.setText(Server.convertBackSpecialCharacters(post.getDescription()));
                if (!post.getProfileImageURL().equals("NONE")) {
                    String[] byteValues = post.getProfileImageURL().substring(1, post.getProfileImageURL().length() - 1).split(",");
                    byte[] bytes = new byte[byteValues.length];

                    for (int i = 0, len = bytes.length; i < len; i++) {
                        bytes[i] = Byte.parseByte(byteValues[i]);
                    }
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image = Bitmap.createScaledBitmap(image, 400, 400, true);
                    RoundDrawable roundDrawable = new RoundDrawable(image);
                    posterImage.setImageDrawable(roundDrawable);
                }

                if (!post.getDescriptionImageURL().equals("NONE")) {
                    addPostImage(post.getDescriptionImageURL(), postImage);
                }

                toBeAdded.setTag(post.getPosterId());

                posts.add(toBeAdded);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
                String currentUserId = preferences.getString("gid","");
                 if(!post.getPosterId().equals(currentUserId)) {
                     toBeAdded.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent i = new Intent(feedBody.getContext(), MessagingActivity.class);
                             i.putExtra("receiverId", post.getPosterId());
                             i.putExtra("fname", post.getFirstName());
                             i.putExtra("lname", post.getLastName());
                             i.putExtra("helper", post.isHelper());
                             i.putExtra("image", post.getProfileImageURL());
                             context.startActivity(i);
                         }
                     });
                 }
                feedBody.addView(toBeAdded, 1);
            }
        });
    }

    public boolean profileAdd(final Post post) {
        if (profileBody != null) {
            profileBody.post(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
                    String posterId = Objects.requireNonNull(preferences.getString("gid", ""));

                    if (posterId.equals(post.getPosterId())) {
                        final LinearLayout toBeAdded = (LinearLayout) View.inflate(context, R.layout.main_post, null);

                        TextView posterName = toBeAdded.findViewById(R.id.post_posterName);
                        TextView postDate = toBeAdded.findViewById(R.id.post_time);
                        TextView postDescription = toBeAdded.findViewById(R.id.post_description);
                        ImageView posterImage = toBeAdded.findViewById(R.id.post_posterAvatar);
                        ImageView postImage = toBeAdded.findViewById(R.id.post_image);
                        ImageView helperIcon = toBeAdded.findViewById(R.id.post_helper);
                        ImageView solvedIcon = toBeAdded.findViewById(R.id.post_solved_icon);
                        TextView location = toBeAdded.findViewById(R.id.post_location);

                        if(post.isSolved())
                            solvedIcon.setImageResource(R.drawable.ic_solved);

                        if(!post.isHelper())
                            helperIcon.setImageDrawable(null);

                        if(!post.getLocation().equals("Not found"))
                            location.setText(Server.convertBackSpecialCharacters(post.getLocation()));
                        else location.setText("");

                        final String name = post.getFirstName() + " " + post.getLastName();
                        posterName.setText(Server.convertBackSpecialCharacters(name));
                        postDate.setText(post.getPostDate());
                        postDescription.setText(Server.convertBackSpecialCharacters(post.getDescription()));

                        if (!post.getProfileImageURL().equals("NONE")) {
                            String[] byteValues = post.getProfileImageURL().substring(1, post.getProfileImageURL().length() - 1).split(",");
                            byte[] bytes = new byte[byteValues.length];

                            for (int i = 0, len = bytes.length; i < len; i++) {
                                bytes[i] = Byte.parseByte(byteValues[i]);
                            }
                            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            image = Bitmap.createScaledBitmap(image, 400, 400, true);
                            RoundDrawable roundDrawable = new RoundDrawable(image);
                            posterImage.setImageDrawable(roundDrawable);
                        }

                        if (!post.getDescriptionImageURL().equals("NONE")) {
                            addPostImage(post.getDescriptionImageURL(), postImage);
                        }

                        toBeAdded.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePost(post, toBeAdded);
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

    private void addPostImage(String image, ImageView imageView) {
        String[] byteValues = image.substring(1, image.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i]);
        }

        Bitmap imageB = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        DisplayMetrics displayMetrics = Server.getAppContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        imageB = Bitmap.createScaledBitmap(imageB, width,1000, true);
        imageView.setImageBitmap(imageB);
    }

    private void updatePost(final Post post, final View v) {
        final CharSequence[] options = {"Șterge postarea", "Marchează drept soluționată", "Anulează"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(v.getContext());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Șterge postarea")) {
                    Server.sendMessage("D;"+post.getPosterId()+";"+post.getPostDate()+";;");
                    profileBody.removeView(v);
                    Toast.makeText(Server.getAppContext(),"La repornirea aplicației veți vedea toate modificările!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else if(options[item].equals("Marchează drept soluționată")) {
                    if(!post.isSolved()) {
                        Server.sendMessage("S;" + post.getPosterId() + ";" + post.getPostDate() + ";;");
                        Toast.makeText(Server.getAppContext(), "La repornirea aplicației veți vedea modificările!", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(Server.getAppContext(), "Postarea este deja soluționată!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                }
                else if (options[item].equals("Anulează")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
