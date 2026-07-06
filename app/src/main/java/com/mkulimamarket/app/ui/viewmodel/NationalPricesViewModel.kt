package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.repository.NationalPrice
import com.mkulimamarket.app.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NationalPricesViewModel : ViewModel() {

    private val repository = NetworkModule.priceRepository

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Success(
            val grouped: Map<String, List<NationalPrice>>
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var all = emptyList<NationalPrice>()

    init { load() }

    fun refresh() {
        repository.invalidateCache()
        load()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.loadPrices()

            all = repository.getNationalPrices()

            applyFilter()
        }
    }

    private fun applyFilter() {

        val q = _searchQuery.value

        val filtered = if (q.isBlank()) all else all.filter {
            it.commodity.contains(q, true) ||
                    it.category.contains(q, true)
        }

        _uiState.value =
            if (filtered.isEmpty()) UiState.Empty
            else UiState.Success(filtered.groupBy { it.category })
    }
}