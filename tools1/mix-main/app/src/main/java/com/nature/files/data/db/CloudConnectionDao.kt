package com.nature.files.data.db

import androidx.room.*

@Dao
interface CloudConnectionDao {
    @Query("SELECT * FROM cloud_connections")
    suspend fun getAllConnections(): List<CloudConnectionEntity>

    @Query("SELECT * FROM cloud_connections WHERE id = :id")
    suspend fun getConnection(id: String): CloudConnectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: CloudConnectionEntity)

    @Delete
    suspend fun deleteConnection(connection: CloudConnectionEntity)
}
