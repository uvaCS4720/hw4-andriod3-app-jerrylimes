package edu.nd.pmcburne.hello

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hello.data.Location
import edu.nd.pmcburne.hello.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUIState(
    val counterValue: Int,
    val availableTags: List<String> = emptyList()
)

class MainViewModel(
    val initialCounterValue: Int = 0
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState(initialCounterValue))
    private val repository = LocationRepository()

    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun incrementCounter() {
        _uiState.update { currentState ->
            currentState.copy(counterValue = _uiState.value.counterValue + 1)
        }
    }

    fun decrementCounter() {
        _uiState.update { currentState ->
            currentState.copy(counterValue = _uiState.value.counterValue - 1)
        }
    }

    fun resetCounter() {
        _uiState.update { currentState ->
            currentState.copy(counterValue = 0)
        }
    }

    fun loadLocations() {
        viewModelScope.launch {
            try {
                val url = "https://www.cs.virginia.edu/~wxt4gm/placemarks.json"
                val locations = repository.fetchLocations(url)
                updateTags(locations)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching locations:", e)
            }
        }
    }

    fun updateTags(locations: List<Location>) {
        val uniqueTags = locations
            .flatMap { it.tag_list }
            .distinct()
            .sorted()
        _uiState.update { it.copy(availableTags = uniqueTags) }
    }

    val isDecrementEnabled: Boolean
        get() = _uiState.value.counterValue > 0
    val isResetEnabled: Boolean
        get() = _uiState.value.counterValue > 0

    init {
        loadLocations()
    }
}