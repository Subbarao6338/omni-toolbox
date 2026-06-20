package com.example.naturedialer

import com.example.naturedialer.ui.viewmodel.CallViewModel
import org.junit.Test
import org.junit.Assert.*

class CallViewModelTest {
    @Test
    fun testStartCall() {
        val viewModel = CallViewModel()
        viewModel.startCall("123456")
        assertEquals("123456", viewModel.ongoingCall.value)
    }

    @Test
    fun testEndCall() {
        val viewModel = CallViewModel()
        viewModel.startCall("123456")
        viewModel.endCall()
        assertNull(viewModel.ongoingCall.value)
    }
}
