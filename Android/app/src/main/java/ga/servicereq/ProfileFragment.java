package ga.servicereq;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private Button editProfile;
    private Button logout;
    public ImageView avatar;
    private View rootView;
    SharedPreferences pref;

    private static Boolean permissionGranted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_profile, container, false);
            LinearLayout mainContent = Objects.requireNonNull(rootView).findViewById(R.id.profile_main);
            PostsAdapter.getInstance(this.getContext()).setProfileBody(mainContent);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());

        editProfile = Objects.requireNonNull(getView()).findViewById(R.id.profile_editButton);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        logout = view.findViewById(R.id.profile_logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor prefEdit = pref.edit();
                PostsAdapter.clearPosts();
                prefEdit.clear();
                prefEdit.apply();
                Server.sendMessage("O;;");
                Server.clearMessages();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        TextView name = view.findViewById(R.id.profile_Name);
        String nameS = pref.getString("fn", "") + " " + pref.getString("ln", "");
        name.setText(nameS);

        RatingBar rating = view.findViewById(R.id.profile_rating);
        rating.setRating(pref.getFloat("rtg", 0f));

        avatar = view.findViewById(R.id.profile_Avatar);
        setAvatar();
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"SELECT IMAGE IN ACTION", Toast.LENGTH_SHORT).show();
                selectImage();
            }
        });

    }

    private void setAvatar() {
        String imageString = pref.getString("img","");
        if(!imageString.equals("NONE") && !imageString.equals("")) {
            String[] byteValues = imageString.substring(1, imageString.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];

            for (int i = 0, len = bytes.length; i < len; i++) {
                bytes[i] = Byte.parseByte(byteValues[i]);
            }
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image = Bitmap.createScaledBitmap(image, 400, 400, true);
            RoundDrawable roundDrawable = new RoundDrawable(image);
            avatar.setImageDrawable(roundDrawable);
        }
    }

    @Override
    public void onDestroyView() {
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView();
    }

    private void selectImage() {
        final CharSequence[] options = {"Camera", "Alege din Galeria foto", "Anulează"};
        final Context activity = Objects.requireNonNull(this.getContext());
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(rootView.getContext());
        builder.setTitle("Încarcă o imagine");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            while (!permissionGranted) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "fotografie.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Alege din Galeria foto")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent,"Alege o fotografie"), 2);
                } else if (options[item].equals("Anulează")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());
            String userId = preferences.getString("gid","");
            StringBuilder serverMessage = new StringBuilder("I;");
            serverMessage.append(userId).append(";");
            if(requestCode == 1) {
                if(resultCode == RESULT_OK) {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("fotografie.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    Bitmap image = BitmapFactory.decodeFile(f.getPath());
                    image = Bitmap.createScaledBitmap(image, 400,400, true);
                    RoundDrawable roundDrawable = new RoundDrawable(image);
                    avatar.setImageDrawable(roundDrawable);

                    image = roundDrawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG,80, stream);

                    StringBuilder imageString = new StringBuilder();
                    imageString.append("[");
                    for(byte b : stream.toByteArray())
                        imageString.append(b).append(",");
                    imageString.setLength(imageString.length()-1);
                    serverMessage.append(imageString.append("]"));
                    serverMessage.append(";;");
                    Server.sendMessage(serverMessage.toString());
                    SharedPreferences.Editor prefEdit= preferences.edit();
                    prefEdit.putString("img",imageString.toString());
                    prefEdit.apply();
                }
            }
            else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Bitmap image;
                    try {
                        image = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), selectedImage);
                        image = Bitmap.createScaledBitmap(image, 400,400, true);
                        RoundDrawable roundDrawable = new RoundDrawable(image);
                        avatar.setImageDrawable(roundDrawable);
//
                        image = roundDrawable.getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG,80, stream);

                        StringBuilder imageString = new StringBuilder();
                        imageString.append("[");
                        for(byte b : stream.toByteArray())
                            imageString.append(b).append(",");
                        imageString.setLength(imageString.length()-1);
                        serverMessage.append(imageString.append("]"));
                        serverMessage.append(";;");
                        Server.sendMessage(serverMessage.toString());
                        SharedPreferences.Editor prefEdit= preferences.edit();
                        prefEdit.putString("img",imageString.toString());
                        prefEdit.apply();

                    } catch (IOException e) {
                        Log.e("IMAGE_INTENT", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("UPLOAD_IMAGE", "Exception in onActivityResult : " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
            else permissionGranted = true;
        }
    }
}
