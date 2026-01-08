package com.example.catstudy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import com.example.catstudy.db.ChapterDao;
import com.example.catstudy.model.Chapter;
import com.example.catstudy.ui.activity.VideoPlayerActivity;
import com.example.catstudy.network.ApiClient;
import com.example.catstudy.network.ApisApi;
import com.example.catstudy.network.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

@OptIn(markerClass = UnstableApi.class)
public class CourseCatalogFragment extends Fragment {

    private int courseId;
    private List<String> remoteVideoUrls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get courseId from the activity intent
        if (getActivity() != null) {
            courseId = getActivity().getIntent().getIntExtra("course_id", -1);
        }

        fetchRemoteVideos();

        ListView listView = new ListView(getContext());

        // Get real chapter data from database
        ChapterDao chapterDao = new ChapterDao(getContext());
        List<Chapter> chapterList = chapterDao.getChaptersByCourseId(courseId);

        // Convert Chapter objects to strings for ArrayAdapter
        List<String> chapterTitles = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            chapterTitles.add(chapter.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, chapterTitles);
        listView.setAdapter(adapter);

        // Add click listener to play the selected chapter
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < chapterList.size()) {
                Chapter selectedChapter = chapterList.get(position);
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);

                String videoUrl = selectedChapter.getVideoUrl();
                if (remoteVideoUrls != null && !remoteVideoUrls.isEmpty()) {
                    videoUrl = remoteVideoUrls.get(position % remoteVideoUrls.size());
                }

                intent.putExtra("extra_video_url", videoUrl);
                intent.putExtra("extra_chapter_title", selectedChapter.getTitle());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Invalid chapter", Toast.LENGTH_SHORT).show();
            }
        });

        return listView;
    }

    private void fetchRemoteVideos() {
        ApisApi apisApi = ApiClient.createService(ApisApi.class);
        apisApi.getVideos().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> videos = response.body().getData();
                    if (videos != null && !videos.isEmpty()) {
                        List<String> validVideos = new java.util.ArrayList<>();
                        for (String v : videos) {
                            if (v != null && !v.isEmpty()) {
                                validVideos.add(v);
                            }
                        }
                        if (!validVideos.isEmpty()) {
                            remoteVideoUrls = validVideos;
                            android.util.Log.d("CourseCatalog", "Remote videos fetched: " + validVideos.size());
                        }
                    } else {
                        android.util.Log.w("CourseCatalog", "Remote videos list is empty");
                    }
                } else {
                    android.util.Log.e("CourseCatalog", "Remote videos fetch failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                android.util.Log.e("CourseCatalog", "Remote videos fetch error: " + t.getMessage());
            }
        });
    }

//    @Override
//    public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
//        // Ignore failure, keep using local placeholder
//    }
}




