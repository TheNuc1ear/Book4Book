package com.infosys.b4b;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {

    private ImageButton profilePicture;
    private Button logout;
    private String[] items;
    private ActivityResultLauncher<String> gallery; //Launchers for registerActivityForResult
    private ActivityResultLauncher<Intent> camera;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private adapterProfile adapter;
    private List<bookListing> allBookListing;
    private String currentUser;
    private TextView profilename;


    public profileFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment profile_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance() {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        logout = view.findViewById(R.id.logout);
        currentUser = mAuth.getInstance().getCurrentUser().getUid();
        profilename = view.findViewById(R.id.userName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userData");

        mDatabaseRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData user = snapshot.getValue(userData.class);
                profilename.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        items = getResources().getStringArray(R.array.DialogCameraGallery); //To get the names of the dialog items in values/arrays
        recyclerView = view.findViewById(R.id.recyclerView);
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures/");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userData");

        Task<Uri> url = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + currentUser).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(profilePicture).load(uri).into(profilePicture);
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create an alert dialog to pick camera/gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //To choose the items in the dialog, we need an onclick for each items.
                builder.setTitle(R.string.alertCameraGallery);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Camera")) {
                            //Call intent to take picture with camera
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            camera.launch(cameraIntent);
                        }

                        else {
                            gallery.launch("image/*");
                        }
                    }

                });
                builder.show();

            }
        });

        //Activity for gallery (This is basically the new startActivityForResult)
        gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profilePicture.setImageURI(result);
                uploadPicture(result);
            }
        });

        //Activity for camera
        camera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    Uri bitmapToUri = getImageUri(getContext(),bitmap);
                    profilePicture.setImageURI(bitmapToUri);
                    uploadPicture(bitmapToUri);
                }
            }
        });

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
                        startActivity(new Intent(getActivity(), loginActivity.class));
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton(R.string.confirmNo, null);
                builder.show();
            }
        });

        allBookListing = new ArrayList<>();
        //Initialise Firebase reference for all book listings
        mDatabaseRef = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Booklisting");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()){
                    bookListing change = snap.getValue(bookListing.class);
                    Log.i("UID",change.getUseruid());
                    Log.i("UID current user", currentUser);
                    //Only add to allBookListing if the listing belongs to the current user
                    if(change.getUseruid().equals(currentUser)) {
                        allBookListing.add(change);
                    }
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
        adapter = new adapterProfile(allBookListing);
        recyclerView.setAdapter(adapter);
    }

    //Upload image to firebase storage
    private void uploadPicture(Uri uri){

        StorageReference fileReference = mStorageRef.child(currentUser);

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
                });
    }

    //Changing from bitmap to URI (For uploading image to firebase)
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}