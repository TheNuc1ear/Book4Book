package com.infosys.b4b;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile_fragment extends Fragment {

    private ImageButton profilePicture, editPreferences;
    private Button logout;
    private String[] items;
    private ActivityResultLauncher<String> gallery;
    private ActivityResultLauncher<Intent> camera;
    private ActivityResultLauncher<String> requestPermission;
    private File file;
    private Uri uri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<bookListing> allBookListing;
    private final int requestCode = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profile_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profile_fragment newInstance(String param1, String param2) {
        profile_fragment fragment = new profile_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePicture = view.findViewById(R.id.profilePicture);
        editPreferences = view.findViewById(R.id.editPreference);
        logout = view.findViewById(R.id.logout);
        items = getResources().getStringArray(R.array.DialogCameraGallery); //To get the names of the dialog items in values/arrays
        recyclerView = view.findViewById(R.id.recyclerView);
        String packageName = getActivity().getApplicationContext().getPackageName();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("profile_pictures");


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file = new File(Environment.getExternalStorageDirectory(), "temp_images.jpg");
                uri = FileProvider.getUriForFile(getActivity(), packageName + ".provider", file);
                //Create an alert dialog to pick camera/gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //To choose the items in the dialog, we need an onclick for each items.
                builder.setTitle(R.string.alertCameraGallery);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Camera")) {

                            //Checking for permissions to use camera app
//                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
//                                requestPermission.launch(Manifest.permission.CAMERA);
//                            }
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            camera.launch(cameraIntent);
                            //Checking if the device has a camera app
//                            if(cameraIntent.resolveActivity(getActivity().getPackageManager()) == null){
//                                Toast.makeText(getActivity(), "No camera app found!", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//
//                            }
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

        //Activity for gallery (This is basically the new startActivityForResult)
        gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profilePicture.setImageURI(result);
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            }
        });

        //Activity for camera
        camera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    profilePicture.setImageBitmap(bitmap);
                }
            }
        });

        //requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), )

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create an alert dialog to confirm logging out
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(R.string.alertLogout);
                builder.setPositiveButton(R.string.confirmYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton(R.string.confirmNo, null);
                builder.show();
            }
        });

        allBookListing = new ArrayList<>();
        //Initialise Firebase reference
        mDatabaseRef = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Booklisting");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()){
                    bookListing change = snap.getValue(bookListing.class);
                    allBookListing.add(change);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Using linear layout for the "My Listings" section, pass into recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BookAdapter(allBookListing);
        recyclerView.setAdapter(adapter);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPicture(){
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        fileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_LONG).show();}
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                        double progressPercent = (100.00 * tasksnapshot.getBytesTransferred() / tasksnapshot.getTotalByteCount());
                    }
                });
    }

}
