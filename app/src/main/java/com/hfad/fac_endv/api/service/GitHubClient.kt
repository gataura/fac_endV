package com.hfad.fac_endv.api.service

import com.hfad.fac_endv.api.model.AccessToken
import com.hfad.fac_endv.api.model.GitHubRepo
import com.hfad.fac_endv.api.model.RepoList
import retrofit2.Call
import retrofit2.http.*

public interface GitHubClient {

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    fun getAccessToken(
            @Field("client_id") clientId:String,
            @Field("client_secret") clientSecret:String,
            @Field("code") code:String
    ): Call<AccessToken>

    @GET("user/repos?")
    fun reposForToken(@Query("access_token") access_token:String): Call<List<GitHubRepo>>
}