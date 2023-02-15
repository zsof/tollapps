package hu.zsof.tollapps.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.zsof.tollapps.network.repository.EventRepository
import hu.zsof.tollapps.network.repository.LocalDataStateService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    stateService: LocalDataStateService,
    private val eventRepository: EventRepository
) :
    ViewModel() {

    var event = stateService.event
    fun getEvent(name: String) {
        viewModelScope.launch {
            val e = eventRepository.getCurrentEvent(name)
            if (e != null) {
                event.postValue(e)
            }
        }
    }

    fun deleteApply(name: String) {
        viewModelScope.launch {
            val e = eventRepository.deleteApplicationForEvent(name)
            if (e != null) {
                event.postValue(e)
            }
        }
    }
}
