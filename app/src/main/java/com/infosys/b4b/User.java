package com.infosys.b4b;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;

public class User extends AppCompatActivity {

    private ImageButton profilePicture, editPreferences;
    private Button logout;
    private String[] items;
    private ActivityResultLauncher<String> gallery;
    private ActivityResultLauncher<Uri> camera;
    private File file;
    private Uri uri;
    //FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton profilePicture = findViewById(R.id.profilePicture);
        ImageButton editPreferences = findViewById(R.id.editPreference);
        Button logout = findViewById(R.id.logout);
        String[] items = getResources().getStringArray(R.array.DialogCameraGallary); //To get the names of the dialog items in values/arrays
        String packageName = this.getApplicationContext().getPackageName();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file = new File(Environment.getExternalStorageDirectory(), "temp_images.jpg");
                uri = FileProvider.getUriForFile(User.this, packageName + ".provider", file);
                //Create an alert dialog to pick camera/gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(User.this);

                //To choose the items in the dialog, we need an onclick for each items.
                builder.setTitle(R.string.alertCameraGallery);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Camera")) {
                            camera.launch(uri);
                        }
                        else {
                            gallery.launch("image/*");
                        }
                    }

                });
                builder.show();

//                AlertDialog pick =
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(intent);
            }
        });

        //Activity for gallery
        gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profilePicture.setImageURI(result);
            }
        });

        //Activity for camera
        camera = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if(result){
                    profilePicture.setImageURI(uri);
                    boolean deleted = file.delete();
                    Log.i("File has been deleted", String.valueOf(deleted));
                }
            }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create an alert dialog to confirm logging out
                AlertDialog.Builder builder = new AlertDialog.Builder(User.this);

                builder.setMessage(R.string.alertLogout);
                builder.setPositiveButton(R.string.confirmYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        mAuth.signOut();
//                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                builder.setNegativeButton(R.string.confirmNo, null);
                builder.show();
            }
        });

        editPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
