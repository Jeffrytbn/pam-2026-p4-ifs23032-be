package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object RumahAdatTable : UUIDTable("rumah_adat") {
    val nama = varchar("nama", 120)
    val pathGambar = varchar("path_gambar", 255)
    val asal = varchar("asal", 120)
    val deskripsi = text("deskripsi")
    val ciriKhas = text("ciri_khas")
    val fungsi = text("fungsi")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}