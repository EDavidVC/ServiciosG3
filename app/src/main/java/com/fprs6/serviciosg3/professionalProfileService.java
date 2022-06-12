package com.fprs6.serviciosg3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterComment;
import com.fprs6.serviciosg3.adapters.AdapterImage;
import com.fprs6.serviciosg3.adapters.AdapterProfessional;
import com.fprs6.serviciosg3.objects.*;
import com.fprs6.serviciosg3.professionalTools.detailsChanger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class professionalProfileService extends AppCompatActivity {
    private String p_uid;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ImageView profileProfessionalCoverImageIV;
    private ImageView profileProfessionalImageIV;
    private TextView profileProfessionalNameTV;
    private TextView profileProfessionalDescriptionTV;
    private TextView profileProfessionalTypeServiceTV;
    private TextView profileProfessionalGeneralDescriptionTV;
    //private TextView profileProfessionalImagesTV;
    private TextView profileProfessionalPhoneTV;
    private TextView profileProfessionalAddresTV;
    private TextView profileProfessionalMailTV;
    private TextView profileProfessionalValorationTV;
    private TextView profileProfessionalServiceAbstractTV;
    private RecyclerView commentsRecyclerView;
    private RecyclerView imagenRecyclerView;

    private FloatingActionButton professionalMsgFab;

    private Button createANewComment;

    private LayoutInflater layoutInflater;
    private View commentLayout;
    private RatingBar commentRatingBar;
    private TextView valueCommentRating;
    private EditText commentText;

    private List<ModelComment> commentList;
    private AdapterComment adapterComment;

    private List<ModelImage> imageList;
    private AdapterImage adapterImage;

    ModelProfessionals modelProfessionals;

    public professionalProfileService(){}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_professional);

        p_uid = getIntent().getStringExtra("p_uid");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        profileProfessionalCoverImageIV             = findViewById(R.id.profileProfessionalCoverImageSERVICE);
        profileProfessionalImageIV                  = findViewById(R.id.profileProfessionalImageSERVICE);
        profileProfessionalNameTV                   = findViewById(R.id.profileProfessionalNameSERVICE);
        profileProfessionalDescriptionTV            = findViewById(R.id.profileProfessionalDescriptionSERVICE);
        profileProfessionalTypeServiceTV            = findViewById(R.id.profileProfessionalServiceTypeSERVICE);
        profileProfessionalServiceAbstractTV        = findViewById(R.id.profileProfessionalServiceAbstractSERVICE);
        profileProfessionalGeneralDescriptionTV     = findViewById(R.id.profileProfessionalGeneralDescriptionSERVICE);
        profileProfessionalPhoneTV                  = findViewById(R.id.profileProfessionalPhoneSERVICE);
        profileProfessionalAddresTV                 = findViewById(R.id.profileProfessionalAddresSERVICE);
        profileProfessionalMailTV                   = findViewById(R.id.profileProfessionalMailSERVICE);
        profileProfessionalValorationTV             = findViewById(R.id.profileProfessionalValorationSERVICE);
        commentsRecyclerView                        = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(professionalProfileService.this));

        imagenRecyclerView                          = findViewById(R.id.viewImagesRecyclerProfileServicer);
        createANewComment                           = findViewById(R.id.createANewComment);

        layoutInflater = getLayoutInflater();
        commentLayout = layoutInflater.inflate(R.layout.form_comment, null);


        commentRatingBar = commentLayout.findViewById(R.id.commentRating);
        valueCommentRating = commentLayout.findViewById(R.id.valueCommentRating);
        commentText = commentLayout.findViewById(R.id.commentText);

        commentList = new ArrayList<>();
        imageList = new ArrayList<>();


        modelProfessionals = new ModelProfessionals();
        commentRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                valueCommentRating.setText(""+commentRatingBar.getRating());
            }
        });
        createANewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateCommentForm();
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference professionalDBReference = firebaseDatabase.getReference("professionals");
        DatabaseReference userDBReference = firebaseDatabase.getReference("users");
        DatabaseReference imageDBReference = firebaseDatabase.getReference("images");

        Query queryProfessional = professionalDBReference.orderByChild("professional_uid").equalTo(p_uid);
        queryProfessional.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    modelProfessionals = ds.getValue(ModelProfessionals.class);

                    profileProfessionalValorationTV.setText(""+modelProfessionals.getProfessional_valoration());
                    profileProfessionalServiceAbstractTV.setText(modelProfessionals.getService_abstract());
                    profileProfessionalTypeServiceTV.setText(modelProfessionals.getService_type());
                    profileProfessionalGeneralDescriptionTV.setText(modelProfessionals.getService_description_general());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query queryUser = userDBReference.orderByChild("uid").equalTo(p_uid);
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    profileProfessionalNameTV.setText(modelUser.getUserName());
                    profileProfessionalDescriptionTV.setText(modelUser.getDescription());
                    profileProfessionalPhoneTV.setText("+51 "+modelUser.getPhone());
                    profileProfessionalAddresTV.setText(modelUser.getAddress());
                    profileProfessionalMailTV.setText(modelUser.getEmail());

                    try {
                        Picasso.get().load(modelUser.getProfileImage()).into(profileProfessionalImageIV);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_profile_black).into(profileProfessionalImageIV);
                    }
                    try {
                        Picasso.get().load(modelUser.getProfileCoverImage()).into(profileProfessionalCoverImageIV);
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query queryMyImages = imageDBReference.orderByChild("uid").equalTo(p_uid);
        queryMyImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelImage modelImage = ds.getValue(ModelImage.class);
                    if(modelImage.getUid().equals(p_uid)){
                        imageList.add(modelImage);
                    }
                }
                adapterImage = new AdapterImage(professionalProfileService.this, imageList);
                imagenRecyclerView.setAdapter(adapterImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Informacion");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        professionalMsgFab = findViewById(R.id.msj_fab);
        professionalMsgFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChatWithUser();
            }
        });

        loadComments();




    }
    private void getChatWithUser(){
        Intent chatIntent = new Intent(professionalProfileService.this, chatActivity.class);
        chatIntent.putExtra("chatContactUid", p_uid);
        startActivity(chatIntent);

    }
    private void showCreateCommentForm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(professionalProfileService.this);

        if(commentLayout.getParent() != null){
            ((ViewGroup)commentLayout.getParent()).removeView(commentLayout);
        }
        builder.setView(commentLayout);
        builder.setPositiveButton("Comentar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                float ratingValue = commentRatingBar.getRating();
                String commentTextTxt = commentText.getText().toString();

                ModelComment modelComment = new ModelComment(
                        p_uid,
                        firebaseUser.getUid(),
                        commentTextTxt,
                        ratingValue
                );
                new detailsChanger().sendComment(modelComment);


            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(professionalProfileService.this, "Celcelado", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void loadComments(){

        DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("comments");
        commentsReference.orderByChild("commented").equalTo(p_uid);
        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                int cantCommets = 0;
                float totalValoration = 0;
                String result = "0.0";
                for(DataSnapshot ds:snapshot.getChildren()){

                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    if(modelComment.getCommented().equals(p_uid)){
                        commentList.add(modelComment);
                        cantCommets++;
                        totalValoration += modelComment.getValoration();

                        result = ""+totalValoration/cantCommets;

                        profileProfessionalValorationTV.setText(result.substring(0, 3));

                    }

                    adapterComment = new AdapterComment( professionalProfileService.this, commentList);
                    commentsRecyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}