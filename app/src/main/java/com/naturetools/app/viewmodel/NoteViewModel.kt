package com.naturetools.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.naturetools.app.data.local.AppDatabase
import com.naturetools.app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao = AppDatabase.getDatabase(application).noteDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allNotes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .combine(_searchQuery) { notes, query ->
            if (query.isBlank()) notes
            else notes.filter { it.title.contains(query, true) || it.content.contains(query, true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveNote(title: String, content: String, id: Long = 0) {
        viewModelScope.launch {
            if (id == 0L) {
                noteDao.insertNote(Note(title = title, content = content))
            } else {
                noteDao.insertNote(Note(id = id, title = title, content = content))
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
}
