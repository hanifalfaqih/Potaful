package id.hanifalfaqih.potaful.data.remote

import id.hanifalfaqih.potaful.data.remote.request.AddPotRequest
import id.hanifalfaqih.potaful.data.remote.response.BaseResponse
import id.hanifalfaqih.potaful.data.remote.response.GoogleAuthUrlResponse
import id.hanifalfaqih.potaful.data.remote.response.MyPotsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    /**
     * ADD POT
     */
    @POST("api/mypot/add")
    suspend fun addPot(
        @Header("Authorization") token: String,
        @Body request: AddPotRequest
    ): BaseResponse<String>

    /**
     * GET MY POTS
     */
    @GET("api/mypot")
    suspend fun getMyPots(
        @Header("Authorization") token: String
    ): MyPotsResponse

    /**
     * GET POT DETAIL DATA
     */
    @GET("api/mypot/{pot_id}/data")
    suspend fun getPotDetail(
        @Header("Authorization") token: String,
        @Path("pot_id") potId: String
    ): id.hanifalfaqih.potaful.data.remote.response.PotDetailResponse

    /**
     * GET USER PROFILE
     */
    @GET("api/auth/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): id.hanifalfaqih.potaful.data.remote.response.UserProfileResponse

    /**
     * Get Google Auth URL
     */
    @GET("api/auth/google")
    suspend fun getGoogleAuthUrl(): GoogleAuthUrlResponse


//    // Authentication
//    @FormUrlEncoded
//    @POST("auth/register")
//    suspend fun register(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): retrofit2.Response<Any>
//
//    @FormUrlEncoded
//    @POST("auth/login")
//    suspend fun login(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): retrofit2.Response<Any>
//
//    // User Profile
//    @GET("user/profile")
//    suspend fun getProfile(
//        @Header("Authorization") token: String
//    ): retrofit2.Response<Any>
//
//    @PUT("user/profile")
//    suspend fun updateProfile(
//        @Header("Authorization") token: String,
//        @Body profileData: Any
//    ): retrofit2.Response<Any>
//
//    // Plants/Pots endpoints - modify as needed
//    @GET("plants")
//    suspend fun getPlants(
//        @Header("Authorization") token: String
//    ): retrofit2.Response<Any>
//
//    @POST("plants")
//    suspend fun addPlant(
//        @Header("Authorization") token: String,
//        @Body plantData: Any
//    ): retrofit2.Response<Any>
//
//    @GET("plants/{id}")
//    suspend fun getPlantById(
//        @Header("Authorization") token: String,
//        @Path("id") plantId: String
//    ): retrofit2.Response<Any>
//
//    @PUT("plants/{id}")
//    suspend fun updatePlant(
//        @Header("Authorization") token: String,
//        @Path("id") plantId: String,
//        @Body plantData: Any
//    ): retrofit2.Response<Any>
//
//    @DELETE("plants/{id}")
//    suspend fun deletePlant(
//        @Header("Authorization") token: String,
//        @Path("id") plantId: String
//    ): retrofit2.Response<Any>
}
