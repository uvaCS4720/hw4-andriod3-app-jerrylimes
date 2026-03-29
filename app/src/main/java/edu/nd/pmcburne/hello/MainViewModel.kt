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
    val availableTags: List<String> = emptyList(),
    val allLocations: List<Location> = emptyList(),
    val selectedTag: String? = null
)

class MainViewModel(
    val initialCounterValue: Int = 0
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState(selectedTag = "core"))
    private val repository = LocationRepository()

    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

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
        _uiState.update {
            it.copy(
                availableTags = uniqueTags,
                allLocations = locations
            )
        }
    }

    fun selectTag(tag: String?) {
        _uiState.update { it.copy(selectedTag = tag) }
    }

    init {
        loadLocations()
    }
}