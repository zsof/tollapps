package hu.zsof.tollapps.network.repository

import androidx.lifecycle.MutableLiveData
import hu.zsof.tollapps.network.model.Event

object LocalDataStateService {

    var name: String = ""

    val event: MutableLiveData<Event> = MutableLiveData<Event>()
}
