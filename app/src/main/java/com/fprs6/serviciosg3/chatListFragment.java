package com.fprs6.serviciosg3;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterChatList;
import com.fprs6.serviciosg3.objects.ModelChat;
import com.fprs6.serviciosg3.objects.ModelChatList;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class chatListFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView recyclerView;
    private List<ModelChatList> modelChatListList;
    private List<ModelUser> modelUserList;

    private DatabaseReference dbReference;
    private FirebaseUser firebaseUser;

    private AdapterChatList adapterChatList;
    private DatabaseReference chatsDBReference;




    public chatListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        recyclerView = view.findViewById(R.id.chatListRecycler);

        modelChatListList = new ArrayList<>();

        dbReference = firebaseDatabase.getReference("chat_list").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                modelChatListList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChatList modelChatList = ds.getValue(ModelChatList.class);
                    modelChatListList.add(modelChatList);
                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        return view;
    }

    private void loadChats() {
        modelUserList = new ArrayList<>();

        dbReference = firebaseDatabase.getReference("users");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                modelUserList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    for (ModelChatList modelChatList : modelChatListList){
                        if (modelUser.getUid() != null && modelUser.getUid().equals(modelChatList.getId())){
                            modelUserList.add(modelUser);
                            break;
                        }
                    }
                    adapterChatList = new AdapterChatList(getContext(), modelUserList);

                    recyclerView.setAdapter(adapterChatList);

                    for(int i = 0; i < modelUserList.size(); i++){
                        //lastMessage(modelUserList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(final String userid) {

        chatsDBReference = firebaseDatabase.getReference("chats");
        chatsDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChat modelChat = ds.getValue(ModelChat.class);

                    if (modelChat != null){
                        continue;
                    }
                    String receiver = modelChat.getReceiver();
                    String sender = modelChat.getSender();

                    if (sender != null || receiver != null){
                        continue;
                    }
                    if (modelChat.getReceiver().equals(firebaseUser.getUid()) && modelChat.getSender().equals(userid) ||
                        modelChat.getReceiver().equals(userid) && modelChat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = modelChat.getMessage();
                    }
                }

                adapterChatList.setLastMessageMap(userid, theLastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}