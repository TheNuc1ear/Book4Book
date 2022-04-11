package com.infosys.b4b;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class upload_fragment extends Fragment {
    // Most popular book genres
    public static String[] genres = {"All", "Action and Adventure", "Classics", "Comic Book /Graphic Novel", "Detective and Mystery"
            , "Fantasy", "Historical Fiction", "Horror", "Literary Fiction","Romance", "Sci-Fi",
            "Short Stories","Suspense and Thrillers", "Women's Fiction" , "Biographies/Autobiographies",
            "History", "Memoir", "Poetry", "Self-Help", "True Crime", "Others"};

    private static final int RESULT_OK = -1;
    private ImageView bookImage;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    TextInputEditText booktitle;
    TextInputEditText bookdescribe;
    AutoCompleteTextView bookgenre;
    boolean[] selectedgenre;
    ArrayList<Integer> genrelist = new ArrayList<>();
    Button submitBtn;
    private ActivityResultLauncher<String> gallery;
    private ActivityResultLauncher<Intent> camera;
    private String[] items;


    public upload_fragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment chat_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static upload_fragment newInstance() {
        upload_fragment fragment = new upload_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        bookImage = view.findViewById(R.id.uploadBookImage);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        items = getResources().getStringArray(R.array.DialogCameraGallery); //To get the names of the dialog items in values/arrays

        booktitle = view.findViewById(R.id.booktitle);
        bookdescribe = view.findViewById(R.id.bookdescribe);
        submitBtn = view.findViewById(R.id.submitBtn);
        bookgenre = view.findViewById(R.id.auto_complete_txt);

        selectedgenre = new boolean[genres.length];


        bookImage.setOnClickListener(new View.OnClickListener() {
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
                imageUri = result;
                bookImage.setImageURI(result);
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
                    imageUri = bitmapToUri;
                    bookImage.setImageURI(bitmapToUri);
                }
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
                        if (b) {
                            genrelist.add(i);
                            Collections.sort(genrelist);
                        } else {
                            genrelist.remove(i);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < genrelist.size(); j++) {
                            stringBuilder.append((genres[genrelist.get(j)]));

                            if (j != genrelist.size() - 1) {
                                stringBuilder.append(",");
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
                        for (int j = 0; j < selectedgenre.length; j++) {
                            selectedgenre[j] = false;

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
                String[] arrOfStr = genre.split(",", -2);
                ArrayList<String> bookarraylist = new ArrayList<>();

                for (String s : arrOfStr) {
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
                    daobookListing dao = new daobookListing();
                    dao.add(listing);
//                            FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Booklisting").child(listing.getListingId()).setValue(listing);
                    DatabaseReference reference = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                    DatabaseReference reference2 = reference.child("BookListing");
                    DatabaseReference reference3 = reference2.child(listing.getListingId());
                    String postId = reference3.getKey();
                    //Store the postId as the listingId, then in our listingId create a getter for image
                    //using the listingId attribute to get the image from storage
                    uploadPicture(postId);
                    FragmentTransaction fr = getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new home_Fragment());
                    fr.commit();
                }
            }
        });
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

    public void writeNewUser(String email, String username, String id) {
        daoUserData dao = new daoUserData();
        userData user = new userData(email, username, id);
        dao.add(user);

    }

    //Changing from bitmap to URI (For uploading image to firebase)
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



}