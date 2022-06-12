package com.fprs6.serviciosg3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.R;
import com.fprs6.serviciosg3.objects.ModelComment;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.commentHolder> {

    private Context context;
    private List<ModelComment> commentList;

    public AdapterComment(Context context, List<ModelComment> commentList){
        this.context = context;
        this.commentList = commentList;
    }

    @NotNull
    @Override
    public commentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_show_model, parent, false);
        return new commentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull commentHolder holder, int position) {

        //Users Identificators
        String professionalUid = commentList.get(position).getCommented();
        String clientUid = commentList.get(position).getCommenter();

        String commentTextOnlyReadTXT = commentList.get(position).getComment();
        double commentRatingOnlyReadTXT = commentList.get(position).getValoration();
        String commentDatePostedTXT = commentList.get(position).getTime_comment();

        Calendar selfCal = Calendar.getInstance(Locale.US);
        selfCal.setTimeInMillis(Long.parseLong(commentDatePostedTXT));

        String DateSending = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(selfCal.getTime()).toString();

        DatabaseReference clientDBReference = FirebaseDatabase.getInstance().getReference("users");
        Query queryclient = clientDBReference.orderByChild("uid").equalTo(clientUid);

        queryclient.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    String userProfileImageText = modelUser.getProfileImage();
                    String userName = modelUser.getUserName();

                    holder.commentUserName.setText(userName);

                    try {
                        Picasso.get().load(userProfileImageText).into(holder.commentProfile);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_profile_black).into(holder.commentProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.commentRatingOnlyRead.setRating((float)commentRatingOnlyReadTXT);
        holder.commentTextOnlyRead.setText(commentTextOnlyReadTXT);
        holder.commentDatePosted.setText(DateSending);






    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class commentHolder extends RecyclerView.ViewHolder{
        private ImageView commentProfile;
        private TextView commentUserName;
        private TextView commentTextOnlyRead;
        private RatingBar commentRatingOnlyRead;
        private TextView commentDatePosted;

        public commentHolder(@NotNull View itemView) {
            super(itemView);
            commentProfile = itemView.findViewById(R.id.commentProfile);
            commentUserName = itemView.findViewById(R.id.commentUserName);
            commentTextOnlyRead = itemView.findViewById(R.id.commentTextOnlyRead);
            commentRatingOnlyRead = itemView.findViewById(R.id.commentRatingOnlyRead);
            commentDatePosted = itemView.findViewById(R.id.commentDatePosted);
        }
    }
}
