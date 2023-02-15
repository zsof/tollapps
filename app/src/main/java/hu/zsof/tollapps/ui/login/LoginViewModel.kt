package hu.zsof.tollapps.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.zsof.tollapps.network.repository.EventRepository
import hu.zsof.tollapps.network.response.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {

    suspend fun login(name: String): Response? {
        return eventRepository.login(name)
    }
}
