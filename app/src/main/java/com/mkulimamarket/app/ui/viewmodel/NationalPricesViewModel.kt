package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.repository.NationalPrice
import com.mkulimamarket.app.data.repository.PriceRepository
import com.mkulimamarket.app.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NationalPricesViewModel : ViewModel() {

    private val repository =
        PriceRepository(NetworkModule.api)

    sealed class UiState {

        object Loading : UiState()

        object Empty : UiState()

        data class Error(
            val message: String
        ) : UiState()

        data class Success(
            val grouped:
            Map<String, List<NationalPrice>>
        ) : UiState()
    }

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Loading)

    val uiState =
        _uiState.asStateFlow()

    private val _searchQuery =
        MutableStateFlow("")

    val searchQuery =
        _searchQuery.asStateFlow()

    private var allPrices =
        emptyList<NationalPrice>()

    init {

        load()
    }

    fun onSearchQueryChanged(
        query: String
    ) {

        _searchQuery.value = query

        applyFilter()
    }

    fun refresh() {

        repository.invalidateCache()

        load()
    }

    private fun load() {

        viewModelScope.launch {

            _uiState.value =
                UiState.Loading

            repository.loadPrices()

            allPrices =
                repository.getNationalPrices()

            applyFilter()
        }
    }

    private fun applyFilter() {

        val query =
            _searchQuery.value

        val filtered =
            if (query.isBlank()) {

                allPrices

            } else {

                allPrices.filter {

                    it.commodity.contains(query, true) ||
                            it.category.contains(query, true)
                }
            }

        _uiState.value =
            if (filtered.isEmpty()) {

                UiState.Empty

            } else {

                UiState.Success(
                    filtered.groupBy {
                        it.categoryLabel
                    }
                )
            }
    }
}