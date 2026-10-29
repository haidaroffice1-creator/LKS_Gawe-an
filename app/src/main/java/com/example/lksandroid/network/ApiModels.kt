package com.example.lksandroid.network

import android.app.wallpaper.WallpaperDescription
import com.google.gson.annotations.SerializedName

data class AuthDTO(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterDTO(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
)

data class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserData?
)

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String,
)

data class Job(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("company") val company: Company,
    @SerializedName("locationType") val locationType: String,
    @SerializedName("locationRegion") val locationRegion: String,
    @SerializedName("yearOfExperience") val yearOfExperience: String,
    @SerializedName("quota") val quota: Int,

    @SerializedName("isSaved") var isSaved: Boolean = false,
    @SerializedName("isApplied") var isApplied: Boolean = false
)

data class Company(
    @SerializedName("name") val name: String
)

data class JobResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Job>
)

data class JobDetailResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: JobDetail
)

data class JobDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("company") val company: Company,
    @SerializedName("locationType") val locationType: String,
    @SerializedName("locationRegion") val locationRegion: String,
    @SerializedName("yearOfExperience") val yearOfExperience: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("responsibilities") val responsibilities: String? = null, // Pastikan ada ini
    @SerializedName("qualifications") val qualifications: String? = null,
    @SerializedName("softSkills") val softSkills: String? = null,
    @SerializedName("isSaved") var isSaved: Boolean = false,     // Wajib gantikan 'val' jadi 'var'
    @SerializedName("isApplied") var isApplied: Boolean = false,  // Wajib gantikan 'val' jadi 'var'
    @SerializedName("jobLink") val jobLink: String? = null
)