package com.erp.data.remote

import android.util.Log
import com.erp.common.model.BaseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.reflect.KClass

class FirebaseService<T : BaseEntity>(
    private val classType: KClass<T>,
    private val collectionPath: String
) {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(collectionPath)
    
    suspend fun getAll(): List<T> {
        return try {
            collection.get().await().documents.mapNotNull { doc ->
                try {
                    val item = doc.toObject(classType.java)
                    item?.id = doc.id
                    item
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Error converting document to object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting all documents: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun getById(id: String): T? {
        return try {
            val document = collection.document(id).get().await()
            if (document.exists()) {
                val item = document.toObject(classType.java)
                item?.id = id
                item
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting document by ID: ${e.message}")
            null
        }
    }
    
    suspend fun insert(item: T): String {
        return try {
            if (item.id.isNullOrEmpty()) {
                val docRef = collection.add(item).await()
                val newId = docRef.id
                
                item.id = newId
                return newId
            } else {
                collection.document(item.id!!).set(item).await()
                return item.id!!
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error inserting document: ${e.message}", e)
            ""
        }
    }
    
    suspend fun update(item: T): Boolean {
        return try {
            if (item.id.isNullOrEmpty()) {
                return false
            }
            
            collection.document(item.id!!).set(item).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating document: ${e.message}")
            false
        }
    }
    
    suspend fun delete(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error deleting document: ${e.message}")
            false
        }
    }
} 