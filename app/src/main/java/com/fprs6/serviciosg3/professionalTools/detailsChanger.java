package com.fprs6.serviciosg3.professionalTools;

import androidx.annotation.NonNull;
import com.fprs6.serviciosg3.objects.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class detailsChanger {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    // Per Global data is necesary
    private int cantCommets = 0;
    private double totalValoration = 0;
    private double result = 0.0;
    private ModelProfessionals modelProfessionals;

    public detailsChanger(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference("professionals");

        modelProfessionals = new ModelProfessionals();
    }
    public void registerThisUser(String uid, ModelUser modelUser){
        HashMap<String, Object> structure = new HashMap<>();

        structure.put("email", modelUser.getEmail());
        structure.put("number_phone", modelUser.getPhone());
        structure.put("actived", false);
        structure.put("professional_cover_image", modelUser.getProfileCoverImage());
        structure.put("professional_image", modelUser.getProfileImage());
        structure.put("professional_name", modelUser.getUserName());
        structure.put("professional_uid", modelUser.getUid());
        structure.put("professional_valoration", 0.0);
        structure.put("referencies", "");
        structure.put("service_abstract", "Descripcion del Servicio");
        structure.put("service_description_general", "Descripcion General del Trabajo");
        structure.put("service_type", "Tipo no seleccionado");


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("professionals");
        reference.child(modelUser.getUid()).setValue(structure);
    }
    public void changeProfessionalStatus(Boolean bool, ModelUser modelUser){
        HashMap<String, Object> structure = new HashMap<>();

        structure.put("actived", bool);


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("professionals");
        reference.child(modelUser.getUid()).updateChildren(structure);

    }
    //This a temp Function
    public  void changeProfessionalBasicDetails(ModelProfessionals tempProfessionalModel){
        HashMap<String, Object> structure = new HashMap<>();

        structure.put("price_base", tempProfessionalModel.getPrice_base());
        structure.put("service_abstract", tempProfessionalModel.getService_abstract());
        structure.put("service_description_general", tempProfessionalModel.getService_description_general());
        structure.put("service_type", tempProfessionalModel.getService_type());


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("professionals");
        reference.child(firebaseUser.getUid()).updateChildren(structure);
    }

    public void confirmContract(ModelContract modelContract, String keyMessage){

        HashMap<String, Object> dataChangeStatus = new HashMap<>();

        dataChangeStatus.put("status", "confirmed");


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("chats");
        reference.child(keyMessage).child("contract_details").updateChildren(dataChangeStatus);


        String time_last_update = String.valueOf(System.currentTimeMillis());

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> contractStructure = new HashMap<>();

        contractStructure.put("professional", modelContract.getProfessional());
        contractStructure.put("client", modelContract.getClient());
        contractStructure.put("confirmed_for", modelContract.getConfirmed_for());
        contractStructure.put("status", modelContract.getStatus());
        contractStructure.put("details_contract", modelContract.getDetails_contract());
        contractStructure.put("price", modelContract.getPrice_contract());
        contractStructure.put("time_last_update", time_last_update);

        dbReference.child("contracts").push().setValue(contractStructure);
    }
    public void calcelContract(String keyMessage){

        HashMap<String, Object> dataChangeStatus = new HashMap<>();

        dataChangeStatus.put("status", "canceled");


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("chats");
        reference.child(keyMessage).child("contract_details").updateChildren(dataChangeStatus);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
    }
    public void sendComment(ModelComment modelComment){
        String time_last_update = String.valueOf(System.currentTimeMillis());
        modelComment.setTime_comment(time_last_update);



        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        dbReference.child("comments").push().setValue(modelComment);

    }


}
