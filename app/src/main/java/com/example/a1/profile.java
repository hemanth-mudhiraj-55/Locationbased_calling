package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class profile extends Fragment {

    // UI Components
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvBalance;
    private TextView tvNoCallHistory, tvNoRechargeHistory, tvNoWishlist;
    private ImageButton btnSettings, btnLogout,btnLogin;
    private ProgressBar progressBar;
    private RecyclerView rvCallHistory, rvRechargeHistory, rvWishlist;
    private LinearLayout rechargeButton, callHistoryButton, wishlistButton, recentThingsLayout;

    // Adapters
    private CallHistoryAdapter callHistoryAdapter;
    private RechargeHistoryAdapter rechargeHistoryAdapter;
    private WishlistAdapter wishlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        initViews(view);
        setupClickListeners();
        loadUserProfile();

        return view;
    }

    private void initViews(View view) {
        // TextViews
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvNoCallHistory = view.findViewById(R.id.tv_no_call_history);
        tvNoRechargeHistory = view.findViewById(R.id.tv_no_recharge_history);
        tvNoWishlist = view.findViewById(R.id.tv_no_wishlist);

        // Buttons
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
         btnLogin = view.findViewById(R.id.profile_btn_login);

        // Layout containers
        rechargeButton = view.findViewById(R.id.recharge_button);
        callHistoryButton = view.findViewById(R.id.Call_history_button);
        wishlistButton = view.findViewById(R.id.wishlist_button);
        recentThingsLayout = view.findViewById(R.id.recent_things);

        // Progress
        progressBar = view.findViewById(R.id.progress_bar);

        // RecyclerViews
        rvCallHistory = view.findViewById(R.id.rv_call_history);
        rvRechargeHistory = view.findViewById(R.id.rv_recharge_history);
        rvWishlist = view.findViewById(R.id.rv_wishlist);
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        btnLogin.setOnClickListener(v -> navigateToLogin());
        btnSettings.setOnClickListener(v -> openSettings());

        // These will only be set if the views are visible
        if (rechargeButton.getVisibility() == View.VISIBLE) {
            rechargeButton.findViewById(R.id.btn_recharge).setOnClickListener(v -> openRechargeActivity());
        }
        if (callHistoryButton.getVisibility() == View.VISIBLE) {
            callHistoryButton.findViewById(R.id.btn_call_history).setOnClickListener(v -> openFullCallHistory());
        }
        if (wishlistButton.getVisibility() == View.VISIBLE) {
            wishlistButton.findViewById(R.id.btn_wishlist).setOnClickListener(v -> openWishlist());
        }
    }

    private void loadUserProfile() {
        showProgress(true);

        // Simulate API response - in real app you would get this from your API call
        LoginResponse loginResponse = getLoginResponseFromSharedPreferences();

        if (loginResponse != null && loginResponse.isSuccess()) {
            // User is authenticated - show all content
            setAuthenticatedUI(loginResponse);
        } else {
            // User is not authenticated - hide sensitive content
            setUnauthenticatedUI();
        }

        showProgress(false);
    }
    private void setAuthenticatedUI(LoginResponse response) {
        // Show all authenticated user content
        rechargeButton.setVisibility(View.VISIBLE);
        callHistoryButton.setVisibility(View.VISIBLE);
        wishlistButton.setVisibility(View.VISIBLE);  // Always show wishlist
        recentThingsLayout.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);// Show logout for authenticated users

        // Set user info directly from LoginResponse (not from User class)
        tvUserName.setText(response.getUserId());
        tvUserEmail.setText(response.getEmail());
        tvUserPhone.setText(response.getMobile());
        tvBalance.setText(String.format("Balance: Rs:%.2f", response.getBalance()));

        // Setup recycler views with data
        setupRecyclerViews(response);
    }
    private LoginResponse getLoginResponseFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Create a mock LoginResponse from shared preferences
        LoginResponse response = new LoginResponse();
        response.setSuccess(sharedPreferences.getBoolean("isLoggedIn", false));

        if (response.isSuccess()) {
           // LoginResponse.User user = new LoginResponse.User();
            response.setUserId(sharedPreferences.getString("userId", "Guest User"));
            response.setEmail(sharedPreferences.getString("email", "email@example.com"));
            response.setMobile(sharedPreferences.getString("mobile", "+0000000000"));
            response.setBalance(sharedPreferences.getFloat("balance", 0.0f));
           // response.setUser(user);

            // Set mock data for recent items
            response.setRecentRecharges(getMockRecharges());
            response.setRecentCalls(getMockCalls());
            response.setWishlistItems(getMockWishlistItems());
        }

        return response;
    }



    private void setupRecyclerViews(LoginResponse response) {
        // Call History
        rvCallHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        callHistoryAdapter = new CallHistoryAdapter(convertToCallHistoryItems(response.getRecentCalls()));
        rvCallHistory.setAdapter(callHistoryAdapter);

        // Recharge History
        rvRechargeHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rechargeHistoryAdapter = new RechargeHistoryAdapter(convertToRechargeHistoryItems(response.getRecentRecharges()));
        rvRechargeHistory.setAdapter(rechargeHistoryAdapter);

        // Wishlist
        rvWishlist.setLayoutManager(new LinearLayoutManager(getContext()));
        wishlistAdapter = new WishlistAdapter(convertToWishlistItems(response.getWishlistItems()));
        rvWishlist.setAdapter(wishlistAdapter);

        updateEmptyStates();
    }

    private List<CallHistoryItem> convertToCallHistoryItems(List<LoginResponse.Call> calls) {
        List<CallHistoryItem> items = new ArrayList<>();
        if (calls != null) {
            for (LoginResponse.Call call : calls) {
                items.add(new CallHistoryItem(
                        call.getPhoneNumber(),
                        call.getDuration(),
                        call.getDate()
                ));
            }
        }
        return items;
    }

    private List<RechargeHistoryItem> convertToRechargeHistoryItems(List<LoginResponse.Recharge> recharges) {
        List<RechargeHistoryItem> items = new ArrayList<>();
        if (recharges != null) {
            for (LoginResponse.Recharge recharge : recharges) {
                items.add(new RechargeHistoryItem(
                        String.format("Rs:%.2f", recharge.getAmount()),
                        recharge.getStatus(),
                        recharge.getDate()
                ));
            }
        }
        return items;
    }

    private List<WishlistItem> convertToWishlistItems(List<LoginResponse.WishlistItem> wishlistItems) {
        List<WishlistItem> items = new ArrayList<>();
        if (wishlistItems != null) {
            for (LoginResponse.WishlistItem item : wishlistItems) {
                items.add(new WishlistItem(
                        item.getName(),
                        String.format("Rs:%.2f", item.getPrice()),
                        R.drawable.ic_premium // Default icon, you can customize this
                ));
            }
        }
        return items;
    }

    private List<LoginResponse.Recharge> getMockRecharges() {
        List<LoginResponse.Recharge> recharges = new ArrayList<>();

        LoginResponse.Recharge recharge1 = new LoginResponse.Recharge();
        recharge1.setRechargeId("1");
        recharge1.setAmount(100.00);
        recharge1.setDate("Today, 9:00 AM");
        recharge1.setStatus("Success");
        recharges.add(recharge1);

        LoginResponse.Recharge recharge2 = new LoginResponse.Recharge();
        recharge2.setRechargeId("2");
        recharge2.setAmount(50.00);
        recharge2.setDate("Yesterday, 2:30 PM");
        recharge2.setStatus("Success");
        recharges.add(recharge2);

        return recharges;
    }

    private List<LoginResponse.Call> getMockCalls() {
        List<LoginResponse.Call> calls = new ArrayList<>();

        LoginResponse.Call call1 = new LoginResponse.Call();
        call1.setCallId("1");
        call1.setPhoneNumber("+1 (555) 123-4567");
        call1.setDuration("5 min 23 sec");
        call1.setDate("Today, 10:30 AM");
        call1.setType("outgoing");
        calls.add(call1);

        LoginResponse.Call call2 = new LoginResponse.Call();
        call2.setCallId("2");
        call2.setPhoneNumber("+1 (555) 987-6543");
        call2.setDuration("2 min 45 sec");
        call2.setDate("Yesterday, 4:15 PM");
        call2.setType("incoming");
        calls.add(call2);

        return calls;
    }

    private List<LoginResponse.WishlistItem> getMockWishlistItems() {
        List<LoginResponse.WishlistItem> items = new ArrayList<>();

        LoginResponse.WishlistItem item1 = new LoginResponse.WishlistItem();
        item1.setItemId("1");
        item1.setName("Premium Plan");
        item1.setDescription("Unlimited calls");
        item1.setPrice(19.99);
        item1.setImageUrl("");
        items.add(item1);

        LoginResponse.WishlistItem item2 = new LoginResponse.WishlistItem();
        item2.setItemId("2");
        item2.setName("Extra Minutes");
        item2.setDescription("500 minutes package");
        item2.setPrice(5.99);
        item2.setImageUrl("");
        items.add(item2);

        return items;
    }
    private void setUnauthenticatedUI() {
        // Hide authenticated-only content
        rechargeButton.setVisibility(View.GONE);
        callHistoryButton.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);  // Hide logout button
        btnLogin.setVisibility(View.VISIBLE);

        // Keep these visible even when unauthenticated
        wishlistButton.setVisibility(View.VISIBLE);
        recentThingsLayout.setVisibility(View.VISIBLE);

        // Set default guest user info
        tvUserName.setText("Guest User");
        tvUserEmail.setText("Please login to view your profile");
        tvUserPhone.setText("");
        tvBalance.setText("");

        // Show empty states for guest users
        showEmptyStatesForGuest();
    }

    private void showEmptyStatesForGuest() {
        rvCallHistory.setVisibility(View.GONE);
        tvNoCallHistory.setVisibility(View.VISIBLE);
        tvNoCallHistory.setText("Login to view call history");

        rvRechargeHistory.setVisibility(View.GONE);
        tvNoRechargeHistory.setVisibility(View.VISIBLE);
        tvNoRechargeHistory.setText("Login to view recharge history");

        rvWishlist.setVisibility(View.VISIBLE);  // Keep wishlist visible
        tvNoWishlist.setVisibility(View.GONE);
    }
    private void updateEmptyStates() {
        // Call History
        if (callHistoryAdapter == null || callHistoryAdapter.getItemCount() == 0) {
            rvCallHistory.setVisibility(View.GONE);
            tvNoCallHistory.setVisibility(View.VISIBLE);
        } else {
            rvCallHistory.setVisibility(View.VISIBLE);
            tvNoCallHistory.setVisibility(View.GONE);
        }

        // Recharge History
        if (rechargeHistoryAdapter == null || rechargeHistoryAdapter.getItemCount() == 0) {
            rvRechargeHistory.setVisibility(View.GONE);
            tvNoRechargeHistory.setVisibility(View.VISIBLE);
        } else {
            rvRechargeHistory.setVisibility(View.VISIBLE);
            tvNoRechargeHistory.setVisibility(View.GONE);
        }

        // Wishlist
        if (wishlistAdapter == null || wishlistAdapter.getItemCount() == 0) {
            rvWishlist.setVisibility(View.GONE);
            tvNoWishlist.setVisibility(View.VISIBLE);
        } else {
            rvWishlist.setVisibility(View.VISIBLE);
            tvNoWishlist.setVisibility(View.GONE);
        }
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void logoutUser() {
        // Get token from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = preferences.getString("authToken", null);

        if (token == null || token.isEmpty()) {
            // No token found, proceed with local logout
            clearLocalUserData();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Add Bearer prefix if not already present
        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        retrofit_interface apiService = RetrofitClient.getApiService();
        apiService.logoutUser(authToken).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                // Clear local data regardless of server response
                clearLocalUserData();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Server responded but with error
                    String errorMsg = "Logout failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMsg += ": Couldn't parse error";
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
                navigateToLogin();
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                // Clear local data even if network failed
                clearLocalUserData();
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                navigateToLogin();
            }
        });
    }

    private void clearLocalUserData() {
        // Clear SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("authToken");
        editor.remove("userName");
        editor.remove("userEmail");
        editor.apply();

        // Clear any other local data (like from FP_OTP_Verification_Page)
        FP_OTP_Verification_Page.logoutUser(requireContext());

        // Optional: Clear any cached data or images
        // Glide.get(requireContext()).clearMemory();
        // new Thread(() -> Glide.get(requireContext()).clearDiskCache()).start();
    }

    private void navigateToLogin() {
        startActivity(new Intent(getActivity(), Login_pg.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        requireActivity().finish();
    }

    private void openSettings() {
        startActivity(new Intent(getActivity(), Settings.class));
    }

    private void openFullCallHistory() {
        startActivity(new Intent(getActivity(), CallHistory.class));
    }

    private void openWishlist() {
        startActivity(new Intent(getActivity(), Wishlist.class));
    }

    private void openRechargeActivity() {
        startActivity(new Intent(getActivity(), Recharge.class));
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // Data Models for UI
    public static class CallHistoryItem {
        public String number;
        public String duration;
        public String time;

        public CallHistoryItem(String number, String duration, String time) {
            this.number = number;
            this.duration = duration;
            this.time = time;
        }
    }

    public static class RechargeHistoryItem {
        public String amount;
        public String status;
        public String time;

        public RechargeHistoryItem(String amount, String status, String time) {
            this.amount = amount;
            this.status = status;
            this.time = time;
        }
    }

    public static class WishlistItem {
        public String title;
        public String description;
        public int iconRes;

        public WishlistItem(String title, String description, int iconRes) {
            this.title = title;
            this.description = description;
            this.iconRes = iconRes;
        }
    }

    // Adapters (same as before, but now using the converted data)
    private class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
        private List<CallHistoryItem> items;

        public CallHistoryAdapter(List<CallHistoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_call_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallHistoryItem item = items.get(position);
            holder.tvNumber.setText(item.number);
            holder.tvDuration.setText(item.duration);
            holder.tvTime.setText(item.time);
        }

        @Override
        public int getItemCount() {
            return items != null ? Math.min(items.size(), 2) : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNumber, tvDuration, tvTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tv_number);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }
    }

    private class RechargeHistoryAdapter extends RecyclerView.Adapter<RechargeHistoryAdapter.ViewHolder> {
        private List<RechargeHistoryItem> items;

        public RechargeHistoryAdapter(List<RechargeHistoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recharge_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RechargeHistoryItem item = items.get(position);
            holder.tvAmount.setText(item.amount);
            holder.tvStatus.setText(item.status);
            holder.tvTime.setText(item.time);
        }

        @Override
        public int getItemCount() {
            return items != null ? Math.min(items.size(), 2) : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAmount, tvStatus, tvTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }
    }

    private class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
        private List<WishlistItem> items;

        public WishlistAdapter(List<WishlistItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_wishlist, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WishlistItem item = items.get(position);
            holder.tvTitle.setText(item.title);
            holder.tvDescription.setText(item.description);
            holder.ivIcon.setImageResource(item.iconRes);

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(getContext(), item.title + " clicked", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items != null ? Math.min(items.size(), 2) : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle, tvDescription;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDescription = itemView.findViewById(R.id.tv_description);
            }
        }
    }
}