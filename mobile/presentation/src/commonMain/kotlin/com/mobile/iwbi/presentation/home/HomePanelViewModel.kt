package com.mobile.iwbi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwbi.domain.shopping.ShoppingItem
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.presentation.uistate.HomePanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomePanelViewModel(
    private val helloWorldServicePort: HelloWorldServicePort
) : ViewModel() {
    val _uiState = MutableStateFlow(HomePanelUiState())
    val uiState: StateFlow<HomePanelUiState> = _uiState.asStateFlow()

    fun toggleItem(index: Int) {
        _uiState.value = _uiState.value.copy(
            notes = _uiState.value.notes.toMutableList().apply {
            this[index] = this[index].copy(isChecked = !this[index].isChecked)
            })
    }

    fun addTemplate(template: List<ShoppingItem>) {
        _uiState.value = _uiState.value.copy(
            templates = _uiState.value.templates + listOf(template)
        )
    }

    fun helloWorld() {
        viewModelScope.launch {
            val result = helloWorldServicePort.getHelloWorld()
            println("NIKOLA: $result")
        }
    }
}