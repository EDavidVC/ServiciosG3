package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.R;
import com.fprs6.serviciosg3.objects.ModelComment;
import com.fprs6.serviciosg3.objects.ModelDocument;
import com.fprs6.serviciosg3.pdfViwerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class AdapterDocument extends RecyclerView.Adapter<AdapterDocument.documentHolder> {

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Context context;
    private List<ModelDocument> documentList;

    public AdapterDocument(Context context, List<ModelDocument> documentList) {
        this.context = context;
        this.documentList = documentList;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("documents");


    }

    @NotNull
    @Override
    public documentHolder onCreateViewHolder( @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_document_show_model, parent, false);
        return new documentHolder(view);

    }

    @Override
    public void onBindViewHolder(@NotNull documentHolder holder, int position) {

        String documentUserId = documentList.get(position).getUid();
        String documentName = documentList.get(position).getName_document();
        String documentUrl = documentList.get(position).getUrl_document();
        String documentFileName = documentList.get(position).getReference();
        String documentKey = documentList.get(position).getKey();

        holder.documentCardNameTV.setText(documentName);
        if(!documentUserId.equals(firebaseUser.getUid())){
            holder.documentCardDeleteBtnBT.setVisibility(View.GONE);
        }else {
            holder.documentCardDeleteBtnBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDocument(documentFileName, documentKey);
                }
            });
        }
        holder.documentCardViewBtnBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pdfViwerInstant = new Intent(context, pdfViwerActivity.class);
                pdfViwerInstant.putExtra("urlPdf", documentUrl);
                context.startActivity(pdfViwerInstant);
            }
        });

    }
    private void deleteDocument(String documentFileName, String keyDocument){
        StorageReference storageReference = firebaseStorage.getReference("documents/"+documentFileName);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Borrado con exito", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, "Error al Borrar", Toast.LENGTH_SHORT).show();
            }
        });


        databaseReference.child(keyDocument).setValue(new HashMap<>());


    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    class documentHolder extends RecyclerView.ViewHolder{
        private TextView documentCardNameTV;
        private Button documentCardViewBtnBT;
        private Button documentCardDeleteBtnBT;
        public documentHolder(@NotNull View itemView) {
            super(itemView);

            documentCardNameTV = itemView.findViewById(R.id.documentCardName);
            documentCardViewBtnBT = itemView.findViewById(R.id.documentCardViewBtn);
            documentCardDeleteBtnBT = itemView.findViewById(R.id.documentCardDeleteBtn);

        }
    }
}
