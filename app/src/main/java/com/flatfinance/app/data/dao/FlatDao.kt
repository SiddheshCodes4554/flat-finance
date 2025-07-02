package com.flatfinance.app.data.dao

import androidx.room.*
import com.flatfinance.app.data.models.Flat
import kotlinx.coroutines.flow.Flow

@Dao
interface FlatDao {
    
    @Query("SELECT * FROM flats WHERE id = :flatId")
    fun getFlatById(flatId: String): Flow<Flat?>
    
    @Query("SELECT * FROM flats WHERE code = :code LIMIT 1")
    suspend fun getFlatByCode(code: String): Flat?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlat(flat: Flat)
    
    @Update
    suspend fun updateFlat(flat: Flat)
    
    @Delete
    suspend fun deleteFlat(flat: Flat)
    
    @Query("UPDATE flats SET memberIds = :memberIds WHERE id = :flatId")
    suspend fun updateFlatMembers(flatId: String, memberIds: List<String>)
}