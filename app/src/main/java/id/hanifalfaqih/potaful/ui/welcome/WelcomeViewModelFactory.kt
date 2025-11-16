package id.hanifalfaqih.potaful.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.hanifalfaqih.potaful.data.repository.ApiRepository

class WelcomeViewModelFactory(private val repository: ApiRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

