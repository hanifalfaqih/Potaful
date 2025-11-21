package id.hanifalfaqih.potaful.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.remote.response.PlantRecommendationResponse
import kotlinx.coroutines.launch

class OnboardingSharedViewModel : ViewModel() {

    // User input data
    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _skillLevel = MutableLiveData<String>()
    val skillLevel: LiveData<String> = _skillLevel

    private val _homeFrequency = MutableLiveData<String>()
    val homeFrequency: LiveData<String> = _homeFrequency

    private val _preference = MutableLiveData<String>()
    val preference: LiveData<String> = _preference

    // API Response
    private val _plantRecommendation = MutableLiveData<List<PlantRecommendationResponse>?>()
    val plantRecommendation: LiveData<List<PlantRecommendationResponse>?> = _plantRecommendation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun setLocation(location: String) {
        _location.value = location
    }

    fun setSkillLevel(skillLevel: String) {
        _skillLevel.value = skillLevel
    }

    fun setHomeFrequency(homeFrequency: String) {
        _homeFrequency.value = homeFrequency
    }

    fun setPreference(preference: String) {
        _preference.value = preference
    }

    fun getPlantRecommendation() {
        val loc = _location.value
        val skill = _skillLevel.value
        val home = _homeFrequency.value
        val pref = _preference.value

        if (loc.isNullOrEmpty() || skill.isNullOrEmpty() || home.isNullOrEmpty() || pref.isNullOrEmpty()) {
            _errorMessage.value = "Please complete all fields"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = ApiConfig.getN8NApiService().getPlantRecommendation(
                    location = loc,
                    skillLevel = skill,
                    homeFrequency = home,
                    preference = pref
                )
                _plantRecommendation.value = response
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
                _plantRecommendation.value = null
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

