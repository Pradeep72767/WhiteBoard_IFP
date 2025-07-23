package com.example.whiteboard.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GenericViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(creator.invoke().javaClass)) {
            return creator.invoke() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}