package com.infosys.b4b;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class upload_fragment extends Fragment {

    String[] genres = {"Romance", "Mystery", "Fantasy", "Fiction", "Horror", "Biography"};

    private static final int RESULT_OK = -1;
    private ImageView profilePic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    TextInputEditText booktitle;
    TextInputEditText bookdescribe;
    AutoCompleteTextView bookgenre;
    ArrayAdapter<String> adapterItems;



    Button submitBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_fragment, container, false);

        String TAG="o";


        profilePic = view.findViewById(R.id.iconIv);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        booktitle = (TextInputEditText) view.findViewById(R.id.booktitle);
        bookdescribe = (TextInputEditText) view.findViewById(R.id.bookdescribe);
        submitBtn = view.findViewById(R.id.submitBtn);
        bookgenre = view.findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_genres, genres);
        bookgenre.setAdapter(adapterItems);


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();

            }
        });

        bookgenre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String genre = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getContext(), "Genre: "+genre, Toast.LENGTH_SHORT).show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Button submit_button = (Button) view;
                //submit_button.setText("Submitted!");
                long timestamp = System.currentTimeMillis();
                String title = booktitle.getText().toString();
                String description = bookdescribe.getText().toString();
                String genre = bookgenre.getText().toString();
                bookListing listing = new bookListing(title,description,genre);
                /*HashMap<String, bookListing> hashMap = new HashMap<>();
                hashMap.put(listing.getListingId(), listing);*/
                //hashMap.put("title", ""+title);
                //hashMap.put("description", ""+description);
                //hashMap.put("genre", ""+genre);


                if (title.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                }
                else if (description.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
                }
                else if (genre.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a genre", Toast.LENGTH_SHORT).show();
                }
                else if (imageUri == null){
                    Toast.makeText(getContext(), "Please pick an image", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Book added successfully", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Booklisting").child(listing.getListingId()).setValue(listing);
                    DatabaseReference reference = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                    DatabaseReference reference2 = reference.child("BookListing");
                    DatabaseReference reference3 = reference2.child(listing.getListingId());
                    String postId = reference3.getKey();
                    //Store the postId as the listingId, then in our listingId create a getter for image
                    //using the listingId attribute to get the image from storage
                    uploadPicture(postId);
                }
            }
        });
        return view;
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select image"),1);
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK  && data!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            //uploadPicture();
        }
    }

    private void uploadPicture(String s) {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("uploading image...");
        pd.show();

        StorageReference mountainsRef = storageReference.child("images/" + s);


        mountainsRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(profilePic.findViewById(R.id.iconIv), "Image uploaded.", Snackbar.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getContext().getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();}
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                        double progressPercent = (100.00 * tasksnapshot.getBytesTransferred() / tasksnapshot.getTotalByteCount());
                        pd.setMessage("Progress:" +(int) progressPercent + "%");
                    }
                });

    }



}