package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.MapsActivity;
import com.fprs6.serviciosg3.R;
import com.fprs6.serviciosg3.objects.ModelChat;
import com.fprs6.serviciosg3.objects.ModelContract;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.fprs6.serviciosg3.professionalTools.detailsChanger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.chatHolder>{

    private static final int MSG_TYPE_CONTACT = 0;
    private static final int MSG_TYP_USER = 1;

    private Context context;
    private List<ModelChat> chatList;
    private FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public chatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_CONTACT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_contact, parent, false);
            return new chatHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_user, parent, false);
            return new chatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull chatHolder holder, int position) {
        String messageText = chatList.get(position).getMessage();
        String timeSendingText = chatList.get(position).getTimeSending();
        String typeMessage = chatList.get(position).getType();
        double latitude = chatList.get(position).getLatitude();
        double longitude = chatList.get(position).getLongitude();
        String sender = chatList.get(position).getSender();
        String receiver = chatList.get(position).getReceiver();


        Calendar selfCal = Calendar.getInstance(Locale.US);
        selfCal.setTimeInMillis(Long.parseLong(timeSendingText));


        String DateSending = new SimpleDateFormat("dd/MM").format(selfCal.getTime()).toString();

        if(typeMessage.equals("location")){
            holder.typeLocationMessageLL.setVisibility(View.VISIBLE);
        }else if(typeMessage.equals("text")){
            holder.typeTextMessageTV.setVisibility(View.VISIBLE);
            holder.typeTextMessageTV.setText(messageText);
        }
        else if(typeMessage.equals("contract")){
            String statusContract = chatList.get(position).getModelContract().getStatus();
            String contractProfesionalUid = chatList.get(position).getModelContract().getProfessional();
            String contractClientUid = chatList.get(position).getModelContract().getClient();
            String contractDescription = chatList.get(position).getModelContract().getDetails_contract();
            String contractPrice = chatList.get(position).getModelContract().getPrice_contract();

            if(sender.equals(firebaseUser.getUid())){
                holder.typeStatusContractMessageLL.setVisibility(View.VISIBLE);
                if (statusContract.equals("confirmed")){
                    holder.stateContractTv.setText("Confirmado");
                }else if (statusContract.equals("canceled")){
                    holder.stateContractTv.setText("Concelado");
                }else {
                    holder.stateContractTv.setText("Esperando...");
                }

            }
            else if (receiver.equals(firebaseUser.getUid())){
                holder.typeConfirmContractMessageLL.setVisibility(View.VISIBLE);
                if (statusContract.equals("no_action")){

                    holder.confirmContract.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ModelContract modelContract = new ModelContract(
                                    contractProfesionalUid,
                                    contractClientUid,
                                    firebaseUser.getUid().toString(),
                                    "progress",
                                    contractDescription,
                                    contractPrice,
                                    0
                            );
                            new detailsChanger().confirmContract(modelContract, chatList.get(position).getKeyMessage());
                        }
                    });
                    holder.cancelContract.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new detailsChanger().calcelContract(chatList.get(position).getKeyMessage());
                        }
                    });
                }
                else {
                    holder.typeConfirmContractMessageLL.findViewById(R.id.typeStatusContractMessage).setVisibility(View.VISIBLE);
                    holder.typeConfirmContractMessageLL.findViewById(R.id.confirm_cancel_buttons).setVisibility(View.GONE);
                    //holder.typeStatusContractMessageLL.setVisibility(View.VISIBLE);
                    if (statusContract.equals("confirmed")){
                        holder.stateContractTv.setText("Confirmado");
                    }else if (statusContract.equals("canceled")){
                        holder.stateContractTv.setText("Concelado");
                    }
                }
            }
            DatabaseReference clientDBReference = FirebaseDatabase.getInstance().getReference("users");
            Query queryclient = clientDBReference.orderByChild("uid").equalTo(contractClientUid);

            queryclient.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        holder.contractMessageClientName.setText(modelUser.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference professDBReference = FirebaseDatabase.getInstance().getReference("users");
            Query queryProfess = professDBReference.orderByChild("uid").equalTo(contractProfesionalUid);

            queryProfess.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        holder.contractMessageProfessionalName.setText(modelUser.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            holder.contractMessageDescription.setText(contractDescription);
            holder.contractMessagePrice.setText(contractPrice);


        }
        else{
            Toast.makeText(context, "ERROR AL CARGAR MENSAJE", Toast.LENGTH_SHORT).show();
        }


        holder.messageTimeSendingTv.setText(DateSending);
        holder.viewOnMapButtonBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleIntent = new Intent(context, MapsActivity.class);
                googleIntent.putExtra("onlyView", true);
                googleIntent.putExtra("userName", "Se encuentra Aqui");
                googleIntent.putExtra("latitude", latitude);
                googleIntent.putExtra("longitude", longitude);
                context.startActivity(googleIntent);
            }
        });


        if(position == chatList.size()-1){
            if(chatList.get(position).isMessageStatus()){
                holder.messageStatusTv.setText("Entregado");

            }else {
                holder.messageStatusTv.setText("Enviando");
            }

        }else {
            holder.messageStatusTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYP_USER;
        }else {
            return MSG_TYPE_CONTACT;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class chatHolder extends RecyclerView.ViewHolder{
        private TextView typeTextMessageTV;
        private LinearLayout typeLocationMessageLL;
        private LinearLayout typeConfirmContractMessageLL;
        private LinearLayout typeStatusContractMessageLL;
        private TextView messageTimeSendingTv;
        private TextView messageStatusTv;
        private TextView stateContractTv;
        private Button viewOnMapButtonBT;
        private Button cancelContract;
        private Button confirmContract;

        //CONTRACT INFORMATION
        private TextView contractMessageProfessionalName;
        private TextView contractMessageClientName;
        private TextView contractMessageDescription;
        private TextView contractMessagePrice;

        public chatHolder(@NonNull View itemView) {
            super(itemView);
            typeTextMessageTV = itemView.findViewById(R.id.typeTextMessage);
            typeLocationMessageLL = itemView.findViewById(R.id.typeLocationMessage);
            messageTimeSendingTv = itemView.findViewById(R.id.timeSendingChat);
            messageStatusTv = itemView.findViewById(R.id.statusMessageChat);
            viewOnMapButtonBT = itemView.findViewById(R.id.viewOnMapButton);
            typeConfirmContractMessageLL = itemView.findViewById(R.id.typeConfirmContractMessage);
            typeStatusContractMessageLL = itemView.findViewById(R.id.typeStatusContractMessage);
            stateContractTv = itemView.findViewById(R.id.stateContract);

            cancelContract = itemView.findViewById(R.id.cancelContractButton);
            confirmContract = itemView.findViewById(R.id.confirmContractButton);

            contractMessageProfessionalName = itemView.findViewById(R.id.contractMessageProfessionalName);
            contractMessageClientName = itemView.findViewById(R.id.contractMessageClientName);
            contractMessageDescription = itemView.findViewById(R.id.contractMessageDescription);
            contractMessagePrice = itemView.findViewById(R.id.contractMessagePrice);

        }
    }
}
