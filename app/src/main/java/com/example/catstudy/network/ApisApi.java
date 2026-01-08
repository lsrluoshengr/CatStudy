package com.example.catstudy.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApisApi {
    @GET("apis/images")
    Call<ApiResponse<List<String>>> getImages();

    @GET("apis/courses")
    Call<ApiResponse<List<com.example.catstudy.model.Course>>> getCourses();
}