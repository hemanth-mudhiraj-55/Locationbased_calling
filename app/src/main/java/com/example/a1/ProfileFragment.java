package com.example.a1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserLocation, tvBalance;
    private Button btnRecharge, btnSettings, btnLogout;
    private RecyclerView rvCallHistory;
    private ProgressBar progressBar;
   // private CallHistoryAdapter callHistoryAdapter;
    private retrofit_interface apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

//        initViews(view);
//        setupApiService();
//        fetchData();

        return view;
    }

//    private void initViews(View view) {
//        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
//        tvUserName = view.findViewById(R.id.tv_user_name);
//        tvUserEmail = view.findViewById(R.id.tv_user_email);
//        tvUserPhone = view.findViewById(R.id.tv_user_phone);
//        tvUserLocation = view.findViewById(R.id.tv_user_location);
//        tvBalance = view.findViewById(R.id.tv_balance);
//        btnRecharge = view.findViewById(R.id.btn_recharge);
//        btnSettings = view.findViewById(R.id.btn_settings);
//        btnLogout = view.findViewById(R.id.btn_logout);
//        rvCallHistory = view.findViewById(R.id.rv_call_history);
//        progressBar = view.findViewById(R.id.progress_bar);
//
//        callHistoryAdapter = new CallHistoryAdapter(new ArrayList<>());
//        rvCallHistory.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvCallHistory.setAdapter(callHistoryAdapter);
//        rvCallHistory.setHasFixedSize(true);
//
//        setClickListeners();
//    }
//
//    private void setupApiService() {
//        apiService = RetrofitClient.getApiService();
//    }
//
//    private void fetchData() {
//        showLoading(true);
//        fetchUserData();
//        fetchCallHistory();
//    }
//
//    private void fetchUserData() {
//        Call<UserResponse> call = apiService.getUserProfile();
//        call.enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                showLoading(false);
//                if (response.isSuccessful() && response.body() != null) {
//                    updateUserUI(response.body());
//                } else {
//                    showError(getString(R.string.error_user_data));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//                showLoading(false);
//                showError(getString(R.string.network_error));
//            }
//        });
//    }
//
//    private void fetchCallHistory() {
//        Call<CallHistoryResponse> call = apiService.getCallHistory();
//        call.enqueue(new Callback<CallHistoryResponse>() {
//            @Override
//            public void onResponse(Call<CallHistoryResponse> call, Response<CallHistoryResponse> response) {
//                showLoading(false);
//                if (response.isSuccessful() && response.body() != null) {
//                    callHistoryAdapter.updateData(response.body().getCallHistory());
//                } else {
//                    showError(getString(R.string.error_call_history));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallHistoryResponse> call, Throwable t) {
//                showLoading(false);
//                showError(getString(R.string.network_error));
//            }
//        });
//    }
//
//    private void updateUserUI(UserResponse user) {
//        tvUserName.setText(user.getName());
//        tvUserEmail.setText(user.getEmail());
//        tvUserPhone.setText(user.getPhone());
//        tvUserLocation.setText(getString(R.string.current_location, user.getLocation()));
//        tvBalance.setText(getString(R.string.balance_format, user.getBalance()));
//
//        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
//            Glide.with(this)
//                    .load(user.getProfileImageUrl())
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.profile)
//                            .error(R.drawable.profile)
//                            .circleCrop())
//                    .into(ivProfilePicture);
//        }
//    }
//
//    private void setClickListeners() {
//        btnRecharge.setOnClickListener(v -> {
//            // Implement recharge navigationa
//            Toast.makeText(getContext(), R.string.recharge_message, Toast.LENGTH_SHORT).show();
//        });
//
//        btnSettings.setOnClickListener(v -> {
//            // Implement settings navigation
//            Toast.makeText(getContext(), R.string.settings_message, Toast.LENGTH_SHORT).show();
//        });
//
//        btnLogout.setOnClickListener(v -> performLogout());
//    }
//
//    private void performLogout() {
//        showLoading(true);
//        Call<Void> call = apiService.logout();
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                showLoading(false);
//                if (response.isSuccessful()) {
//                    clearUserData();
//                    navigateToLogin();
//                } else {
//                    showError(getString(R.string.logout_failed));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                showLoading(false);
//                showError(getString(R.string.network_error));
//            }
//        });
//    }
//
//    private void showLoading(boolean isLoading) {
//        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//    }
//
//    private void showError(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void clearUserData() {
//        // Clear shared preferences or other local storage
//    }
//
//    private void navigateToLogin() {
//        // Implement navigation to login screen
//        Toast.makeText(getContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
//    }
//
//    // Adapter class remains the same
//
//    private static class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.CallHistoryViewHolder> {
//        private List<CallHistoryItem> callHistory;
//
//        public CallHistoryAdapter(List<CallHistoryItem> callHistory) {
//            this.callHistory = callHistory;
//        }
//
//        public void updateData(List<CallHistoryItem> newData) {
//            callHistory = newData;
//            notifyDataSetChanged();
//        }
//
//        @NonNull
//        @Override
//        public CallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_call_history, parent, false);
//            return new CallHistoryViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull CallHistoryViewHolder holder, int position) {
//            CallHistoryItem item = callHistory.get(position);
//            holder.bind(item);
//        }
//
//        @Override
//        public int getItemCount() {
//            return callHistory.size();
//        }
//
//        static class CallHistoryViewHolder extends RecyclerView.ViewHolder {
//            private final TextView tvCallerName;
//            private final TextView tvCallDate;
//            private final TextView tvCallDuration;
//
//            public CallHistoryViewHolder(@NonNull View itemView) {
//                super(itemView);
//                tvCallerName = itemView.findViewById(R.id.tv_caller_name);
//                tvCallDate = itemView.findViewById(R.id.tv_call_date);
//                tvCallDuration = itemView.findViewById(R.id.tv_call_duration);
//            }
//
//            public void bind(CallHistoryItem item) {
//                tvCallerName.setText(item.getCallerName());
//                tvCallDate.setText(item.getCallDate());
//                tvCallDuration.setText(item.getCallDuration());
//            }
//        }
//    }
}