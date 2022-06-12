package com.fprs6.serviciosg3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fprs6.serviciosg3.objects.ModelProfessionals;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.fprs6.serviciosg3.professionalTools.detailsChanger;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String storagePath = "User_Profile_n_Cover/";

    private ImageView profileImageIV;
    private ImageView profileCoverImageIV;
    private TextView userNameTV;
    private TextView userDescriptionTV;
    private TextView userPhoneTV;
    private TextView userAddresTV;
    private TextView userMailTV;
    private TextView userAgeOldTV;

    private FloatingActionButton fab;

    private ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private String cameraPermissions[];
    private String storagePermissions[];

    private Uri image_uri;

    private String profileOrCoverPhoto;

    private ModelUser modelUser;
    private ModelProfessionals modelProfessionals;

    private Switch profileIsProfessionalCheck;

    public ProfileFragment() {
        // Required empty public constructor
    }
    public ProfileFragment(ModelProfessionals modelProfessionals) {
        // Required empty public constructor
        this.modelProfessionals = modelProfessionals;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();


        profileImageIV      = view.findViewById(R.id.profileImage);
        profileCoverImageIV = view.findViewById(R.id.profileCoverImage);
        userNameTV          = view.findViewById(R.id.profileUserName);
        userDescriptionTV   = view.findViewById(R.id.profileUserDescription);
        userPhoneTV         = view.findViewById(R.id.profileUserPhone);
        userAddresTV        = view.findViewById(R.id.profileUserAddres);
        userMailTV          = view.findViewById(R.id.profileUserMail);
        userAgeOldTV        = view.findViewById(R.id.profileUserAgeOld);

        fab = view.findViewById(R.id.fab);
        profileIsProfessionalCheck = view.findViewById(R.id.profileIsProfessionalCheck);

        pd = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    modelUser = ds.getValue(ModelUser.class);


                    userNameTV.setText(modelUser.getUserName());
                    userDescriptionTV.setText(modelUser.getDescription());
                    userAddresTV.setText(modelUser.getAddress());
                    userPhoneTV.setText(modelUser.getPhone());
                    userAgeOldTV.setText(modelUser.getAgeOld());
                    userMailTV.setText(modelUser.getEmail());

                    profileIsProfessionalCheck.setChecked(modelProfessionals.isActived());

                    try {
                        Picasso.get().load(modelUser.getProfileImage()).into(profileImageIV);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_profile_black).into(profileImageIV);
                    }
                    try {
                        Picasso.get().load(modelUser.getProfileCoverImage()).into(profileCoverImageIV);
                    }catch (Exception e){

                    }

                    //Deveria Agregar Una imagen o un proceso pero no lo hare por ahora
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });

        profileIsProfessionalCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProfessional();
            }
        });

        return view;
    }
    public void setProfessionalCheck(ModelProfessionals model){
        profileIsProfessionalCheck.setChecked(model.isActived());
    }
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }
    private void showEditProfileDialog () {
        String options [] = {"Editar Perfil" , "Editar Portada", "Editar Nombre", "Editar Descripcion", "Editar Telefono", "Editar Direccion"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Editar Datos");
        builder.setItems(options, new DialogInterface.OnClickListener (){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    pd.setMessage("Actualizando Perfil");
                    profileOrCoverPhoto = "profileImage";
                    showImagePicDialog();
                }
                else if (which == 1){
                    pd.setMessage("Actualizando Portada");
                    profileOrCoverPhoto = "profileCoverImage";
                    showImagePicDialog();
                }
                else if (which == 2){
                    pd.setMessage("Actualizando Nombre");
                    showUserEditInformation("userName");
                }
                else if (which == 3){
                    pd.setMessage("Actualizando Descripcion");
                    showUserEditInformation("description");
                }
                else if (which == 4){
                    pd.setMessage("Actualizando Telefono");
                    showUserEditInformation("phone");
                }
                else if (which == 5){
                    pd.setMessage("Actualizando direccion");
                    showUserEditInformation("address");
                }
            }
        });
        builder.create().show ();
    }
    private void showUserEditInformation(String dbKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar Dato");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText editText = new EditText(getActivity());
        editText.setHint("Informacion");

        linearLayout.addView(editText);

        builder.setView(linearLayout);
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(dbKey, value);

                    databaseReference.child(firebaseUser.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Actualizado Correctamente...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {
                    Toast.makeText(getActivity(), "Ingrese Nuevo Valor por favor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void showImagePicDialog () {
        String options [] = {"Camara" , "Desde Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cargar Imagen");
        builder.setItems(options, new DialogInterface.OnClickListener (){
            @Override
            public void onClick (DialogInterface dialog, int which)  {
                if (which == 0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                }
                else if (which == 1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if( cameraAccept && writeStorageAccept){
                        pickFromCamera();
                    }else{
                        Toast.makeText(getActivity(), "Porfavor habilitar los permisos a la camara y de almacenamiento", Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if( writeStorageAccept){
                        pickFromGallery();
                    }else{
                        Toast.makeText(getActivity(), "Porfavor habilitar los permisos de almacenamiento", Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                upLoadProfileCoverPhoto(image_uri);
            }
            if(requestCode  == IMAGE_PICK_CAMERA_CODE){
                upLoadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void upLoadProfileCoverPhoto(final Uri uri) {
        pd.show();
        String filePathAndName = storagePath+""+profileOrCoverPhoto+"_"+firebaseUser.getUid();
        StorageReference SR = storageReference.child(filePathAndName);
        SR.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if(uriTask.isSuccessful()){
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profileOrCoverPhoto, downloadUri.toString());
                            databaseReference.child(firebaseUser.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Imagen Cargada...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Error al cargar Imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Ocurrio un Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraContent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraContent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraContent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }
    private void checkProfessional(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Mensaje");
        alertDialog.setMessage("¿Esta Seguro?\nEditar Estatus Profesional");
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new detailsChanger().changeProfessionalStatus(profileIsProfessionalCheck.isChecked(), modelUser);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(profileIsProfessionalCheck.isChecked()){
                   profileIsProfessionalCheck.setChecked(false);
                }else{
                    profileIsProfessionalCheck.setChecked(true);
                }
            }
        });
        alertDialog.show();
    }

}