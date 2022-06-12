package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.R;
import com.fprs6.serviciosg3.chatActivity;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.chatListHolder>{

    private Context context;
    private List<ModelUser> modelUserList;
    private HashMap<String, String> lastMessageMap;

    public AdapterChatList(Context context, List<ModelUser> modelUserList) {
        this.context = context;
        this.modelUserList = modelUserList;
        this.lastMessageMap = new HashMap<>();
    }
    public void setLastMessageMap(String contactUID, String lastMessage){
        lastMessageMap.put(contactUID, lastMessage);
    }

    @NotNull
    @Override
    public chatListHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_show_model, parent, false);
        return new chatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull chatListHolder holder, int position) {

        String contactUid = modelUserList.get(position).getUid();
        String messageProfileTxt = modelUserList.get(position).getProfileImage();
        String messageUserName = modelUserList.get(position).getUserName();
        String lastMessage = lastMessageMap.get(contactUid);

        holder.messageName.setText(messageUserName);

        try {
            Picasso.get().load(messageProfileTxt).placeholder(R.drawable.ic_default_profile_black).into(holder.messageProfile);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_default_profile_black).into(holder.messageProfile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context, chatActivity.class);
                chatIntent.putExtra("chatContactUid", contactUid);
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelUserList.size();
    }


    class chatListHolder extends RecyclerView.ViewHolder{

        private ImageView messageProfile;
        private TextView messageName;

        public chatListHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            messageProfile = itemView.findViewById(R.id.messageProfile);
            messageName = itemView.findViewById(R.id.messageName);

        }
    }
}
