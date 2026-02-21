package com.gabriel.danael_fernandes_react_manager.core.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource // Importe o Resource
import org.springframework.core.io.UrlResource // Importe o UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
@Service
class FileStorageService(
    @Value("\${file.upload-dir}") private val uploadDir: String,
    @Value("\${file.default-image-name}") private val defaultImageName: String
) {
    private val rootLocation: Path = Paths.get(uploadDir)

    init {
        // Garante que o diretório de upload exista
        if (Files.notExists(rootLocation)) {
            Files.createDirectories(rootLocation)
        }
    }

    fun store(): String {
        return uploadDir + defaultImageName
    }

        fun store(file: MultipartFile): String {
        // 1. Validação (não vazia, tipo de arquivo, etc.)
        if (file.isEmpty) {
            throw RuntimeException("Falha ao armazenar arquivo vazio.")
        }
        if (file.contentType !in listOf("image/jpeg", "image/png")) {
            throw RuntimeException("Tipo de arquivo não permitido.")
        }

        // 2. Gerar um nome de arquivo único para evitar conflitos e ataques
        val originalFilename = file.originalFilename ?: "unnamed"
        val extension = originalFilename.substringAfterLast('.', "")
        val uniqueFilename = "${UUID.randomUUID()}.$extension"

        // 3. Salvar o arquivo no sistema de arquivos
        val destinationFile = rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath()
        file.inputStream.use { input ->
            Files.copy(input, destinationFile)
        }

        // 4. Retornar o nome único gerado para ser salvo no banco
        return uniqueFilename
    }

    // Você também pode adicionar métodos para carregar (load) e deletar (delete) arquivos aqui.
}