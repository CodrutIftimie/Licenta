package ga.servicereq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity {

    Button post;
    Button postImage;
    EditText description;
    TextView imageName;
    TextView locationTv;
    Spinner category;
    SharedPreferences prefs;
    static AsyncTask<String, String, String> task;
    private String imageBytes = "NONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        prefs = PreferenceManager.getDefaultSharedPreferences(Server.getAppContext());

        post = findViewById(R.id.newpost_postButton);
        postImage = findViewById(R.id.newpost_btn_uploadImage);
        imageName = findViewById(R.id.newpost_imageName);
        description = findViewById(R.id.newpost_editText);
        category = findViewById(R.id.newpost_spinner);
        locationTv = findViewById(R.id.newpost_locationTV);


        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Services.getArray());
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) category.getSelectedView()).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                if (description.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Nu poți adăuga o postare fără descriere", Toast.LENGTH_LONG).show();
                } else {

                    String location = locationTv.getText().toString().contains("Nu a putut fi")?"Not found":locationTv.getText().toString().substring(9);
                    String message = "N;" +
                            prefs.getString("gid", "") + ";" +
                            Server.formatSpecialCharacters(description.getText().toString()) + ";" +
                            Services.getById((int)category.getSelectedItemId()).toEnglishString() + ";" +
                            Server.formatSpecialCharacters(location) + ";" +
                            imageBytes + ";;";
                    post.setEnabled(false);

                    Server.sendMessage(message);

                    task = new AsyncTask<String, String, String>() {
                        @Override
                        protected void onPreExecute() {
                            post.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    post.setEnabled(true);
                                }
                            },200);
                        }

                        @Override
                        protected String doInBackground(String... params) {
                            int count = Server.messagesCount();
                            while (count == Server.messagesCount()) {
                                SystemClock.sleep(100);
                            }
                            final String response = Server.getMessage(Server.messagesCount() - 1);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.equals("SUCCESS") || response.equals(";SUCCESS"))
                                        Toast.makeText(NewPostActivity.this, "Postare adăugată!", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(NewPostActivity.this, "Eroare la adăugarea postării! Încearcă din nou.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return null;
                        }
                    }.execute();

                }
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setEnabled(false);
                selectImage();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                setLocation();
            }
        }).start();
    }



    private void setLocation() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},3);
        }
        else {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            setCityName(location);
        }
    }

    private void setCityName(Location location) {
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),10);
            for(Address address : addresses) {
                if(address.getLocality() != null && address.getLocality().length() > 0) {
                    city = "Locație: " + address.getLocality();
                    break;
                }
            }
        } catch (IOException e) {
            city = "Locație: Nu a putut fi găsită locația.";
        }
        final String finalCity = city;
        locationTv.post(new Runnable() {
            @Override
            public void run() {
                locationTv.setText(finalCity);
            }
        });

    }

    private void selectImage() {
        final CharSequence[] options = {"Camera", "Alege din Galeria foto", "Anulează"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(postImage.getContext());
        builder.setTitle("Încarcă o imagine");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    if (ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(NewPostActivity.this,
                                new String[]{Manifest.permission.CAMERA},1);
                    }

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "fotografie.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Alege din Galeria foto")) {

                    if (ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(NewPostActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent,"Alege o fotografie"), 2);
                } else if (options[item].equals("Anulează")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(requestCode == 1) {
                if(resultCode == RESULT_OK) {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("fotografie.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    imageName.setText("fotografie.jpg");
                    Bitmap image = BitmapFactory.decodeFile(f.getPath());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image = Bitmap.createScaledBitmap(image, 400,400, true);
                    image.compress(Bitmap.CompressFormat.JPEG,80, stream);

                    StringBuilder imageString = new StringBuilder();
                    imageString.append("[");
                    for(byte b : stream.toByteArray())
                        imageString.append(b).append(",");
                    imageString.setLength(imageString.length()-1);
                    imageString.append("]");

                    imageBytes = imageString.toString();
                    post.setEnabled(true);
                }
            }
            else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Bitmap image;
                    try {
                        String path = selectedImage.getPath();
                        if(path != null) {
                            String filename = path.substring(path.lastIndexOf("/") + 1);
                            if(filename.length()>15)
                                filename = filename.substring(0,15) + "...";
                            imageName.setText(filename);
                        }
                        image = MediaStore.Images.Media.getBitmap(postImage.getContext().getContentResolver(), selectedImage);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int width = displayMetrics.widthPixels;
                        image = Bitmap.createScaledBitmap(image, width,1000, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG,80, stream);


                        StringBuilder imageString = new StringBuilder();
                        imageString.append("[");
                        for(byte b : stream.toByteArray())
                            imageString.append(b).append(",");
                        imageString.setLength(imageString.length()-1);
                        imageString.append("]");

                        imageBytes = imageString.toString();
                        post.setEnabled(true);

                    } catch (IOException e) {
                        Log.e("IMAGE_INTENT", e.getMessage());
                        Toast.makeText(Server.getAppContext(), "Nu s-a putut încărca imaginea!", Toast.LENGTH_SHORT).show();
                        post.setEnabled(true);
                    }
                }
            }
            else if(requestCode == 3) {
                if(resultCode == RESULT_OK) {
                    LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    setCityName(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                }
            }
        } catch (Exception e) {
            Log.e("UPLOAD_POST_IMAGE", "Exception in onActivityResult : " + e.getMessage());
            Toast.makeText(Server.getAppContext(), "Nu s-a putut încărca imaginea!", Toast.LENGTH_SHORT).show();
            post.setEnabled(true);
        }
    }
}