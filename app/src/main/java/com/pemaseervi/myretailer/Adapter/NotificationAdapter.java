package com.pemaseervi.myretailer.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.NotificationModel;
import com.pemaseervi.myretailer.R;

import java.util.List;
public  class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private List<NotificationModel> notificationModalList;

    public NotificationAdapter(List<NotificationModel> notificationModalList) {
        this.notificationModalList = notificationModalList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        String image = notificationModalList.get(position).getImage();
        String body= notificationModalList.get(position).getBody();
        String title= notificationModalList.get(position).getTitle();
        boolean readed = notificationModalList.get(position).isReaded();
        String  date = notificationModalList.get(position).getDate();
        holder.setData(image,title,body,date,readed);
    }

    @Override
    public int getItemCount() {
        return notificationModalList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView,notificationBodyMsg,notificationDateAndTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textView= itemView.findViewById(R.id.notification_title);
            notificationBodyMsg=itemView.findViewById(R.id.notification_body_msg);
            notificationDateAndTime=itemView.findViewById(R.id.notification_date_and_time);
        }
        private void setData(String image,String title, String body,String date,boolean readed){
            Glide.with(itemView.getContext()).load(image).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(imageView);

            if(readed){
                textView.setAlpha(0.5f);
                notificationBodyMsg.setAlpha(0.5f);
            }else{
                textView.setAlpha(1f);
                notificationBodyMsg.setAlpha(1f);
            }
            textView.setText(title);
            notificationDateAndTime.setText(date);
            notificationBodyMsg.setText(body);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBQueries.isNotificationClicked=true;
                    ((Activity) itemView.getContext()).finish();
                      }
            });

        }
    }
}
