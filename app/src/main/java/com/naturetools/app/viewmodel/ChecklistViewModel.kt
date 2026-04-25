package com.naturetools.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.naturetools.app.data.local.AppDatabase
import com.naturetools.app.model.ChecklistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val checklistDao = AppDatabase.getDatabase(application).checklistDao()
    val allItems: Flow<List<ChecklistItem>> = checklistDao.getAllItems()

    fun addItem(text: String) {
        viewModelScope.launch {
            checklistDao.insertItem(ChecklistItem(text = text))
        }
    }

    fun toggleItem(item: ChecklistItem) {
        viewModelScope.launch {
            checklistDao.updateItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteItem(item: ChecklistItem) {
        viewModelScope.launch {
            checklistDao.deleteItem(item)
        }
    }
}
