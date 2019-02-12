package com.mytaxi.apis.phraseapi.client

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.mytaxi.apis.phraseapi.client.model.CreateKey
import com.mytaxi.apis.phraseapi.client.model.CreatePhraseLocale
import com.mytaxi.apis.phraseapi.client.model.CreatePhraseProject
import com.mytaxi.apis.phraseapi.client.model.CreateTranslation
import com.mytaxi.apis.phraseapi.client.model.Key
import com.mytaxi.apis.phraseapi.client.model.Keys
import com.mytaxi.apis.phraseapi.client.model.PhraseLocale
import com.mytaxi.apis.phraseapi.client.model.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.client.model.PhraseLocales
import com.mytaxi.apis.phraseapi.client.model.PhraseProject
import com.mytaxi.apis.phraseapi.client.model.PhraseProjects
import com.mytaxi.apis.phraseapi.client.model.Translation
import com.mytaxi.apis.phraseapi.client.model.Translations
import com.mytaxi.apis.phraseapi.client.model.UpdatePhraseProject
import feign.Feign
import feign.RequestInterceptor
import feign.Response
import feign.form.FormEncoder
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate

@Suppress("MaxLineLength", "TooManyFunctions", "TooGenericExceptionCaught")
class PhraseApiClientImpl : PhraseApiClient {
    private var log = LoggerFactory.getLogger(PhraseApiClientImpl::class.java.name)

    private val client: PhraseApi
    private val config: PhraseApiClientConfig
    private val responseCache: Cache<String, Any> // key url, value

    // Response
    private val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    constructor(client: PhraseApi) {
        config = PhraseApiClientConfig(
            authKey = ""
        )
        this.client = client
        responseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(config.responseCacheExpireAfterWriteMilliseconds, TimeUnit.MILLISECONDS)
            .build<String, Any>()
        runCleaningTimer()
    }

    constructor(config: PhraseApiClientConfig) {
        this.config = config
        client = PhraseApiImpl(config)
        responseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(config.responseCacheExpireAfterWriteMilliseconds, TimeUnit.MILLISECONDS)
            .build<String, Any>()
        runCleaningTimer()
    }

    constructor(url: String, authKey: String): this(PhraseApiClientConfig(url, authKey))

    private fun runCleaningTimer() {
        Timer("responseCache", true).scheduleAtFixedRate(config.cleanUpFareRateMilliseconds, config.cleanUpFareRateMilliseconds) {
            try {
                log.debug("CleanUp of responses cache started")
                responseCache.cleanUp()
                log.debug("CleanUp of responses cache finished")
            } catch (ex: Exception) {
                log.debug("Error during responses cleanup", ex)
            }
        }
    }

    override fun projects(): PhraseProjects? {
        val response = client.projects()
        log.debug("Get projects")
        return processResponse("GET/api/v2/projects", response)
    }

    override fun project(projectId: String): PhraseProject? {
        val response = client.project(projectId)
        log.debug("Get project [$projectId]")
        return processResponse("GET/api/v2/projects/$projectId", response)
    }

    override fun deleteProject(projectId: String): Boolean {
        log.debug("Delete project [$projectId]")
        val response = client.deleteProject(projectId)
        processResponse<Void>("DELETE/api/v2/projects/$projectId", response)
        return response.status() == HttpStatus.SC_NO_CONTENT
    }

    override fun createProject(phraseProject: CreatePhraseProject): PhraseProject? {
        log.debug("Create project [$phraseProject]")
        val response = client.createProject(
            phraseProject.name,
            phraseProject.projectImage,
            phraseProject.mainFormat,
            phraseProject.sharesTranslationMemory,
            phraseProject.removeProjectImage,
            phraseProject.accountId
        )
        return processResponse("POST/api/v2/projects", response)
    }

    override fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject? {
        log.debug("Update project [$phraseProject]")
        val response = client.updateProject(
            projectId,
            phraseProject.name,
            phraseProject.projectImage,
            phraseProject.mainFormat,
            phraseProject.sharesTranslationMemory,
            phraseProject.removeProjectImage,
            phraseProject.accountId
        )
        return processResponse("PUT/api/v2/projects/$projectId", response)
    }

    override fun locales(projectId: String, localeId: String): PhraseLocale? {
        log.debug("Get locale [$localeId] for project [$projectId]")
        val response = client.locale(projectId, localeId)
        return processResponse("GET/api/v2/projects/$projectId/locales/$localeId", response)
    }

    override fun locales(projectId: String): PhraseLocales? {
        log.debug("Get locales for project [$projectId]")
        val response = client.locales(projectId)
        return processResponse("GET/api/v2/projects/$projectId/locales", response)
    }

    override fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale? {
        log.debug("Create locale [$locale] for project [$projectId]")
        val response = client.createLocale(
            projectId,
            locale.name,
            locale.code,
            null,
            locale.default,
            locale.mail,
            locale.rtl,
            locale.sourceLocaleId,
            locale.unverifyNewTranslations,
            locale.unverifyUpdatedTranslations,
            locale.autotranslate
        )
        return processResponse("POST/api/v2/projects/$projectId/locales", response)
    }

    override fun downloadLocale(projectId: String, localeId: String): PhraseLocaleMessages? {
        log.debug("Download locale [$localeId] for project [$projectId]")
        val response = client.downloadLocale(projectId, localeId, "json")
        return processResponse("GET/api/v2/projects/$projectId/locales/$localeId/download?file_format=json", response)
    }

    override fun downloadLocaleAsProperties(projectId: String, localeId: String, escapeSingleQuotes: Boolean): ByteArray? {
        log.debug("Download locale [$localeId] for project [$projectId]")
        val response = client.downloadLocale(projectId, localeId, "properties", escapeSingleQuotes)
        return processResponse("GET/api/v2/projects/$projectId/locales/$localeId/download?file_format=json", response)
    }

    override fun deleteLocale(projectId: String, localeId: String) {
        log.debug("Delete locale [$localeId] for project [$projectId]")
        client.deleteLocale(projectId, localeId)
    }

    override fun translations(project: PhraseProject, locale: PhraseLocale): Translations? {
        log.debug("Get translations for locale [${locale.id}] for project [${project.id}]")
        val response = client.translations(project.id, locale.id)
        return processResponse("GET/api/v2/projects/${project.id}/locales/${locale.id}/translations", response)
    }

    override fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation? {
        log.debug("Creating the translation [${createTranslation.content}] for locale [${createTranslation.localeId}] " +
            "for project [$projectId] for key [${createTranslation.keyId}]")
        val response = client.createTranslation(projectId, createTranslation.localeId, createTranslation.keyId, createTranslation.content)
        return processResponse("POST/api/v2/projects/$projectId/translations", response)
    }

    override fun createTranslation(projectId: String, localeId: String, keyId: String, content: String): Translation? {
        log.debug("Creating the translation [$content] for locale [$localeId] " +
                "for project [$projectId] for key [$keyId]")
        val response = client.createTranslation(projectId, localeId, keyId, content)
        return processResponse("POST/api/v2/projects/$projectId/translations", response)
    }

    override fun createKey(projectId: String, createKey: CreateKey): Key? {
        log.debug("Creating keys [${createKey.name}] for project [$projectId]")
        val response = client.createKey(
            projectId,
            createKey.name,
            createKey.tags,
            createKey.description,
            createKey.branch,
            createKey.plural,
            createKey.namePlural,
            createKey.dataType,
            createKey.maxCharactersAllowed,
            createKey.screenshot,
            createKey.removeScreenshot,
            createKey.unformatted,
            createKey.xmlSpacePreserve,
            createKey.originalFile,
            createKey.localizedFormatString,
            createKey.localizedFormatKey
        )
        return processResponse("POST/api/v2/projects/$projectId/keys", response)
    }

    override fun createKey(projectId: String, name: String, tags: ArrayList<String>?): Key? {
        log.debug("Creating keys [$name] for project [$projectId]")
        val response = client.createKey(projectId, name, tags)
        return processResponse("POST/api/v2/projects/$projectId/keys", response)
    }


    override fun searchKey(projectId: String, localeId: String?, q: String?): Keys? {
        log.debug("Searching keys for project [$projectId] - locale [$localeId] - query [$q]")
        val response = client.searchKey(projectId, localeId, q)
        return processResponse("POST/api/v2/projects/$projectId/keys/search", response)
    }

    override fun deleteKey(projectId: String, keyId: String): Boolean {
        log.debug("Deleting key [$keyId] for project [$projectId]")
        val response = client.deleteKey(projectId, keyId)
        processResponse<Void>("DELETE/api/v2/projects/$projectId/keys/$keyId", response)
        return response.status() == HttpStatus.SC_NO_CONTENT
    }

    private inline fun <reified T> processResponse(key: String, response: Response): T? {
        log.debug("Response : status [${response.status()}] \n headers [${response.headers()}]")

        if (response.status() !in HttpStatus.SC_OK..HttpStatus.SC_BAD_REQUEST) {
            val message = response.body()?.asReader()?.readText()
            val warningMessage = key.plus("\n")
                .plus("Status : ${response.status()}")
                .plus("\n")
                .plus("Headers : \n ${response.headers().map { it -> it.toString().plus("\n") }}")
                .plus("\n")
                .plus("Body : $message")
            log.warn(warningMessage)
            throw PhraseAppApiException(response.status(), message)
        }

        return if (response.status() == HttpStatus.SC_NOT_MODIFIED) {
            val cacheResponse = responseCache.getIfPresent(key) as T
            log.debug("Cached response : $cacheResponse")
            cacheResponse
        } else {

            val contentType = response.headers()
                .asSequence()
                .firstOrNull { it -> HttpHeaders.CONTENT_TYPE.equals(it.key, true) }
                ?.value
                ?.first() ?: throw PhraseAppApiException("Content type is NULL")

            val mediaType = MediaType.parse(contentType)
            val responseObject = when (mediaType.subtype()) {
                MediaType.JSON_UTF_8.subtype() -> {
                    getObject(response)
                }
                MediaType.OCTET_STREAM.subtype() -> {
                    IOUtils.toByteArray(response.body().asInputStream()) as T
                }
                else -> {
                    throw PhraseAppApiException("Content Type $contentType is not supported")
                }
            }

            getETag(response)?.also {
                responseCache.put(key, responseObject)
                (client as CacheApi).putETag(key, it)
            }

            responseObject
        }
    }

    private inline fun <reified T> getObject(response: Response): T {
        try {
            val responseObject = gson.fromJson(response.body().asReader(), T::class.java)
            log.debug("Response object : $responseObject")
            return responseObject
        } catch (ex: JsonSyntaxException) {
            log.warn(ex.message)
            throw PhraseAppApiException("Error during parsing response", ex)
        } catch (ex: JsonIOException) {
            log.warn(ex.message)
            throw PhraseAppApiException("Error during parsing response", ex)
        }
    }

    private fun getETag(response: Response): String? {
        val eTagHeader = response.headers()
            .entries
            .find { it.key.equals(HttpHeaders.ETAG, true) }
        return eTagHeader?.value?.first()
    }

    @Suppress("TooManyFunctions")
    private class PhraseApiImpl(
        val config: PhraseApiClientConfig
    ) : PhraseApi, CacheApi {

        private var log = LoggerFactory.getLogger(PhraseApiImpl::class.java.name)

        private val target: PhraseApi
        private val eTagCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<String, String>() // key : url, value : eTag

        init {
            target = Feign.builder()
                .requestInterceptor(getInterceptor())
                .decoder(GsonDecoder())
                .encoder(FormEncoder(GsonEncoder()))
                .target(PhraseApi::class.java, config.url)

            Timer("eTagCache", true).scheduleAtFixedRate(config.cleanUpFareRateMilliseconds, config.cleanUpFareRateMilliseconds) {
                try {
                    log.debug("CleanUp of eTags cache started")
                    eTagCache.cleanUp()
                    log.debug("CleanUp of eTags cache finished")
                } catch (ex: Exception) {
                    log.debug("Error during eTags cleanup", ex)
                }
            }
        }

        private fun getInterceptor() = RequestInterceptor {
            apply {
                it.header(HttpHeaders.IF_NONE_MATCH, getETag(it.request().method() + it.request().url()))
                it.header(HttpHeaders.AUTHORIZATION, "token ${config.authKey}")
            }
        }

        override fun putETag(key: String, eTag: String) {
            eTagCache.put(key, eTag)
        }

        override fun getETag(key: String): String? = eTagCache.getIfPresent(key)

        //PROJECT
        override fun projects(): Response = target.projects()

        override fun project(projectId: String): Response = target.project(projectId)

        override fun createProject(
            name: String,
            projectImage: File?,
            mainFormat: String?,
            sharesTranslationMemory: String?,
            removeProjectImage: Boolean?,
            accountId: String?
        ): Response = target.createProject(
            name = name,
            mainFormat = mainFormat,
            accountId = accountId,
            projectImage = projectImage,
            removeProjectImage = removeProjectImage,
            sharesTranslationMemory = sharesTranslationMemory
        )

        override fun updateProject(
            projectId: String,
            name: String,
            projectImage: File?,
            mainFormat: String?,
            sharesTranslationMemory: String?,
            removeProjectImage: Boolean?,
            accountId: String?
        ): Response = target.updateProject(
            projectId = projectId,
            name = name,
            mainFormat = mainFormat,
            accountId = accountId,
            projectImage = projectImage,
            removeProjectImage = removeProjectImage,
            sharesTranslationMemory = sharesTranslationMemory
        )

        override fun deleteProject(projectId: String): Response = target.deleteProject(projectId)


        //LOCALE
        override fun locales(projectId: String): Response = target.locales(projectId)

        override fun locale(projectId: String, localeId: String): Response = target.locale(projectId, localeId)

        override fun downloadLocale(projectId: String, localeId: String, fileFormat: String, escapeSingleQuotes: Boolean?):
            Response = target.downloadLocale(projectId, localeId, fileFormat, escapeSingleQuotes)

        override fun createLocale(
            projectId: String,
            name: String,
            code: String,
            branch: String?,
            default: Boolean?,
            mail: Boolean?,
            rtl: Boolean?,
            sourceLocaleId: String?,
            unverifyNewTranslations: String?,
            unverifyUpdatedTranslations: String?,
            autotranslate: String?
        ): Response = target.createLocale(projectId, name, code, branch, default, mail, rtl, sourceLocaleId, unverifyNewTranslations, unverifyUpdatedTranslations, autotranslate)

        override fun updateLocale(
            projectId: String,
            localeId: String,
            name: String,
            code: String,
            branch: String?,
            default: Boolean?,
            mail: Boolean?,
            rtl: Boolean?,
            sourceLocaleId: String?,
            unverifyNewTranslations: String?,
            unverifyUpdatedTranslations: String?,
            autotranslate: String?
        ): Response = target.updateLocale(projectId, localeId, name, code, branch, default, mail, rtl, sourceLocaleId, unverifyNewTranslations, unverifyUpdatedTranslations, autotranslate)

        override fun deleteLocale(projectId: String, localeId: String): Response = target.deleteLocale(projectId, localeId)


        //TRANSLATION
        override fun translations(projectId: String, localeId: String): Response = target.translations(projectId, localeId)

        override fun createTranslation(projectId: String, localeId: String, keyId: String, content: String): Response = target.createTranslation(projectId, localeId, keyId, content)


        //KEYS
        override fun createKey(
            projectId: String,
            name: String,
            tags: ArrayList<String>?,
            description: String?,
            branch: String?,
            plural: Boolean?,
            namePlural: String?,
            dataType: String?,
            maxCharactersAllowed: Number?,
            screenshot: File?,
            removeScreenshot: Boolean?,
            unformatted: Boolean?,
            xmlSpacePreserve: Boolean?,
            originalFile: String?,
            localizedFormatString: String?,
            localizedFormatKey: String?
        ): Response = target.createKey(projectId, name, tags, description, branch, plural, namePlural, dataType, maxCharactersAllowed, screenshot, removeScreenshot, unformatted,
            xmlSpacePreserve, originalFile, localizedFormatString, localizedFormatKey)

        override fun createKey(projectId: String, name: String, tags: ArrayList<String>?): Response = target.createKey(projectId, name, tags)

        override fun searchKey(projectId: String, localeId: String?, q: String?): Response = target.searchKey(projectId, localeId, q)

        override fun deleteKey(projectId: String, keyId: String): Response = target.deleteKey(projectId, keyId)
    }
}

class PhraseAppApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(httpStatus: Int, message: String?) : super("Code [$httpStatus] : $message")
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}
