package com.fprs6.serviciosg3;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterComment;
import com.fprs6.serviciosg3.adapters.AdapterDocument;
import com.fprs6.serviciosg3.adapters.AdapterImage;
import com.fprs6.serviciosg3.objects.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class professionalProfileFragment extends Fragment {

    private String professionalUid;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference documentDBReference;
    private DatabaseReference imageDBReference;

    private ImageView profileProfessionalCoverImageIV;
    private ImageView profileProfessionalImageIV;
    private TextView profileProfessionalNameTV;
    private TextView profileProfessionalDescriptionTV;
    private TextView profileProfessionalGeneralDescriptionTV;
    //private TextView profileProfessionalImagesTV;
    private TextView profileProfessionalPhoneTV;
    private TextView profileProfessionalAddresTV;
    private TextView profileProfessionalMailTV;
    private TextView profileProfessionalValorationTV;
    private TextView profileProfessionalServiceAbstractTV;
    private RecyclerView commentsProfileRecyclerView;
    private RecyclerView viewDocumentsRecyclerProfileRV;
    private RecyclerView viewImagesRecyclerProfileRV;

    private FloatingActionButton fab_editProfessionalProfile;

    private List<ModelComment> commentList;
    private AdapterComment adapterComment;

    private List<ModelImage> imageList;
    private AdapterImage adapterImage;

    private List<ModelDocument> documentList;
    private AdapterDocument adapterDocument;


    public professionalProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_professional, container, false);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_professional_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        profileProfessionalCoverImageIV             = view.findViewById(R.id.profileProfessionalCoverImage);
        profileProfessionalImageIV                  = view.findViewById(R.id.profileProfessionalImage);
        profileProfessionalNameTV                   = view.findViewById(R.id.profileProfessionalName);
        profileProfessionalDescriptionTV            = view.findViewById(R.id.profileProfessionalDescription);
        profileProfessionalServiceAbstractTV        = view.findViewById(R.id.profileProfessionalServiceAbstract);
        profileProfessionalGeneralDescriptionTV     = view.findViewById(R.id.profileProfessionalGeneralDescription);
        profileProfessionalPhoneTV                  = view.findViewById(R.id.profileProfessionalPhone);
        profileProfessionalAddresTV                 = view.findViewById(R.id.profileProfessionalAddres);
        profileProfessionalMailTV                   = view.findViewById(R.id.profileProfessionalMail);
        profileProfessionalValorationTV             = view.findViewById(R.id.profileProfessionalValoration);

        viewImagesRecyclerProfileRV                 = view.findViewById(R.id.viewImagesRecyclerProfile);

        fab_editProfessionalProfile                 = view.findViewById(R.id.editProfessionalProfile);

        commentsProfileRecyclerView                 = view.findViewById(R.id.commentsProfileRecyclerView);
        commentsProfileRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        viewDocumentsRecyclerProfileRV              = view.findViewById(R.id.viewDocumentsRecyclerProfile);


        commentList = new ArrayList<>();
        documentList = new ArrayList<ModelDocument>();
        imageList = new ArrayList<>();



        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference professionalDBReference = firebaseDatabase.getReference("professionals");
        DatabaseReference userDBReference = firebaseDatabase.getReference("users");

        documentDBReference = firebaseDatabase.getReference("documents");
        imageDBReference = firebaseDatabase.getReference("images");

        fab_editProfessionalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfessionalIntent = new Intent(getActivity(), EditProfessionalActivity.class);
                startActivity(editProfessionalIntent);
            }
        });

        Query queryProfessional = professionalDBReference.orderByChild("professional_uid").equalTo(firebaseUser.getUid());
        queryProfessional.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    ModelProfessionals modelProfessionals = ds.getValue(ModelProfessionals.class);

                    profileProfessionalValorationTV.setText(""+modelProfessionals.getProfessional_valoration());
                    profileProfessionalServiceAbstractTV.setText(modelProfessionals.getService_abstract());
                    profileProfessionalGeneralDescriptionTV.setText(modelProfessionals.getService_description_general());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query queryUser = userDBReference.orderByChild("uid").equalTo(firebaseUser.getUid());
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
        Query queryMyDocuments = documentDBReference.orderByChild("uid").equalTo(firebaseUser.getUid());
        queryMyDocuments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelDocument modelDocument = ds.getValue(ModelDocument.class);
                    if(modelDocument.getUid().equals(firebaseUser.getUid())){
                        documentList.add(modelDocument);
                    }
                }
                adapterDocument = new AdapterDocument(getContext(), documentList);
                viewDocumentsRecyclerProfileRV.setAdapter(adapterDocument);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query queryMyImages = imageDBReference.orderByChild("uid").equalTo(firebaseUser.getUid());
        queryMyImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelImage modelImage = ds.getValue(ModelImage.class);
                    if(modelImage.getUid().equals(firebaseUser.getUid())){
                        imageList.add(modelImage);
                    }
                }
                adapterImage = new AdapterImage(getContext(), imageList);
                viewImagesRecyclerProfileRV.setAdapter(adapterImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadComments();
        return view;

    }
    private void loadComments(){

        DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("comments");
        commentsReference.orderByChild("commented").equalTo(firebaseUser.getUid());
        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                int cantCommets = 0;
                float totalValoration = 0;
                String result = "0.0";
                for(DataSnapshot ds:snapshot.getChildren()){

                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    if(modelComment.getCommented().equals(firebaseUser.getUid())){
                        commentList.add(modelComment);
                        cantCommets++;
                        totalValoration += modelComment.getValoration();

                        result = ""+totalValoration/cantCommets;

                        profileProfessionalValorationTV.setText(result.substring(0, 3));

                    }

                    adapterComment = new AdapterComment( getContext(), commentList);
                    commentsProfileRecyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}