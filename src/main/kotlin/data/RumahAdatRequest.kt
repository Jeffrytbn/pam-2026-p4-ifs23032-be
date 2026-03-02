package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.delcom.entities.RumahAdat

class RumahAdatRequest {
    var nama: String = ""
    var pathGambar: String = ""
    var asal: String = ""
    var deskripsi: String = ""
    var ciriKhas: String = ""
    var fungsi: String = ""

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "pathGambar" to pathGambar,
            "asal" to asal,
            "deskripsi" to deskripsi,
            "ciriKhas" to ciriKhas,
            "fungsi" to fungsi
        )
    }

    fun toEntity(oldId: String? = null): RumahAdat {
        val now = Clock.System.now()
        return RumahAdat(
            id = oldId ?: "",
            nama = nama,
            pathGambar = pathGambar,
            asal = asal,
            deskripsi = deskripsi,
            ciriKhas = ciriKhas,
            fungsi = fungsi,
            createdAt = now,
            updatedAt = now
        )
    }
}