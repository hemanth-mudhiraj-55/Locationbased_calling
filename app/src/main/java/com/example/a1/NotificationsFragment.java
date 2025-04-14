package com.example.a1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private Button btnDeleteAll, btnDeleteSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);

        // Initialize views
        rvNotifications = view.findViewById(R.id.rv_notifications);
        btnDeleteAll = view.findViewById(R.id.btn_delete_notifications);
        btnDeleteSelected = view.findViewById(R.id.btn_delete_selected_notifications);

        // Setup RecyclerView
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(notificationAdapter);

        // Setup buttons
        setupButtons();

        // Fetch notifications
        fetchNotifications();

        return view;
    }

    private void setupButtons() {
        btnDeleteAll.setOnClickListener(v -> {
            if (!notificationList.isEmpty()) {
                notificationList.clear();
                notificationAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "All notifications deleted", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteSelected.setOnClickListener(v -> {
            List<Notification> selectedItems = notificationAdapter.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                notificationList.removeAll(selectedItems);
                notificationAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Selected notifications deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNotifications() {
        retrofit_interface apiService = RetrofitClient.getApiService();
        Call<List<Notification>> call = apiService.getNotifications();

        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    notificationAdapter.notifyDataSetChanged();

                    // Show delete buttons if there are notifications
                    if (!notificationList.isEmpty()) {
                        btnDeleteAll.setVisibility(View.VISIBLE);
                        btnDeleteSelected.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}