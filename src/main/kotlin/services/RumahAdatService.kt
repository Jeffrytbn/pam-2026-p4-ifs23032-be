package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.copyAndClose
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.RumahAdatRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IRumahAdatRepository
import java.io.File
import java.util.UUID


class RumahAdatService(private val rumahAdatRepository: IRumahAdatRepository) {

    suspend fun getAllRumahAdat(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val items = rumahAdatRepository.getRumahAdat(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar rumah adat",
            mapOf(Pair("rumahAdat", items))
        )
        call.respond(response)
    }

    suspend fun getRumahAdatById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID rumah adat tidak boleh kosong!")

        val item = rumahAdatRepository.getRumahAdatById(id)
            ?: throw AppException(404, "Data rumah adat tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data rumah adat",
            mapOf(Pair("rumahAdat", item))
        )
        call.respond(response)
    }

    private suspend fun getRumahAdatRequest(call: ApplicationCall): RumahAdatRequest {
        val req = RumahAdatRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> req.nama = part.value.trim()
                        "asal" -> req.asal = part.value.trim()
                        "deskripsi" -> req.deskripsi = part.value
                        "ciriKhas" -> req.ciriKhas = part.value
                        "fungsi" -> req.fungsi = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "file") {
                        val ext = part.originalFileName
                            ?.substringAfterLast('.', "")
                            ?.let { if (it.isNotEmpty()) ".$it" else "" }
                            ?: ""

                        val fileName = UUID.randomUUID().toString() + ext
                        val filePath = "uploads/rumah_adat/$fileName"

                        val file = File(filePath)
                        file.parentFile.mkdirs()

                        part.provider().copyAndClose(file.writeChannel())
                        req.pathGambar = filePath
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        return req
    }

    private fun validateRumahAdatRequest(req: RumahAdatRequest) {
        val validator = ValidatorHelper(req.toMap())
        validator.required("nama", "Nama tidak boleh kosong")
        validator.required("asal", "Asal tidak boleh kosong")
        validator.required("deskripsi", "Deskripsi tidak boleh kosong")
        validator.required("ciriKhas", "Ciri khas tidak boleh kosong")
        validator.required("fungsi", "Fungsi tidak boleh kosong")
        validator.required("pathGambar", "Gambar tidak boleh kosong")
        validator.validate()

        val file = File(req.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar rumah adat gagal diupload!")
        }
    }

    suspend fun createRumahAdat(call: ApplicationCall) {
        val req = getRumahAdatRequest(call)
        validateRumahAdatRequest(req)

        val exist = rumahAdatRepository.getRumahAdatByName(req.nama)
        if (exist != null) {
            val tmp = File(req.pathGambar)
            if (tmp.exists()) tmp.delete()
            throw AppException(409, "Rumah adat dengan nama ini sudah terdaftar!")
        }

        val id = rumahAdatRepository.addRumahAdat(
            req.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data rumah adat",
            mapOf(Pair("rumahAdatId", id))
        )
        call.respond(response)
    }

    suspend fun updateRumahAdat(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID rumah adat tidak boleh kosong!")

        val old = rumahAdatRepository.getRumahAdatById(id)
            ?: throw AppException(404, "Data rumah adat tidak tersedia!")

        val req = getRumahAdatRequest(call)

        if (req.pathGambar.isEmpty()) {
            req.pathGambar = old.pathGambar
        }

        validateRumahAdatRequest(req)

        if (req.nama != old.nama) {
            val exist = rumahAdatRepository.getRumahAdatByName(req.nama)
            if (exist != null) {
                val tmp = File(req.pathGambar)
                if (tmp.exists()) tmp.delete()
                throw AppException(409, "Rumah adat dengan nama ini sudah terdaftar!")
            }
        }

        if (req.pathGambar != old.pathGambar) {
            val oldFile = File(old.pathGambar)
            if (oldFile.exists()) oldFile.delete()
        }

        val ok = rumahAdatRepository.updateRumahAdat(id, req.toEntity(id))
        if (!ok) {
            throw AppException(400, "Gagal memperbarui data rumah adat!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data rumah adat",
            null
        )
        call.respond(response)
    }

    suspend fun deleteRumahAdat(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID rumah adat tidak boleh kosong!")

        val old = rumahAdatRepository.getRumahAdatById(id)
            ?: throw AppException(404, "Data rumah adat tidak tersedia!")

        val oldFile = File(old.pathGambar)

        val ok = rumahAdatRepository.removeRumahAdat(id)
        if (!ok) {
            throw AppException(400, "Gagal menghapus data rumah adat!")
        }

        if (oldFile.exists()) oldFile.delete()

        val response = DataResponse(
            "success",
            "Berhasil menghapus data rumah adat",
            null
        )
        call.respond(response)
    }

    suspend fun getRumahAdatImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)

        val item = rumahAdatRepository.getRumahAdatById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(item.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)

        call.respondFile(file)
    }
}