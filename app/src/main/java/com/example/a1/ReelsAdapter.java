package com.example.a1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.List;

public class ReelsAdapter  {//extends RecyclerView.Adapter<ReelsAdapter.ReelViewHolder>

//    private List<Reel> reelList;
//    private Context context;
//
//    public ReelsAdapter(List<Reel> reelList, Context context) {
//        this.reelList = reelList;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contribute, parent, false);
//        return new ReelViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
//        Reel reel = reelList.get(position);
//        holder.bind(reel);
//    }
//
//    @Override
//    public int getItemCount() {
//        return reelList.size();
//    }
//
//    public class ReelViewHolder extends RecyclerView.ViewHolder {
//        private ExoPlayerView videoView;
//        private TextView username, caption, location, likeCount, commentCount;
//        private ImageView profileImage, likeButton, commentButton, shareButton;
//        private ProgressBar progressBar;
//
//        public ReelViewHolder(@NonNull View itemView) {
//            super(itemView);
//            videoView = itemView.findViewById(R.id.videoView);
//            username = itemView.findViewById(R.id.username);
//            caption = itemView.findViewById(R.id.caption);
//            location = itemView.findViewById(R.id.location);
//            likeCount = itemView.findViewById(R.id.likeCount);
//            commentCount = itemView.findViewById(R.id.commentCount);
//            profileImage = itemView.findViewById(R.id.profileImage);
//            likeButton = itemView.findViewById(R.id.likeButton);
//            commentButton = itemView.findViewById(R.id.commentButton);
//            shareButton = itemView.findViewById(R.id.shareButton);
//            //progressBar = itemView.findViewById(R.id.progressBar);
//        }
//
//        public void bind(Reel reel) {
//            // Set user data
//            username.setText(reel.getUsername());
//            caption.setText(reel.getCaption());
//            location.setText(reel.getLocation());
//            likeCount.setText(formatCount(reel.getLikeCount()));
//            commentCount.setText(formatCount(reel.getCommentCount()));
//
//            // Load profile image
//            Glide.with(context)
//                    .load(reel.getProfileImageUrl())
//                    .circleCrop()
//                    .into(profileImage);
//
//            // Set like button state
//            likeButton.setImageResource(reel.isLiked() ?
//                    R.drawable.ic_like_filled : R.drawable.ic_like_empty);
//
//            // Initialize ExoPlayer for video playback
//            initializePlayer(reel.getVideoUrl());
//
//            // Set click listeners
//            likeButton.setOnClickListener(v -> toggleLike());
//            commentButton.setOnClickListener(v -> showComments());
//            shareButton.setOnClickListener(v -> shareReel());
//        }
//
//        private void initializePlayer(String videoUrl) {
//            SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
//            videoView.setPlayer(player);
//
//            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
//            player.setMediaItem(mediaItem);
//            player.prepare();
//            player.setPlayWhenReady(true);
//
//            player.addListener(new Player.Listener() {
//                @Override
//                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                    if (playbackState == Player.STATE_BUFFERING) {
//                        progressBar.setVisibility(View.VISIBLE);
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//
//        private void toggleLike() {
//            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION) {
//                Reel reel = reelList.get(position);
//                reel.setLiked(!reel.isLiked());
//                reel.setLikeCount(reel.isLiked() ? reel.getLikeCount() + 1 : reel.getLikeCount() - 1);
//                notifyItemChanged(position);
//
//                // Update like on server
//                updateLikeOnServer(reel.getReelId(), reel.isLiked());
//            }
//        }
//
//        private String formatCount(int count) {
//            if (count < 1000) return String.valueOf(count);
//            if (count < 1000000) return String.format(Locale.US, "%.1fK", count / 1000.0);
//            return String.format(Locale.US, "%.1fM", count / 1000000.0);
//        }
//    }
//
//    private void updateLikeOnServer(String reelId, boolean isLiked) {
//        // Implement API call to update like status
//    }
//
//    private void showComments() {
//        // Implement comment display logic
//    }
//
//    private void shareReel() {
//        // Implement share functionality
//    }
}
