package id.hanifalfaqih.potaful.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.remote.response.PotDetailData
import id.hanifalfaqih.potaful.data.remote.response.PotItem
import id.hanifalfaqih.potaful.data.remote.response.WateringResponse
import id.hanifalfaqih.potaful.data.remote.response.WeatherResponse
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: ApiRepository) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val _potsState = MutableLiveData<PotsState>()
    val potsState: LiveData<PotsState> = _potsState

    private val _hydrationState = MutableLiveData<HydrationState>()
    val hydrationState: LiveData<HydrationState> = _hydrationState

    private val _addPotState = MutableLiveData<AddPotState>()
    val addPotState: LiveData<AddPotState> = _addPotState

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    private val _potDetailState = MutableLiveData<PotDetailState>()
    val potDetailState: LiveData<PotDetailState> = _potDetailState

    private val _wateringState = MutableLiveData<WateringState>()
    val wateringState: LiveData<WateringState> = _wateringState

    fun loadMyPots(token: String) {
        if (token.isEmpty()) {
            _potsState.value = PotsState.Error("Token not found.")
            return
        }

        _potsState.value = PotsState.Loading

        scope.launch {
            when (val result = repository.getMyPots(token)) {
                is Result.Success -> {
                    val response = result.data
                    if (response.status == "SUCCESS") {
                        _potsState.value = PotsState.Success(
                            pots = response.data.pots,
                            total = response.data.total
                        )
                    } else {
                        _potsState.value = PotsState.Error(response.message)
                    }
                }

                is Result.Error -> {
                    _potsState.value = PotsState.Error(result.message)
                }

                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun loadHydrationSummary(token: String) {
        if (token.isEmpty()) {
            _hydrationState.value = HydrationState.Error("Token not found.")
            return
        }
        _hydrationState.value = HydrationState.Loading
        scope.launch {
            when (val result = repository.getHydrationSummary(token)) {
                is Result.Success -> {
                    val resp = result.data
                    if (resp.status == "SUCCESS") {
                        _hydrationState.value =
                            HydrationState.Success(resp.data.pots, resp.data.total)
                    } else {
                        _hydrationState.value = HydrationState.Error(resp.message)
                    }
                }

                is Result.Error -> _hydrationState.value = HydrationState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun addPot(token: String, potId: String) {
        if (token.isEmpty()) {
            _addPotState.value = AddPotState.Error("Token not found")
            return
        }

        if (potId.isEmpty()) {
            _addPotState.value = AddPotState.Error("Pot ID cannot be empty")
            return
        }

        _addPotState.value = AddPotState.Loading

        scope.launch {
            when (val result = repository.addPot(token, potId)) {
                is Result.Success -> {
                    val response = result.data
                    if (response.status == "SUCCESS") {
                        _addPotState.value = AddPotState.Success(response.message)
                    } else {
                        _addPotState.value = AddPotState.Error(response.message)
                    }
                }

                is Result.Error -> {
                    _addPotState.value = AddPotState.Error(result.message)
                    Log.d("DashboardViewModel", "Add Pot Error: ${result.message}")
                }

                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun loadWeather(city: String, apiKey: String) {
        if (city.isEmpty()) return
        _weatherState.value = WeatherState.Loading
        scope.launch {
            when (val result = repository.getCurrentWeather(city, apiKey)) {
                is Result.Success -> {
                    _weatherState.value = WeatherState.Success(result.data)
                }

                is Result.Error -> {
                    _weatherState.value = WeatherState.Error(result.message)
                }

                is Result.Loading -> {}
            }
        }
    }

    fun loadPotDetail(token: String, potId: String) {
        if (token.isEmpty() || potId.isEmpty()) return

        _potDetailState.value = PotDetailState.Loading(potId)

        scope.launch {
            when (val result = repository.getPotDetail(token, potId)) {
                is Result.Success -> {
                    _potDetailState.value = PotDetailState.Success(potId, result.data.data)
                }

                is Result.Error -> {
                    _potDetailState.value = PotDetailState.Error(potId, result.message)
                }

                is Result.Loading -> {}
            }
        }
    }

    fun wateringPot(token: String, potId: String) {
        if (token.isEmpty()) {
            _wateringState.value = WateringState.Error("Token not found")
            return
        }

        if (potId.isEmpty()) {
            _wateringState.value = WateringState.Error("Pot ID is empty")
            return
        }

        _wateringState.value = WateringState.Loading(potId)

        scope.launch {
            when (val result = repository.wateringPot(token, potId)) {
                is Result.Success -> {
                    val resp: WateringResponse = result.data
                    if (resp.status == "SUCCESS") {
                        _wateringState.value = WateringState.Success(potId, resp.message)
                    } else {
                        _wateringState.value = WateringState.Error(resp.message)
                    }
                }

                is Result.Error -> {
                    _wateringState.value = WateringState.Error(result.message)
                }

                is Result.Loading -> {}
            }
        }
    }

    fun resetAddPotState() {
        _addPotState.value = AddPotState.Idle
    }

    fun resetWateringState() {
        _wateringState.value = WateringState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    sealed class PotsState {
        object Loading : PotsState()
        data class Success(val pots: List<PotItem>, val total: Int) : PotsState()
        data class Error(val message: String) : PotsState()
    }

    sealed class HydrationState {
        object Loading : HydrationState()
        data class Success(
            val pots: List<id.hanifalfaqih.potaful.data.remote.response.HydrationPotItem>,
            val total: Int
        ) : HydrationState()

        data class Error(val message: String) : HydrationState()
    }

    sealed class AddPotState {
        object Idle : AddPotState()
        object Loading : AddPotState()
        data class Success(val message: String) : AddPotState()
        data class Error(val message: String) : AddPotState()
    }

    sealed class WeatherState {
        object Loading : WeatherState()
        data class Success(val data: WeatherResponse) : WeatherState()
        data class Error(val message: String) : WeatherState()
    }

    sealed class PotDetailState {
        data class Loading(val potId: String) : PotDetailState()
        data class Success(val potId: String, val data: PotDetailData) : PotDetailState()
        data class Error(val potId: String, val message: String) : PotDetailState()
    }

    sealed class WateringState {
        object Idle : WateringState()
        data class Loading(val potId: String) : WateringState()
        data class Success(val potId: String, val message: String) : WateringState()
        data class Error(val message: String) : WateringState()
    }
}