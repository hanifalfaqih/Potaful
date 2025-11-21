package id.hanifalfaqih.potaful.data.remote

import id.hanifalfaqih.potaful.data.remote.request.AddPotRequest
import id.hanifalfaqih.potaful.data.remote.response.AddPotResponse
import id.hanifalfaqih.potaful.data.remote.response.GoogleAuthUrlResponse
import id.hanifalfaqih.potaful.data.remote.response.HydrationListResponse
import id.hanifalfaqih.potaful.data.remote.response.MyPotsResponse
import id.hanifalfaqih.potaful.data.remote.response.WateringResponse
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
    ): AddPotResponse

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

    /**
     * WATERING POT
     */
    @POST("api/mypot/{pot_id}/watering")
    suspend fun wateringPot(
        @Header("Authorization") token: String,
        @Path("pot_id") potId: String
    ): WateringResponse

    /**
     * GET HYDRATION SUMMARY
     */
    @GET("api/mypot/hydration")
    suspend fun getHydrationSummary(
        @Header("Authorization") token: String
    ): HydrationListResponse
}
