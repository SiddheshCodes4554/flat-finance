package com.flatfinance.app.data.repositories

import com.flatfinance.app.data.dao.FlatDao
import com.flatfinance.app.data.models.Flat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlatRepository @Inject constructor(
    private val flatDao: FlatDao,
    private val firestore: FirebaseFirestore
) {
    
    private val flatsCollection = firestore.collection("flats")
    
    fun getFlatById(flatId: String): Flow<Flat?> {
        return flatDao.getFlatById(flatId)
    }
    
    suspend fun getFlatByCode(code: String): Flat? {
        return flatDao.getFlatByCode(code)
    }
    
    suspend fun createFlat(name: String, creatorId: String): Flat {
        val flatId = UUID.randomUUID().toString()
        val code = generateFlatCode()
        
        val flat = Flat(
            id = flatId,
            name = name,
            code = code,
            creatorId = creatorId,
            memberIds = listOf(creatorId)
        )
        
        flatDao.insertFlat(flat)
        flatsCollection.document(flatId).set(flat).await()
        
        return flat
    }
    
    suspend fun updateFlat(flat: Flat) {
        flatDao.updateFlat(flat)
        flatsCollection.document(flat.id).set(flat).await()
    }
    
    suspend fun addMemberToFlat(flatId: String, userId: String) {
        val flat = flatDao.getFlatById(flatId).value ?: return
        val updatedMemberIds = flat.memberIds.toMutableList().apply {
            if (!contains(userId)) add(userId)
        }
        
        flatDao.updateFlatMembers(flatId, updatedMemberIds)
        flatsCollection.document(flatId).update("memberIds", updatedMemberIds).await()
    }
    
    suspend fun removeMemberFromFlat(flatId: String, userId: String) {
        val flat = flatDao.getFlatById(flatId).value ?: return
        val updatedMemberIds = flat.memberIds.toMutableList().apply {
            remove(userId)
        }
        
        flatDao.updateFlatMembers(flatId, updatedMemberIds)
        flatsCollection.document(flatId).update("memberIds", updatedMemberIds).await()
    }
    
    private fun generateFlatCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}