package com.example.marius.path.services;

import com.example.marius.path.data_model.CommentC;
import com.example.marius.path.data_model.GlobePosts;
import com.example.marius.path.data_model.Post;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JsonPlaceholderApi {

    @GET("getFirstPosts")
    @Headers("Content-Type:application/json")
    Call<GlobePosts> getFirstPosts(
            @Query("country") String country,
            @Query("distance") Double distance,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

//    @GET("posts")
//    Call<List<Post>> getPosts(
//            @Query("userId") Integer[] userId,
//            @Query("_sort") String sort,
//            @Query("_order") String order
//    );

    @GET("posts")
    Call<List<Post>> getPosts(@QueryMap Map<String, String> parameters);

    @GET("posts/{id}/comments")
    Call<List<CommentC>> getComments(@Path("id") int postId);

    @GET
    Call<List<CommentC>> getComments(@Url String url);
}
