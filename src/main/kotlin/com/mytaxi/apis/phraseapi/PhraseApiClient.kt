package com.mytaxi.apis.phraseapi

import com.google.common.cache.CacheBuilder
import com.google.common.net.HttpHeaders
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.locale.reponse.CreatePhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocales
import com.mytaxi.apis.phraseapi.project.reponse.CreatePhraseProject
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProject
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProjects
import com.mytaxi.apis.phraseapi.project.reponse.UpdatePhraseProject
import com.mytaxi.apis.phraseapi.translation.responce.Translations
import feign.Feign
import feign.RequestInterceptor
import feign.Response
import feign.form.FormEncoder
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import java.io.File
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.apache.commons.httpclient.HttpStatus


class PhraseApiClient {

    private var LOG = LoggerFactory.getLogger(PhraseApiClient::class.java.name)

    private val client: PhraseApi
    private val gson = Gson()

    companion object {
        private val eTagCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<String, String>() // key : url, value : eTag
        private val responseCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<String, Any>() // key url, value : Response
    }

    constructor(client: PhraseApi) {
        this.client = client
    }

    constructor(url: String, authKey: String) {
        client = PhraseApiImpl(authKey, url)
    }

    fun projects(): PhraseProjects? {
        val response = client.projects()
        LOG.debug("Get projects")
        return processResponse("GET/api/v2/projects", response)
    }

    fun project(projectId: String): PhraseProject? {
        val response = client.project(projectId)
        LOG.debug("Get project [$projectId]")
        return processResponse("GET/api/v2/projects/$projectId", response)
    }

    fun deleteProject(projectId: String): Boolean {
        LOG.debug("Delete project [$projectId]")
        return client.deleteProject(projectId).status() == HttpStatus.SC_NO_CONTENT
    }

    fun createProject(phraseProject: CreatePhraseProject): PhraseProject? {
        LOG.debug("Create project [$phraseProject]")
        val response = client.createProject(
            phraseProject.name,
            phraseProject.project_image,
            phraseProject.main_format,
            phraseProject.sharesTranslationMemory,
            phraseProject.remove_project_image,
            phraseProject.account_id
        )
        return processResponse("POST/api/v2/projects", response)
    }

    fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject? {
        LOG.debug("Update project [$phraseProject]")
        val response = client.updateProject(
            projectId,
            phraseProject.name,
            phraseProject.project_image,
            phraseProject.main_format,
            phraseProject.sharesTranslationMemory,
            phraseProject.remove_project_image,
            phraseProject.account_id
        )
        return processResponse("PUT/api/v2/projects/$projectId", response)
    }

    fun locales(projectId: String): PhraseLocales? {
        LOG.debug("Get locales for project [$projectId]")
        val response = client.locales(projectId)
        return processResponse("GET/api/v2/projects/$projectId/locales", response)
    }

    fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale? {
        LOG.debug("Create locale [$locale] for project [$projectId]")
        val response =  client.createLocale(projectId, locale)
        return processResponse("POST/api/v2/projects/$projectId/locales", response)
    }

    fun downloadLocale(projectId: String, localeId: String): PhraseLocaleMessages? {
        LOG.debug("Download locale [$localeId] for project [$projectId]")
        val response = client.downloadLocale(projectId, localeId)
        return processResponse("GET/api/v2/projects/$projectId/locales/$localeId/download?file_format=json", response)
    }

    fun translations(project: PhraseProject, locale: PhraseLocale): Translations? {
        LOG.debug("Get translations for locale [${locale.id}] for project [${project.id}]")
        val response = client.translations(project.id, locale.id)
        return processResponse("GET/api/v2/projects/${project.id}/locales/${locale.id}/translationsn", response)
    }

    private fun getETag(key: String, response: Response): String? {
        val eTagHeader = response.headers()
            .entries
            .find { it.key.equals(HttpHeaders.ETAG, true)}
        return eTagHeader?.value?.first()
    }

    private inline fun <reified T> processResponse(key: String, response: Response): T? {
        LOG.debug("Response : status [${response.status()}] \n headers [${response.headers()}]")
        if (response.status() !in HttpStatus.SC_OK..HttpStatus.SC_BAD_REQUEST) {
            val message = response.body().asReader().readText()
            LOG.warn(message)
            throw PhraseAppApiException(message)
        }

        return if (response.status() == HttpStatus.SC_NOT_MODIFIED) {
            val cacheResponse = responseCache.getIfPresent(key) as T
            LOG.debug("Cached response : $cacheResponse")
            cacheResponse
        } else {
            val responseObject: T = getObject(response)
            responseCache.put(key, responseObject)
            val eTag = getETag(key, response)
            if (eTag != null) {
                eTagCache.put(key, eTag)
            }
            responseObject
        }
    }

    private inline fun <reified T> getObject(response: Response): T {
        try {
            val responseObject = gson.fromJson(response.body().asReader(), T::class.java)
            LOG.debug("Response object : $responseObject")
            return responseObject
        } catch (ex: Exception) {
            LOG.error(ex.message)
            throw PhraseAppApiException("Error during parsing response", ex)
        }
    }

    private class PhraseApiImpl(
        val authKey: String,
        url: String
    ) : PhraseApi {

        private val target: PhraseApi

        init {
            target = Feign.builder()
                .requestInterceptor(getInterceptor())
                .decoder(GsonDecoder())
                .encoder(FormEncoder(GsonEncoder()))
                .target(PhraseApi::class.java, url)
        }

        private fun getInterceptor() = RequestInterceptor {
            apply {
                it.header(HttpHeaders.IF_NONE_MATCH, eTagCache.getIfPresent(it.request().method() + it.request().url()))
                it.header(HttpHeaders.AUTHORIZATION, "token $authKey")
            }
        }


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

        override fun downloadLocale(projectId: String, localeId: String): Response = target.downloadLocale(projectId, localeId)

        override fun createLocale(projectId: String, locale: CreatePhraseLocale): Response = target.createLocale(projectId, locale)

        override fun updateLocale(projectId: String, localeId: String, locale: CreatePhraseLocale): Response = target.updateLocale(projectId, localeId, locale)

        override fun deleteLocale(projectId: String, localeId: String): Response = target.deleteLocale(projectId, localeId)


        //TRANSLATION
        override fun translations(projectId: String, localeId: String): Response = target.translations(projectId, localeId)
    }
}

class PhraseAppApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}
