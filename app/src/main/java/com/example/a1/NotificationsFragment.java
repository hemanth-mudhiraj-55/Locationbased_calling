package com.example.a1;

import android.app.Notification;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a1.Notification_adapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private Notification_adapter notificationAdapter;
    private List<fetch_notifications> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);

        // Initialize RecyclerView
        rvNotifications = view.findViewById(R.id.rv_notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize notification list and adapter
        notificationList = new ArrayList<>();
        notificationAdapter = new Notification_adapter(notificationList);
        rvNotifications.setAdapter(notificationAdapter);

        // Add sample data (replace with real data)
        notificationList.add(new fetch_notifications("Title 1", "Message 1", "10:00 AM"));
        notificationList.add(new fetch_notifications("Title 2", "Message 2", "11:00 AM"));
        notificationAdapter.notifyDataSetChanged();

        return view;
    }

    private void fetchNotifications() {
        retrofit_interface apiService = RetrofitClient.getApiService();
        Call<List<Notification>> call = apiService.getNotifications();
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();
                    for (Notification notification : notifications) {
                        System.out.println("Notification: " + notification.getTitle() + " - " + notification.getBody());
                    }
                } else {
                    System.out.println("Failed to fetch notifications: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }
}