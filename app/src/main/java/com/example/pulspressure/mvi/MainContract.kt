package com.example.pulspressure.mvi

import com.example.pulspressure.data.Model

class MainContract {
    // Events that user performed
    sealed class Event : UiEvent {
        object OnGetList : Event()
        class OnAdd(val item: HashMap<String, String>) : Event()
    }

    // Ui View States
    data class State(
        val actionState: ActionState
    ) : UiState

    // View State that related to Random Number
    sealed class ActionState {
        object Idle : ActionState()
        object Loading : ActionState()
        data class Success(val data : List<Model>) : ActionState()
        object ItemAdded: ActionState()
    }

    // Side effects
    sealed class Effect : UiEffect {
        data class ShowError(val error: String) : Effect()
    }
}