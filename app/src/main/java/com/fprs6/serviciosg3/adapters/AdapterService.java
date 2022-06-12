package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.*;
import com.fprs6.serviciosg3.objects.ModelServices;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterService extends RecyclerView.Adapter<AdapterService.serviceHolder> {

    private Context context;
    private List<ModelServices> servicesList;
    private dashboardActivity dActivity;

    public AdapterService(dashboardActivity dActivity, Context context, List<ModelServices> servicesList) {
        this.context = context;
        this.servicesList = servicesList;
        this.dActivity = dActivity;
    }


    @NonNull
    @Override
    public serviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.services_shows_model, parent, false);
        return new serviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull serviceHolder holder, int position) {
        String serviceLogo = servicesList.get(position).getService_logo();
        String serviceName = servicesList.get(position).getType_name_es();
        String serviceDetails = servicesList.get(position).getDescription_es();

        holder.serviceName.setText(serviceName);
        holder.serviceDetails.setText(serviceDetails);

        try {
            Picasso.get().load(serviceLogo).placeholder(R.drawable.logo_appservice).into(holder.serviceLogo);
        }catch (Exception e){}

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dActivity.swithProfessionalViewList(serviceName, servicesList.get(position).getType_service());
            }
        });

    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    class serviceHolder extends RecyclerView.ViewHolder{
        private ImageView serviceLogo;
        private TextView serviceName, serviceDetails;
        public serviceHolder(@NonNull View itemView) {
            super(itemView);

            serviceLogo = itemView.findViewById(R.id.service_logo);
            serviceName = itemView.findViewById(R.id.name_service);
            serviceDetails = itemView.findViewById(R.id.details_service);

        }
    }
}
