package com.naturetools.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.naturetools.app.data.local.AppDatabase
import com.naturetools.app.model.WaterLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class WaterTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val waterLogDao = AppDatabase.getDatabase(application).waterLogDao()
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()

    val todayTotal: Flow<Int?> = waterLogDao.getTodayTotal(getStartOfDay())

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            waterLogDao.insertLog(WaterLog(amountMl = amountMl))
        }
    }

    fun deleteLog(log: WaterLog) {
        viewModelScope.launch {
            waterLogDao.deleteLog(log)
        }
    }
}
