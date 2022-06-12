package com.fprs6.serviciosg3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterDocument;
import com.fprs6.serviciosg3.adapters.AdapterImage;
import com.fprs6.serviciosg3.objects.ModelDocument;
import com.fprs6.serviciosg3.objects.ModelImage;
import com.fprs6.serviciosg3.objects.ModelProfessionals;
import com.fprs6.serviciosg3.objects.ModelServices;
import com.fprs6.serviciosg3.professionalTools.detailsChanger;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EditProfessionalActivity extends AppCompatActivity {

    //Firebase Requeirments
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference serviceTypeDBReference;
    private DatabaseReference professionalDBReference;
    private DatabaseReference documentDBReference;
    private DatabaseReference imageDBReference;
    private StorageReference documentsStorageReference;
    private StorageReference imageStorageReference;

    //
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int DOCUMENT_PDF_CODE = 12;

    private String cameraPermissions[];
    private String storagePermissions[];

    private Uri image_uri;

    //Components View
    private Spinner udTypeServiceSPN;
    private EditText udAbstractServiceET;
    private EditText udReferencePriceET;
    private EditText udGeneralDescriptionServiceET;
    private RecyclerView viewDocumentsRecyclerRV;
    private RecyclerView viewImagesRecyclerRV;

    //Components View DELETE - CANCEL / ACCEPT - ADD Buttons
    private Button udAddImageBTN;
    private Button udAddDocumentBTN;
    private Button udCancelAllChangesBTN;
    private Button udAcceptAllChangesBTN;

    //Data for Spinner View
    private List<StringWithTag> spinnerItems;
    private ModelServices modelServices;

    //Data For Documents View Recycler
    private List<ModelDocument> documentList;
    private AdapterDocument adapterDocument;

    //Data For Images View Recycler
    private List<ModelImage> imageList;
    private AdapterImage adapterImage;

    //Create a Professional Model Edit Final
    private ModelProfessionals tempProfessionalModel;

    //Form upload Documents
    private LayoutInflater layoutInflater;
    private View formUploadDocumentLayout;
    private EditText documentToUploadName;
    private TextView documentToUploadFileName;
    private Button documentToUploadCharge;

    private Uri documentPdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_professional);

        udTypeServiceSPN = findViewById(R.id.udTypeServices);
        udAbstractServiceET = findViewById(R.id.udAbstractService);
        udReferencePriceET = findViewById(R.id.udReferencePrice);
        udGeneralDescriptionServiceET = findViewById(R.id.udGeneralDescriptionService);
        viewDocumentsRecyclerRV = findViewById(R.id.viewDocumentsRecycler);
        viewImagesRecyclerRV = findViewById(R.id.viewImagesRecycler);

        udAddImageBTN = findViewById(R.id.udAddImage);
        udAddDocumentBTN = findViewById(R.id.udAddDocument);
        udAcceptAllChangesBTN = findViewById(R.id.udAcceptAllChanges);
        udCancelAllChangesBTN = findViewById(R.id.udCancelAllChanges);

        spinnerItems = new ArrayList<StringWithTag>();
        tempProfessionalModel = new ModelProfessionals();

        documentList = new ArrayList<>();
        imageList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        serviceTypeDBReference = firebaseDatabase.getReference("service_type");
        professionalDBReference = firebaseDatabase.getReference("professionals");
        documentDBReference = firebaseDatabase.getReference("documents");
        imageDBReference = firebaseDatabase.getReference("images");

        firebaseStorage = FirebaseStorage.getInstance();
        documentsStorageReference = firebaseStorage.getReference();

        imageStorageReference = firebaseStorage.getReference();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        layoutInflater = getLayoutInflater();
        formUploadDocumentLayout = layoutInflater.inflate(R.layout.form_document_upload, null);
        //Form Document Properties
        documentToUploadName = formUploadDocumentLayout.findViewById(R.id.documentToUploadName);
        documentToUploadFileName = formUploadDocumentLayout.findViewById(R.id.documentToUploadFileName);
        documentToUploadCharge = formUploadDocumentLayout.findViewById(R.id.documentToUploadCharge);



        chargeData();

        udAddDocumentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDocumentForm();
            }
        });
        udAddImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectImageView();
            }
        });

        udCancelAllChangesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        udAcceptAllChangesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails();
                finish();
            }
        });
        documentToUploadCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDocument();
            }
        });
    }

    private void showSelectImageView() {
        if(!checkStoragePermission()){
            requestStoragePermission();
        }else {
            pickFromGallery();
        }
    }

    private void chargeData(){
        Query queryServicesList = serviceTypeDBReference;
        queryServicesList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinnerItems.clear();
                spinnerItems.add(new StringWithTag("default", "No Seleccionado"));
                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelServices = ds.getValue(ModelServices.class);
                    spinnerItems.add(new StringWithTag(modelServices.getType_service(), modelServices.getType_name_es()));
                }
                ArrayAdapter<StringWithTag> itemsForAdd = new ArrayAdapter<StringWithTag>(EditProfessionalActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
                udTypeServiceSPN.setAdapter(itemsForAdd);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query queryMyProfessionalDetails = professionalDBReference.orderByChild("professional_uid").equalTo(firebaseUser.getUid());
        queryMyProfessionalDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelProfessionals modelProfessionals = ds.getValue(ModelProfessionals.class);
                    udAbstractServiceET.setText(modelProfessionals.getService_abstract());
                    int idComponent = 0;
                    for(StringWithTag SWTObject : spinnerItems){
                        if(SWTObject.getId().equals(modelProfessionals.getService_type())){
                            udTypeServiceSPN.setSelection(idComponent,true);
                        }
                        idComponent++;
                    }
                    udReferencePriceET.setText(modelProfessionals.getPrice_base());
                    udGeneralDescriptionServiceET.setText(modelProfessionals.getService_description_general());
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
                adapterDocument = new AdapterDocument(EditProfessionalActivity.this, documentList);
                viewDocumentsRecyclerRV.setAdapter(adapterDocument);
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
                adapterImage = new AdapterImage(EditProfessionalActivity.this, imageList);
                viewImagesRecyclerRV.setAdapter(adapterImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateDetails(){
        //Get Values

        String serviceAbstractTxt = udAbstractServiceET.getText().toString();
        String serviceGeneralDescriptionTxt = udGeneralDescriptionServiceET.getText().toString();
        String serviceTypeTxt = spinnerItems.get(udTypeServiceSPN.getSelectedItemPosition()).id;
        String priceBaseTxt = udReferencePriceET.getText().toString();

        tempProfessionalModel = new ModelProfessionals(
                serviceAbstractTxt,
                serviceGeneralDescriptionTxt,
                serviceTypeTxt,
                priceBaseTxt
        );

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfessionalActivity.this);
        alertDialog.setTitle("¡Aviso!");
        String normalMessage = "¿Estas Seguro?";
        alertDialog.setMessage(normalMessage);
        alertDialog.setPositiveButton("Si, Estoy Seguro", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new detailsChanger().changeProfessionalBasicDetails(tempProfessionalModel);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(EditProfessionalActivity.this, "Operacíon Cancelada", Toast.LENGTH_SHORT).show();
            }
        });
        if(TextUtils.isEmpty(udAbstractServiceET.getText()) ||
                TextUtils.isEmpty(udReferencePriceET.getText()) ||
                TextUtils.isEmpty(udGeneralDescriptionServiceET.getText()) ||
                spinnerItems.get(udTypeServiceSPN.getSelectedItemPosition()).id == "default"
        ){
            normalMessage+= "\n\n Aviso:\nSe encontraron casillas vacias o sin seleccionar";
            alertDialog.setMessage(normalMessage);
            alertDialog.show();
        }else{
            new detailsChanger().changeProfessionalBasicDetails(tempProfessionalModel);
        }
    }
    public class StringWithTag{
        public String id;
        public String nameES;

        public StringWithTag(String id, String nameES) {
            this.id = id;
            this.nameES = nameES;
        }

        @Override
        public String toString(){
            return nameES;
        }
        public String getId(){
            return id;
        }
    }
    private void showCreateDocumentForm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfessionalActivity.this);

        if(formUploadDocumentLayout.getParent() != null){
            ((ViewGroup)formUploadDocumentLayout.getParent()).removeView(formUploadDocumentLayout);
        }
        builder.setView(formUploadDocumentLayout);
        builder.setPositiveButton("Subir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadDocumentToFirebase();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(EditProfessionalActivity.this, "Celcelado", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void uploadDocumentToFirebase() {

        if(documentPdf != null){
            String Key = documentDBReference.push().getKey();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo Documento");
            progressDialog.show();

            StorageReference reference = documentsStorageReference.child("documents").child("documents_"+Key+".pdf");
            reference.putFile(documentPdf)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri uri = uriTask.getResult();

                    String nameDocument = documentToUploadName.getText().toString().trim();

                    String reference = "documents_"+Key+".pdf";

                    ModelDocument modelDocument = new ModelDocument(reference,nameDocument, uri.toString(), firebaseUser.getUid(), Key);



                    documentDBReference.child(Key).setValue(modelDocument);
                    Toast.makeText(EditProfessionalActivity.this, "Documento Subido Exitosamente", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfessionalActivity.this, "Error al subir Documento", Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            Toast.makeText(EditProfessionalActivity.this, "Documento Invalido", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImageToFirebase(final Uri uri) {

        String Key = imageDBReference.push().getKey();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Subiendo Imagen");
        progressDialog.show();
        StorageReference reference = imageStorageReference.child("images").child("image_"+Key);
        reference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        Uri downloadUri = uriTask.getResult();

                        String download = downloadUri.toString();

                        String reference = "image_"+Key;

                        ModelImage modelImage = new ModelImage(download, firebaseUser.getUid(), Key , reference);

                        imageDBReference.child(Key).setValue(modelImage);
                        Toast.makeText(EditProfessionalActivity.this, "Imagen Subido Exitosamente", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfessionalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadDocument(){
        Intent documentSelect = new Intent();
        documentSelect.setType("application/pdf");
        documentSelect.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(documentSelect, "Seleccionar Documento"), 12);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(EditProfessionalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(EditProfessionalActivity.this,storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(EditProfessionalActivity.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(EditProfessionalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(EditProfessionalActivity.this, cameraPermissions, CAMERA_REQUEST_CODE);
    }
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = EditProfessionalActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraContent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraContent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraContent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
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
                        Toast.makeText(EditProfessionalActivity.this, "Porfavor habilitar los permisos a la camara y de almacenamiento", Toast.LENGTH_SHORT);
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
                        Toast.makeText(EditProfessionalActivity.this, "Porfavor habilitar los permisos de almacenamiento", Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                uploadImageToFirebase(image_uri);
            }
            if(requestCode  == IMAGE_PICK_CAMERA_CODE){
                uploadImageToFirebase(image_uri);
            }
            if (requestCode ==  DOCUMENT_PDF_CODE &&
                    data != null && data.getData() != null){
                documentToUploadFileName.setText(data.getDataString().substring(data.getDataString().lastIndexOf("%") + 3 ));
                documentPdf = data.getData();
            }
        }
    }
}