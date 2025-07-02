package com.flatfinance.app.data.repositories

import com.flatfinance.app.data.dao.UserDao
import com.flatfinance.app.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    private val usersCollection = firestore.collection("users")
    
    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString()
        )
    }
    
    fun getUserById(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }
    
    fun getUsersByFlatId(flatId: String): Flow<List<User>> {
        return userDao.getUsersByFlatId(flatId)
    }
    
    suspend fun createUser(name: String, email: String, password: String): User {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw IllegalStateException("Failed to create user")
        
        val user = User(
            id = userId,
            name = name,
            email = email
        )
        
        userDao.insertUser(user)
        usersCollection.document(userId).set(user).await()
        
        return user
    }
    
    suspend fun loginUser(email: String, password: String): User {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw IllegalStateException("Failed to login")
        
        val userDoc = usersCollection.document(userId).get().await()
        val user = userDoc.toObject(User::class.java) ?: throw IllegalStateException("User not found")
        
        userDao.insertUser(user)
        
        return user
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        usersCollection.document(user.id).set(user).await()
    }
    
    suspend fun updateUserFlatId(userId: String, flatId: String?) {
        userDao.updateUserFlatId(userId, flatId)
        usersCollection.document(userId).update("flatId", flatId).await()
    }
    
    suspend fun logoutUser() {
        firebaseAuth.signOut()
    }
}