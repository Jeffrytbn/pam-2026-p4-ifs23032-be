package org.delcom.entities

import kotlinx.datetime.Instant

data class RumahAdat(
    val id: String,
    val nama: String,
    val pathGambar: String,
    val asal: String,
    val deskripsi: String,
    val ciriKhas: String,
    val fungsi: String,
    val createdAt: Instant,
    val updatedAt: Instant
)