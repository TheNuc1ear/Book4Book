package com.infosys.b4b;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;

public class upload_fragment extends Fragment {
    // Most popular book genres
    String[] genres = {"Action and Adventure", "Classics", "Comic Book /Graphic Novel", "Detective and Mystery"
            , "Fantasy", "Historical Fiction", "Horror", "Literary Fiction","Romance", "Sci-Fi",
            "Short Stories","Suspense and Thrillers", "Women's Fiction" , "Biographies/Autobiographies",
            "History", "Memoir", "Poetry", "Self-Help", "True Crime", "Others"};

    private static final int RESULT_OK = -1;
    private ImageView profilePic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    TextInputEditText booktitle;
    TextInputEditText bookdescribe;
    AutoCompleteTextView bookgenre;
    ArrayAdapter<String> adapterItems;
    boolean[] selectedgenre;
    ArrayList<Integer> genrelist = new ArrayList<>();





    Button submitBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_fragment, container, false);

        profilePic = view.findViewById(R.id.iconIv);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        booktitle = view.findViewById(R.id.booktitle);
        bookdescribe = view.findViewById(R.id.bookdescribe);
        submitBtn = view.findViewById(R.id.submitBtn);
        bookgenre = view.findViewById(R.id.auto_complete_txt);

        selectedgenre = new boolean[genres.length];


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();

            }
        });

        bookgenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    getContext()

                    );
                    builder.setTitle("Select genre");
                    builder.setCancelable(false);


                    builder.setMultiChoiceItems(genres, selectedgenre, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            if (b){
                                genrelist.add(i);
                                Collections.sort(genrelist);
                            }else{
                                genrelist.remove(i);
                            }
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int j=0; j<genrelist.size();j++){
                                stringBuilder.append((genres[genrelist.get(j)]));

                                if (j!=genrelist.size()-1){
                                    stringBuilder.append(", ");
                                }
                            }
                            bookgenre.setText(stringBuilder.toString());
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for(int j =0; j<selectedgenre.length;j++){
                                selectedgenre[j]=false;

                                genrelist.clear();

                                bookgenre.setText("");

                            }
                        }
                    });
                    builder.show();
                }
        });


                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = booktitle.getText().toString();
                        String description = bookdescribe.getText().toString();
                        String genre = bookgenre.getText().toString();
                        String[] arrOfStr = genre.split(",",-2);
                        ArrayList<String> bookarraylist = new ArrayList<>();

                        for(String s: arrOfStr){
                            bookarraylist.add(s);
                        }
                        bookListing listing = new bookListing(title, description, bookarraylist);


                        if (title.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                        } else if (description.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
                        } else if (genre.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter a genre", Toast.LENGTH_SHORT).show();
                        } else if (imageUri == null) {
                            Toast.makeText(getContext(), "Please pick an image", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Book added successfully", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Booklisting").child(listing.getListingId()).setValue(listing);
                            DatabaseReference reference = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                            DatabaseReference reference2 = reference.child("BookListing");
                            DatabaseReference reference3 = reference2.child(listing.getListingId());
                            String postId = reference3.getKey();
                            //Store the postId as the listingId, then in our listingId create a getter for image
                            //using the listingId attribute to get the image from storage
                            uploadPicture(postId);
                            FragmentTransaction fr = getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new home_Fragment());
                            fr.commit();
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(requireContext().getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();}
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