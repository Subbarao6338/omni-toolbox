package omni.toolbox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import omni.toolbox.data.local.AppDatabase
import omni.toolbox.model.ChecklistItem
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
