package com.fprs6.serviciosg3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterChat;
import com.fprs6.serviciosg3.objects.ModelChat;
import com.fprs6.serviciosg3.objects.ModelComment;
import com.fprs6.serviciosg3.objects.ModelContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class chatActivity extends AppCompatActivity {

    private Toolbar chatToolbarTB;
    private ImageView chatContactProfileIV;
    private TextView chatContactNameTV;
    private TextView chatContactStatusTV;

    private RecyclerView chatRecyclerViewRV;
    private EditText chatMessageET;
    private ImageView chatSendButtonIV;
    private ImageView chatSendLocationButtonIV;
    private ImageView chatSendContractButtonIV;

    private ValueEventListener statusListener;
    private DatabaseReference messageStatusDBReference;

    private List<ModelChat> chatList;
    private AdapterChat adapterChat;

    private List<ModelComment> commentList;



    private String chatContactUid;
    private String chatMyUid;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    private DatabaseReference dbReferenceUsers;

    //For Test No oficial
    private View contractLayout;
    private CheckBox contractRoleClient;
    private CheckBox contractRoleProfessional;
    private EditText contractDetails;
    private EditText contractPriceDetails;
    private LayoutInflater layoutInflater;

    @Override
    protected void onPause() {
        super.onPause();
        messageStatusDBReference.removeEventListener(statusListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContactUid = getIntent().getStringExtra("chatContactUid");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        dbReferenceUsers = firebaseDatabase.getReference("users");

        chatToolbarTB = findViewById(R.id.chatToolbar);
        chatContactProfileIV = findViewById(R.id.chatContactProfile);
        chatContactStatusTV = findViewById(R.id.chatContactStatus);
        chatContactNameTV = findViewById(R.id.chatContactName);

        chatRecyclerViewRV = findViewById(R.id.chatRecyclerView);
        chatMessageET = findViewById(R.id.chatMessage);
        chatSendButtonIV = findViewById(R.id.chatSendButton);
        chatSendLocationButtonIV = findViewById(R.id.chatSendLocationButton);
        chatSendContractButtonIV = findViewById(R.id.chatSendContractButton);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        chatRecyclerViewRV.setHasFixedSize(true);
        chatRecyclerViewRV.setLayoutManager(linearLayoutManager);

        chatMyUid = firebaseUser.getUid();

        //Initialice View Test
        layoutInflater = getLayoutInflater();

        contractLayout = layoutInflater.inflate(R.layout.form_contract_details, null);
        contractRoleClient = contractLayout.findViewById(R.id.contractDetailsRoleClient);
        contractRoleProfessional = contractLayout.findViewById(R.id.contractDetailsRoleProfessional);
        contractDetails = contractLayout.findViewById(R.id.contractDetailsDescription);
        contractPriceDetails = contractLayout.findViewById(R.id.contractDetailsPrice);

        contractRoleProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contractRoleClient.isChecked()) contractRoleClient.setChecked(false);
            }
        });
        contractRoleClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contractRoleProfessional.isChecked()) contractRoleProfessional.setChecked(false);
            }
        });


        Query userQuery = dbReferenceUsers.orderByChild("uid").equalTo(chatContactUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String chatContactNameTxt = ds.child("userName").getValue().toString();
                    String chatContactProfileTxt = ds.child("profileImage").getValue().toString();

                    chatContactNameTV.setText(chatContactNameTxt);

                    try {
                        Picasso.get().load(chatContactProfileTxt).placeholder(R.drawable.ic_default_profile_black).into(chatContactProfileIV);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_profile_black).into(chatContactProfileIV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setSupportActionBar(chatToolbarTB);
        chatToolbarTB.setTitle("");

        chatSendButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = chatMessageET.getText().toString().trim();
                if(TextUtils.isEmpty(messageText)){
                    Toast.makeText(chatActivity.this, "Mensaje Vacio", Toast.LENGTH_SHORT).show();
                }else{
                    sendMenssage(messageText);
                }
            }
        });
        chatSendLocationButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleIntent = new Intent(chatActivity.this, MapsActivity.class);
                googleIntent.putExtra("chatContactUid",chatContactUid);
                googleIntent.putExtra("chatMyUid",chatMyUid);
                startActivity(googleIntent);
            }
        });
        chatSendContractButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateContractDetails();
            }
        });

        DatabaseReference myDBReference = FirebaseDatabase.getInstance().getReference("chat_list").child(chatMyUid).child(chatContactUid);
        myDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    myDBReference.child("id").setValue(chatContactUid);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference contactDBReference = FirebaseDatabase.getInstance().getReference("chat_list").child(chatContactUid).child(chatMyUid);
        contactDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    contactDBReference.child("id").setValue(chatMyUid);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        readMessages();

        checkStatusMessage();
    }

    private void checkStatusMessage() {
        messageStatusDBReference = FirebaseDatabase.getInstance().getReference("chats");
        statusListener = messageStatusDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    if(modelChat.getReceiver().equals(chatMyUid) && modelChat.getSender().equals(chatContactUid)){
                        HashMap<String, Object> checkStatus = new HashMap<>();
                        checkStatus.put("messageStatus", true);
                        ds.getRef().updateChildren(checkStatus);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessages(){
        chatList = new ArrayList<>();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    ModelContract modelContract = ds.child("contract_details").getValue(ModelContract.class);
                    modelChat.setKeyMessage(ds.getKey());
                    modelChat.setModelContract(modelContract);

                    if(modelChat.getReceiver().equals(""+chatMyUid) && modelChat.getSender().equals(""+chatContactUid) ||
                            modelChat.getReceiver().equals(""+chatContactUid) && modelChat.getSender().equals(""+chatMyUid)){
                        chatList.add(modelChat);
                    }
                    adapterChat = new AdapterChat(chatActivity.this, chatList);
                    adapterChat.notifyDataSetChanged();

                    chatRecyclerViewRV.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMenssage(String message){

        String timeSending = String.valueOf(System.currentTimeMillis());

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> messageStructure = new HashMap<>();
        messageStructure.put("sender", chatMyUid);
        messageStructure.put("receiver", chatContactUid);
        messageStructure.put("message", message);
        messageStructure.put("messageStatus", false);
        messageStructure.put("timeSending", timeSending);
        messageStructure.put("type", "text");
        dbReference.child("chats").push().setValue(messageStructure);

        chatMessageET.setText("");
    }
    private void sendContractMessage(ModelContract modelContract){
        String timeSending = String.valueOf(System.currentTimeMillis());

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> messageStructure = new HashMap<>();
        messageStructure.put("sender", chatMyUid);
        messageStructure.put("receiver", chatContactUid);
        messageStructure.put("contract_details", modelContract);
        messageStructure.put("messageStatus", false);
        messageStructure.put("timeSending", timeSending);
        messageStructure.put("type", "contract");

        dbReference.child("chats").push().setValue(messageStructure);

    }
    private void generateContractDetails(){
        AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.this);

        if(contractLayout.getParent() != null){
            ((ViewGroup)contractLayout.getParent()).removeView(contractLayout);
        }
        builder.setView(contractLayout);
        builder.setPositiveButton("Enviar Contrato", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String client = "no_defined";
                String professional = "no_defined";
                if(contractRoleClient.isChecked()) {
                    client = chatMyUid;
                    professional = chatContactUid;
                }
                else if (contractRoleProfessional.isChecked()){
                    client = chatContactUid;
                    professional = chatMyUid;
                }

                String details = "no_defined";
                if(!TextUtils.isEmpty(contractDetails.getText().toString())) details = contractDetails.getText().toString();

                String priceDetails = "no_defined";
                if(!TextUtils.isEmpty(contractPriceDetails.getText().toString())) priceDetails = contractPriceDetails.getText().toString().trim();

                ModelContract modelContract = new ModelContract(
                        professional, //Professional UID
                        client, //User UID
                        details, //Description
                        priceDetails
                );
                sendContractMessage(modelContract);

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(chatActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onStart() {
        checkStatusUser();
        super.onStart();
    }

    private void checkStatusUser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            chatMyUid = user.getUid();
        }
        else{
            startActivity(new Intent(chatActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkStatusUser();
        }
        return super.onOptionsItemSelected(item);
    }
}