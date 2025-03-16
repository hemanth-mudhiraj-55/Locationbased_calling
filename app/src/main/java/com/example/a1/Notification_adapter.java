package com.example.a1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Notification_adapter extends RecyclerView.Adapter<Notification_adapter.NotificationViewHolder> {

    private List<fetch_notifications> notificationList;

    public Notification_adapter(List<fetch_notifications> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        fetch_notifications notification = notificationList.get(position);
        holder.tvTitle.setText(notification.getTitle()); // Use getTitle() from the fetch_notification class
        holder.tvMessage.setText(notification.getMessage()); // Use getMessage() from the fetch_notification class
        holder.tvTimestamp.setText(notification.getTimestamp()); // Use getTimestamp() from the fetch_notification class
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTimestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTimestamp = itemView.findViewById(R.id.tv_notification_timestamp);
        }
    }
}