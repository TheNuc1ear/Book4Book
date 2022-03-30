package com.infosys.b4b;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class User extends AppCompatActivity {

    private ImageButton profilePicture, editPreferences;
    private Button logout;
    private String[] items;
    private ActivityResultLauncher<String> gallery;
    private ActivityResultLauncher<Uri> camera;
    private File file;
    private Uri uri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        ImageButton profilePicture = findViewById(R.id.profilePicture);
        ImageButton editPreferences = findViewById(R.id.editPreference);
        Button logout = findViewById(R.id.logout);
        String[] items = getResources().getStringArray(R.array.DialogCameraGallery); //To get the names of the dialog items in values/arrays
        String packageName = this.getApplicationContext().getPackageName();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


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
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
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

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
