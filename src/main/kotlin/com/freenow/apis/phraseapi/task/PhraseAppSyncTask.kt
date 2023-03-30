package com.freenow.apis.phraseapi.task

import com.freenow.apis.phraseapi.client.PhraseApiClient
import com.freenow.apis.phraseapi.client.PhraseApiClientImpl
import com.freenow.apis.phraseapi.client.model.PhraseLocale
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Suppress("TooGenericExceptionCaught","SwallowedException")
class PhraseAppSyncTask(
    private val config: PhraseAppSyncTaskConfig
) : Runnable {

    private val log = LoggerFactory.getLogger(PhraseAppSyncTask::class.java.name)
    private val rootMessagesDirectory: Path
    private val client: PhraseApiClient

    private val defaultBranch = "master"

    init {
        client = PhraseApiClientImpl(config.url, config.authKey)

        rootMessagesDirectory = try {
            val classPathResource = ClassPathResource("/").file.path
            Paths.get(classPathResource)
        } catch (@Suppress("SwallowedException") e: Exception) {
            log.warn("could not get default ClassPathResource. use /generated-resources/ instead")
            Paths.get(config.generatedResourcesFolder)
        }

        Files.createDirectories(rootMessagesDirectory)
    }

    override fun run() {
        try {
            log.debug("Phrase App sync started")

            config.branches.forEach { branch ->
                client.locales(config.projectId, branch)
                    .orEmpty()
                    .forEach {
                        updateLocaleFile(it, branch)
                    }
            }
            log.debug("Phrase App sync finished")
        } catch (ex: Exception) {
            log.warn("PhraseApp sync failed", ex)
        }
    }


    private fun handleBranchPath(branch: String): Path = when (defaultBranch == branch)
    {
        true -> rootMessagesDirectory.resolve(config.messagesFolder)
        false -> rootMessagesDirectory.resolve("${config.messagesFolder}_$branch")
    }

    private fun updateLocaleFile(locale: PhraseLocale, branch: String) {
        try {
            val byteArray = client.downloadLocaleAsProperties(
                config.projectId,
                locale.id,
                config.escapeSingleQuotes,
                branch,
                config.tags
            )

            if (byteArray != null) {
                val fileName = createFileName(locale.code)
                val path = handleBranchPath(branch).resolve(fileName)
                if (!Files.exists(path)) {
                    Files.createDirectories(path.parent)
                    Files.createFile(path)
                }
                Files.write(path, byteArray)
            }
        } catch (ex: Exception) {
            log.warn("PhraseApp sync failed for $locale", ex)
        }
    }

    private fun createFileName(code: String): String {
        return config.messagesFilePrefix + code.replace("-", "_") + config.messagesFilePostfix
    }
}

data class PhraseAppSyncTaskConfig @JvmOverloads constructor(
    val url: String,
    val authKey: String,
    val projectId: String,
    val branches: List<String> = listOf("master"),
    val tags: String?,
    val generatedResourcesFolder: String = "generated-resources/",
    val messagesFolder: String = "messages",
    val messagesFilePostfix: String = ".properties",
    val messagesFilePrefix: String = "messages_",
    val escapeSingleQuotes: Boolean = false
)
