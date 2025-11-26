package id.hanifalfaqih.potaful.ui.allpots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.remote.response.HydrationPotItem
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AllPotsViewModel(private val repository: ApiRepository) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val _hydrationState = MutableLiveData<HydrationState>()
    val hydrationState: LiveData<HydrationState> = _hydrationState

    private val _wateringState = MutableLiveData<WateringState>()
    val wateringState: LiveData<WateringState> = _wateringState

    fun loadHydrationPots(token: String) {
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
                        _hydrationState.value = HydrationState.Success(resp.data.pots)
                    } else {
                        _hydrationState.value = HydrationState.Error(resp.message)
                    }
                }

                is Result.Error -> _hydrationState.value = HydrationState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun wateringPot(token: String, potId: String) {
        if (token.isEmpty()) {
            _wateringState.value = WateringState.Error("Token not found", potId)
            return
        }
        _wateringState.value = WateringState.Loading

        scope.launch {
            when (val result = repository.wateringPot(token, potId)) {
                is Result.Success -> {
                    val resp = result.data
                    if (resp.status == "SUCCESS") {
                        _wateringState.value = WateringState.Success(resp.message, potId)
                    } else {
                        _wateringState.value = WateringState.Error(resp.message, potId)
                    }
                }

                is Result.Error -> _wateringState.value = WateringState.Error(result.message, potId)
                is Result.Loading -> {}
            }
        }
    }

    fun resetWateringState() {
        _wateringState.value = WateringState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    sealed class HydrationState {
        object Loading : HydrationState()
        data class Success(val pots: List<HydrationPotItem>) : HydrationState()
        data class Error(val message: String) : HydrationState()
    }

    sealed class WateringState {
        object Idle : WateringState()
        object Loading : WateringState()
        data class Success(val message: String, val potId: String) : WateringState()
        data class Error(val message: String, val potId: String) : WateringState()
    }
}

