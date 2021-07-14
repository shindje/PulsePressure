package com.example.pulspressure

import androidx.lifecycle.viewModelScope
import com.example.pulspressure.data.Model
import com.example.pulspressure.mvi.BaseViewModel
import com.example.pulspressure.mvi.MainContract
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

const val COLLECTION = "pulsePressure"

class MainViewModel: BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun createInitialState(): MainContract.State {
        return MainContract.State(
            MainContract.ActionState.Idle
        )
    }

    override fun handleEvent(event: MainContract.Event) {
        when (event) {
            is MainContract.Event.OnGetList -> { getList() }
            is MainContract.Event.OnAdd -> { addItem(event.item) }
        }
    }

    private fun getList() {
        viewModelScope.launch {
            setState { copy(actionState = MainContract.ActionState.Loading) }
            try {
                db.collection(COLLECTION)
                    .get()
                    .addOnSuccessListener { result ->
                        val data = mutableListOf<Model>()
                        for (document in result) {
                            val model = document.toObject(Model::class.java)
                            data.add(model)
                        }
                        data.sortBy { model -> model.addDate }
                        setState { copy(actionState = MainContract.ActionState.Success(data = data)) }
                    }
                    .addOnFailureListener { exception ->
                        throw  exception
                    }
            } catch (exception : Exception) {
                setEffect { MainContract.Effect.ShowError(exception.localizedMessage) }
            }
        }
    }

    private fun addItem(item: HashMap<String, String>) {
        viewModelScope.launch {
            setState { copy(actionState = MainContract.ActionState.Loading) }
            try {
               db.collection(COLLECTION)
                    .add(item)
                    .addOnSuccessListener { documentReference ->
                        setState { copy(actionState = MainContract.ActionState.ItemAdded) }
                    }
                    .addOnFailureListener { e ->
                        throw e
                    }
            } catch (exception: Exception) {
                setEffect { MainContract.Effect.ShowError(exception.localizedMessage) }
            }
        }
    }
}