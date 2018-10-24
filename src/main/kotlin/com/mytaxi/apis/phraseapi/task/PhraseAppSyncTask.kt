package com.mytaxi.apis.phraseapi.task

import com.mytaxi.apis.phraseapi.client.PhraseApiClient
import com.mytaxi.apis.phraseapi.client.PhraseApiClientImpl
import com.mytaxi.apis.phraseapi.client.model.PhraseLocale
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Suppress("TooGenericExceptionCaught")
class PhraseAppSyncTask(
    url: String,
    authKey: String,
    private val projectId: String
) : Runnable {

    private var log = LoggerFactory.getLogger(PhraseAppSyncTask::class.java.name)

    companion object {
        private const val MESSAGE_FILE_PREFIX = "messages_"
        private const val MESSAGE_FILE_POSTFIX = ".properties"
        private const val GENERATED_RESOURCES_FOLDER = "generated-resources/"
        private const val MESSAGES_FOLDER = "messages/"
    }

    private var messagesDirectory: Path

    private val client: PhraseApiClient

    init {
        client = PhraseApiClientImpl(url, authKey)

        try {
            val classPathResource = ClassPathResource("/").file.path
            messagesDirectory = Paths.get("$classPathResource/$MESSAGES_FOLDER")
        } catch (e: Exception) {
            messagesDirectory = Paths.get(GENERATED_RESOURCES_FOLDER + MESSAGES_FOLDER)
            log.error("could not get default ClassPathResource. use /generated-resources/ instead")
        }

        Files.createDirectories(messagesDirectory)
    }

    override fun run() {
        try {
            log.info("Phrase App sync started")
            client.locales(projectId)
                .orEmpty()
                .forEach {
                    updateLocaleFile(it)
                }
            log.info("Phrase App sync finished")
        } catch (ex: Exception) {
            log.warn("PhraseApp sync failed", ex)
        }
    }

    private fun updateLocaleFile(locale: PhraseLocale) {
        try {
            val byteArray = client.downloadLocaleAsProperties(projectId, locale.id, false)
            if (byteArray != null) {
                val fileName = createFileName(locale.code)
                val path = messagesDirectory.resolve(fileName)
                if (!Files.exists(path)) {
                    Files.createFile(path)
                }
                Files.write(path, byteArray)
            }
        } catch (ex: Exception) {
            log.warn("PhraseApp sync failed for $locale", ex)
        }
    }

    private fun createFileName(code: String): String {
        return MESSAGE_FILE_PREFIX + code.replace("-", "_") + MESSAGE_FILE_POSTFIX
    }
}
