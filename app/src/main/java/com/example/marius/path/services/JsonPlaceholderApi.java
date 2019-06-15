package com.example.marius.path.services;

import com.example.marius.path.data_model.CommentC;
import com.example.marius.path.data_model.FilteredPostsBySearch;
import com.example.marius.path.data_model.GlobePosts;
import com.example.marius.path.data_model.Post;
import com.example.marius.path.data_model.ReverseGeocoding;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
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

    @GET("getMorePosts")
    @Headers("Content-Type:application/json")
    Call<GlobePosts>getMorePosts(
            @Query("country") String country,
            @Query("prevPostId") String prevPostId,
            @Query("distance") Double distance,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

    @GET("searchByString")
    @Headers("Content-Type:application/json")
    Call<FilteredPostsBySearch>searchByString(
            @Query("queryString") String queryString,
            @Query("prevSortLocation") String prevSortLocation
    );

    @GET("maps/api/geocode/json")
    @Headers("Content-Type:application/json")
    Call<ReverseGeocoding>getCountry(
            @Query("latlng") String latLong,
            @Query("result_type") String resultType,
            @Query("key") String apiKey
    );

//    @GET("searchByString")
//    @Headers("Content-Type:application/json")


    @DELETE("fakeDelete")
    @Headers("Content-Type:application/json")
    Call<Void> deleteProfile();


//    @GET("posts")
//    Call<List<Post>> getPosts(
//            @Query("userId") Integer[] userId,
//            @Query("_sort") String sort,
//            @Query("_order") String order
//    );

    // examples
    @GET("posts")
    Call<List<Post>> getPosts(@QueryMap Map<String, String> parameters);

    @GET("posts/{id}/comments")
    Call<List<CommentC>> getComments(@Path("id") int postId);

    @GET
    Call<List<CommentC>> getComments(@Url String url);
}
