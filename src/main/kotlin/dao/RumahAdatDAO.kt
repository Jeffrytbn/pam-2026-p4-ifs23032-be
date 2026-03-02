package org.delcom.dao

import org.delcom.tables.RumahAdatTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class RumahAdatDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RumahAdatDAO>(RumahAdatTable)

    var nama by RumahAdatTable.nama
    var pathGambar by RumahAdatTable.pathGambar
    var asal by RumahAdatTable.asal
    var deskripsi by RumahAdatTable.deskripsi
    var ciriKhas by RumahAdatTable.ciriKhas
    var fungsi by RumahAdatTable.fungsi
    var createdAt by RumahAdatTable.createdAt
    var updatedAt by RumahAdatTable.updatedAt
}