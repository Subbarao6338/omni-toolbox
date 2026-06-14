package omni.toolbox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import omni.toolbox.data.local.AppDatabase
import omni.toolbox.model.Password
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val passwordDao = AppDatabase.getDatabase(application).passwordDao()

    val allPasswords: StateFlow<List<Password>> = passwordDao.getAllPasswords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(password: Password) = viewModelScope.launch {
        passwordDao.insertPassword(password)
    }

    fun update(password: Password) = viewModelScope.launch {
        passwordDao.updatePassword(password)
    }

    fun delete(password: Password) = viewModelScope.launch {
        passwordDao.deletePassword(password)
    }
}
