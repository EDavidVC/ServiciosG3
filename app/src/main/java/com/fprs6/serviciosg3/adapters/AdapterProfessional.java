package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.*;
import com.fprs6.serviciosg3.R;
import com.fprs6.serviciosg3.objects.ModelProfessionals;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProfessional extends RecyclerView.Adapter<AdapterProfessional.professionalHolder> {
    private Context context;
    private List<ModelProfessionals> professionalsList;
    private dashboardActivity dsBoard;
    public AdapterProfessional(dashboardActivity dsBoard,Context context, List<ModelProfessionals> professionalsList) {
        this.context = context;
        this.professionalsList = professionalsList;
        this.dsBoard = dsBoard;
    }


    @NonNull
    @Override
    public professionalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.users_show_model, parent, false);
        return new professionalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull professionalHolder holder, int position) {

        DatabaseReference userDBReference = FirebaseDatabase.getInstance().getReference("users");
        Query queryUser = userDBReference.orderByChild("uid").equalTo(professionalsList.get(position).getProfessional_uid());

        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    holder.professionalNameTV.setText(modelUser.getUserName());
                    try {
                        Picasso.get().load(modelUser.getProfileImage()).into(holder.professionalImageIV);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_profile_black).into(holder.professionalImageIV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.professionalServiceAbstractTV.setText(professionalsList.get(position).getService_abstract());
        holder.professionalValorationTV.setText(""+professionalsList.get(position).getProfessional_valoration());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /*Intent intent = new Intent(context, professionalProfileService.class);
                intent.putExtra("p_uid", professionalsList.get(position).getProfessional_uid() );
                context.startActivity(intent);
                */
                dsBoard.showProfileOfProfessionalSelected(professionalsList.get(position).getProfessional_uid());
            }
        });

    }

    @Override
    public int getItemCount() {

        return professionalsList.size();
    }

    class professionalHolder extends RecyclerView.ViewHolder{
        private ImageView professionalImageIV;
        private TextView professionalNameTV, professionalValorationTV, professionalServiceAbstractTV;
        public professionalHolder(@NonNull View itemView) {
            super(itemView);

            professionalImageIV = itemView.findViewById(R.id.professionalImage);
            professionalNameTV = itemView.findViewById(R.id.professionalName);
            professionalServiceAbstractTV = itemView.findViewById(R.id.profesionalServiceAbstract);
            professionalValorationTV = itemView.findViewById(R.id.professionalValoration);

        }
    }
}
