package com.example.catstudy.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.catstudy.R;

@UnstableApi
public class VideoPlayerActivity extends AppCompatActivity {

    private static final String EXTRA_VIDEO_URL = "extra_video_url";
    private static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";

    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl;
    private String chapterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        
        // Initialize views
        playerView = findViewById(R.id.player_view);
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvChapterTitle = findViewById(R.id.tv_chapter_title);

        // Get intent extras
        videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        chapterTitle = getIntent().getStringExtra(EXTRA_CHAPTER_TITLE);

        // Set chapter title
        tvChapterTitle.setText(chapterTitle);

        // Set up back button
        btnBack.setOnClickListener(v -> finish());

        // Initialize player
        initializePlayer();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Force landscape orientation when the activity is resumed
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Allow portrait orientation when the activity is paused
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
