package org.delcom.repositories

import org.delcom.entities.RumahAdat

interface IRumahAdatRepository {
    suspend fun getRumahAdat(search: String): List<RumahAdat>
    suspend fun getRumahAdatById(id: String): RumahAdat?
    suspend fun getRumahAdatByName(name: String): RumahAdat?
    suspend fun addRumahAdat(rumahAdat: RumahAdat): String
    suspend fun updateRumahAdat(id: String, newRumahAdat: RumahAdat): Boolean
    suspend fun removeRumahAdat(id: String): Boolean
}