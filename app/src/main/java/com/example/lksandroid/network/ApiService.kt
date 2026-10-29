package com.example.lksandroid.network

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth")
    fun login(@Body request: AuthDTO): Call<AuthResponse>

    @POST("api/register")
    fun register(@Body request: RegisterDTO): Call<AuthResponse>

    // Jobs List
    @GET("api/jobs")
    fun getJobs(
        @Header("Authorization") token: String? = null
    ): Call<JobResponse>

    // Job Detail
    @GET("api/jobs/{jobID}")
    fun getJobDetail(
        @Path("jobID") jobID: Int
    ): Call<JobDetailResponse>

    // Apply Job
    @POST("api/jobs/{jobID}/apply")
    fun applyJob(
        @Path("jobID") jobID: Int
    ): Call<Void>

    // Save Job
    @POST("api/jobs/{jobID}/save")
    fun saveJob(
        @Header("Authorization") token: String? = null,
        @Path("jobID") jobID: Int
    ): Call<Void>
}