package com.example.naturedialer.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallViewModel : ViewModel() {
    private val _ongoingCall = MutableStateFlow<String?>(null)
    val ongoingCall: StateFlow<String?> = _ongoingCall.asStateFlow()

    fun startCall(number: String) {
        _ongoingCall.value = number
    }

    fun endCall() {
        _ongoingCall.value = null
    }
}
