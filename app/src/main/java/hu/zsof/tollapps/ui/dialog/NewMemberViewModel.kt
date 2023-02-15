package hu.zsof.tollapps.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.zsof.tollapps.network.repository.EventRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewMemberViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    fun applyNewMember(name: String, type: String) {
        viewModelScope.launch {
            eventRepository.applyForEvent(name, type)
        }
    }
}
