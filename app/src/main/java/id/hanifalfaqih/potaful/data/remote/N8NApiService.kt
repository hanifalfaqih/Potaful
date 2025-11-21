package id.hanifalfaqih.potaful.data.remote

import id.hanifalfaqih.potaful.data.remote.response.PlantRecommendationResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface N8NApiService {
    @POST("webhook/plant-recommendation")
    suspend fun getPlantRecommendation(
        @Query("location") location: String,
        @Query("skill_level") skillLevel: String,
        @Query("home_frequency") homeFrequency: String,
        @Query("preference") preference: String
    ): List<PlantRecommendationResponse>
}

