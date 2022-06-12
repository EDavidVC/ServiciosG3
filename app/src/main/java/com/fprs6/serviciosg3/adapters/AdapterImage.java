package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.R;
import com.fprs6.serviciosg3.objects.ModelImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.imageHolder> {

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Context context;
    private List<ModelImage> imageList;

    public AdapterImage(Context context, List<ModelImage> imageList) {
        this.context = context;
        this.imageList = imageList;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("images");
    }

    private void deleteDocument(String imageFileName, String keyImage){
        StorageReference storageReference = firebaseStorage.getReference("images/"+imageFileName);
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


        databaseReference.child(keyImage).setValue(new HashMap<>());


    }

    @NotNull
    @Override
    public imageHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_show_model, parent, false);
        return new imageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull imageHolder holder, int position) {
        String url = imageList.get(position).getImage_url();
        String image_uid = imageList.get(position).getUid();
        String imageFileName = imageList.get(position).getReference();
        String imageFileKey = imageList.get(position).getKey();

        try {
            Picasso.get().load(url).placeholder(R.drawable.logo_appservice).into(holder.imageItemView);
        }catch (Exception e){
            Picasso.get().load(R.drawable.logo_appservice).into(holder.imageItemView);
        }

        if(!image_uid.equals(firebaseUser.getUid())){
            holder.fabDeleteImageButton.setVisibility(View.GONE);
        }else {
            holder.fabDeleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDocument(imageFileName, imageFileKey);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class imageHolder extends RecyclerView.ViewHolder {
        private ImageView imageItemView;
        private FloatingActionButton fabDeleteImageButton;

        public imageHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageItemView = itemView.findViewById(R.id.imageItemView);
            fabDeleteImageButton = itemView.findViewById(R.id.deleteImageBtn);

        }
    }
}
