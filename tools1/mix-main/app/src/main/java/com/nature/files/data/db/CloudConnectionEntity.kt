package com.nature.files.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_connections")
data class CloudConnectionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "SMB", "SFTP"
    val host: String,
    val port: Int,
    val username: String,
    val password: String
)
