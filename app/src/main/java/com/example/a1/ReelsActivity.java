package com.example.a1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReelsActivity extends AppCompatActivity {

    private ViewPager2 reelsViewPager;
    private ReelsAdapter reelsAdapter;
    private List<Reel> reelList = new ArrayList<>();

    // Right side action views
    private ImageView likeButton;
    private TextView likeCount;
    private ImageView commentButton;
    private TextView commentCount;
    private ImageView shareButton;

    // Bottom section views
    private TextView username;
    private TextView caption;
    private TextView musicInfo;

    // Comment section views
    private LinearLayout commentLayout;
    private EditText commentInput;
    private Button postCommentButton;

    private ExoPlayer currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        initializeViews();
        setupReelsViewPager();
        setupClickListeners();
        loadSampleData();
    }

    private void initializeViews() {
        reelsViewPager = findViewById(R.id.reelsViewPager);

        // Right side actions
        likeButton = findViewById(R.id.likeButton);
        likeCount = findViewById(R.id.likeCount);
        commentButton = findViewById(R.id.commentButton);
        commentCount = findViewById(R.id.commentCount);
        shareButton = findViewById(R.id.shareButton);

        // Bottom section
        username = findViewById(R.id.username);
        caption = findViewById(R.id.caption);
        musicInfo = findViewById(R.id.musicInfo);

        // Comment section
        commentLayout = findViewById(R.id.commentLayout);
        commentInput = findViewById(R.id.commentInput);
        postCommentButton = findViewById(R.id.postCommentButton);
    }

    private void setupReelsViewPager() {
//        reelsAdapter = new ReelsAdapter(reelList, this);
//        reelsViewPager.setAdapter(reelsAdapter);

        reelsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateUIForCurrentReel(position);
            }
        });
    }

    private void setupClickListeners() {
        likeButton.setOnClickListener(v -> toggleLike());

        commentButton.setOnClickListener(v -> {
            if (commentLayout.getVisibility() == View.VISIBLE) {
                commentLayout.setVisibility(View.GONE);
            } else {
                commentLayout.setVisibility(View.VISIBLE);
                commentInput.requestFocus();
            }
        });

        shareButton.setOnClickListener(v -> shareCurrentReel());

        postCommentButton.setOnClickListener(v -> postComment());
    }

    private void loadSampleData() {
        // Add sample reels - in a real app, this would come from an API
//        reelList.add(new Reel(
//                "1",
//                "user123",
//                "https://example.com/profile.jpg",
//                "https://example.com/video1.mp4",
//                "Check out this amazing view!",
//                "New York, USA",
//                125000,
//                2100
//                //false
//        ));
//
//        reelList.add(new Reel(
//                "2",
//                "traveler456",
//                "https://example.com/profile2.jpg",
//                "https://example.com/video2.mp4",
//                "Sunset at the beach",
//                "Miami, Florida",
//                87000,
//                1500,
//                true
//        ));
//
//        reelsAdapter.notifyDataSetChanged();
//        updateUIForCurrentReel(0);
    }

    private void updateUIForCurrentReel(int position) {
        if (position < 0 || position >= reelList.size()) return;

        Reel currentReel = reelList.get(position);

        // Update right side actions
        likeCount.setText(formatCount(currentReel.getLikeCount()));
        commentCount.setText(formatCount(currentReel.getCommentCount()));
        likeButton.setImageResource(currentReel.isLiked() ?
                R.drawable.ic_like_filled : R.drawable.ic_like_empty);

        // Update bottom section
        username.setText(currentReel.getUsername());
        caption.setText(currentReel.getCaption());
        musicInfo.setText(currentReel.getLocation());
    }

    private void toggleLike() {
        int currentPosition = reelsViewPager.getCurrentItem();
        if (currentPosition < 0 || currentPosition >= reelList.size()) return;

        Reel currentReel = reelList.get(currentPosition);
        currentReel.setLiked(!currentReel.isLiked());
        currentReel.setLikeCount(currentReel.isLiked() ?
                currentReel.getLikeCount() + 1 : currentReel.getLikeCount() - 1);

        updateUIForCurrentReel(currentPosition);
        updateLikeOnServer(currentReel.getReelId(), currentReel.isLiked());
    }

    private void shareCurrentReel() {
        int currentPosition = reelsViewPager.getCurrentItem();
        if (currentPosition < 0 || currentPosition >= reelList.size()) return;

        Reel currentReel = reelList.get(currentPosition);
        // Implement actual sharing logic
        Toast.makeText(this, "Sharing reel: " + currentReel.getCaption(), Toast.LENGTH_SHORT).show();
    }

    private void postComment() {
        String comment = commentInput.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        int currentPosition = reelsViewPager.getCurrentItem();
        if (currentPosition < 0 || currentPosition >= reelList.size()) return;

        Reel currentReel = reelList.get(currentPosition);
        currentReel.setCommentCount(currentReel.getCommentCount() + 1);

        // Update UI
        commentCount.setText(formatCount(currentReel.getCommentCount()));
        commentInput.setText("");
        commentLayout.setVisibility(View.GONE);

        // Post comment to server
        postCommentToServer(currentReel.getReelId(), comment);
    }

    private String formatCount(int count) {
        if (count < 1000) return String.valueOf(count);
        if (count < 1000000) return String.format(Locale.US, "%.1fK", count / 1000.0);
        return String.format(Locale.US, "%.1fM", count / 1000000.0);
    }

    private void updateLikeOnServer(String reelId, boolean isLiked) {
        // Implement actual API call to update like status
        Toast.makeText(this, isLiked ? "Liked" : "Unliked", Toast.LENGTH_SHORT).show();
    }

    private void postCommentToServer(String reelId, String comment) {
        // Implement actual API call to post comment
        Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentPlayer != null) {
            currentPlayer.release();
            currentPlayer = null;
        }
    }

}