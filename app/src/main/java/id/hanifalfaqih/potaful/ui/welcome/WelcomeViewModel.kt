package id.hanifalfaqih.potaful.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WelcomeViewModel(private val repository: ApiRepository) : ViewModel() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val _authUrlState = MutableLiveData<AuthUrlState>()
    val authUrlState: LiveData<AuthUrlState> = _authUrlState

    fun fetchGoogleAuthUrl() {
        _authUrlState.value = AuthUrlState.Loading
        scope.launch {
            when (val result = repository.getGoogleAuthUrl()) {
                is Result.Success -> {
                    val response = result.data
                    if (response.status == "SUCCESS") {
                        _authUrlState.value = AuthUrlState.Success(response.data.authUrl)
                    } else {
                        _authUrlState.value = AuthUrlState.Error(response.message)
                    }
                }

                is Result.Error -> _authUrlState.value = AuthUrlState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    sealed class AuthUrlState {
        object Loading : AuthUrlState()
        data class Success(val url: String) : AuthUrlState()
        data class Error(val message: String) : AuthUrlState()
    }
}

