package org.delcom.repositories

import org.delcom.dao.RumahAdatDAO
import org.delcom.entities.RumahAdat
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.RumahAdatTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RumahAdatRepository : IRumahAdatRepository {

    override suspend fun getRumahAdat(search: String): List<RumahAdat> = suspendTransaction {
        if (search.isBlank()) {
            RumahAdatDAO.all()
                .orderBy(RumahAdatTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            RumahAdatDAO.find { RumahAdatTable.nama.lowerCase() like keyword }
                .orderBy(RumahAdatTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getRumahAdatById(id: String): RumahAdat? = suspendTransaction {
        RumahAdatDAO
            .find { (RumahAdatTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getRumahAdatByName(name: String): RumahAdat? = suspendTransaction {
        RumahAdatDAO
            .find { (RumahAdatTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addRumahAdat(rumahAdat: RumahAdat): String = suspendTransaction {
        val now = kotlinx.datetime.Clock.System.now()

        val dao = RumahAdatDAO.new {
            nama = rumahAdat.nama
            pathGambar = rumahAdat.pathGambar
            asal = rumahAdat.asal
            deskripsi = rumahAdat.deskripsi
            ciriKhas = rumahAdat.ciriKhas
            fungsi = rumahAdat.fungsi
            createdAt = now
            updatedAt = now
        }
        dao.id.value.toString()
    }

    override suspend fun updateRumahAdat(id: String, newRumahAdat: RumahAdat): Boolean = suspendTransaction {
        val dao = RumahAdatDAO
            .find { RumahAdatTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            val now = kotlinx.datetime.Clock.System.now()
            dao.nama = newRumahAdat.nama
            dao.pathGambar = newRumahAdat.pathGambar
            dao.asal = newRumahAdat.asal
            dao.deskripsi = newRumahAdat.deskripsi
            dao.ciriKhas = newRumahAdat.ciriKhas
            dao.fungsi = newRumahAdat.fungsi
            dao.updatedAt = now
            true
        } else {
            false
        }
    }

    override suspend fun removeRumahAdat(id: String): Boolean = suspendTransaction {
        val rowsDeleted = RumahAdatTable.deleteWhere { RumahAdatTable.id eq UUID.fromString(id) }
        rowsDeleted == 1
    }
}