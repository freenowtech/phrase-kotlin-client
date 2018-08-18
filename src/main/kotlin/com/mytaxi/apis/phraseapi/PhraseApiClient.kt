package com.mytaxi.apis.phraseapi

import com.google.common.cache.CacheBuilder
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
import com.sun.org.apache.xpath.internal.operations.Bool
import feign.Feign
import feign.Param
import feign.RequestInterceptor
import feign.Response
import feign.form.FormEncoder
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger


class PhraseApiClient {

    private var log = Logger.getLogger(PhraseApiClient::class.java.name)

    private val client: PhraseApi
    private val gson = Gson()

    companion object {
        private val eTagCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<String, String>() // key : url, value : eTag
        private val responseCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<String, Any>() // key eTag, value : Response
    }

    constructor(client: PhraseApi) {
        this.client = client
    }

    constructor(url: String, authKey: String) {
        client = PhraseApiImpl(authKey, url)
    }

    fun projects(): PhraseProjects? {
        val response = client.projects()
        return processResponse("GET/v2/projects", response)
    }

    fun project(projectId: String): PhraseProject? {
        val response = client.project(projectId)
        return processResponse("GET/v2/projects/$projectId", response)
    }

    fun deleteProject(projectId: String): Boolean {
        return client.deleteProject(projectId).status() == 204
    }

    fun createProject(phraseProject: CreatePhraseProject): PhraseProject? {
        val response = client.createProject(
            phraseProject.name,
            phraseProject.project_image,
            phraseProject.main_format,
            phraseProject.sharesTranslationMemory,
            phraseProject.remove_project_image,
            phraseProject.account_id
        )
        return processResponse("POST/v2/projects", response)
    }

    fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject? {
        val response = client.updateProject(
            projectId,
            phraseProject.name,
            phraseProject.project_image,
            phraseProject.main_format,
            phraseProject.sharesTranslationMemory,
            phraseProject.remove_project_image,
            phraseProject.account_id
        )
        return processResponse("PUT/v2/projects/$projectId", response)
    }

    fun locales(projectId: String): PhraseLocales? {
        val response = client.locales(projectId)
        return processResponse("GET/v2/projects/$projectId/locales", response)
    }

    fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale? {
        val response =  client.createLocale(projectId, locale)
        return processResponse("POST/v2/projects/$projectId/locales", response)
    }

    fun downloadLocale(projectId: String, localeId: String): PhraseLocaleMessages? {
        val response = client.downloadLocale(projectId, localeId)
        return processResponse("GET/v2/projects/$projectId/locales/$localeId/download?file_format=json", response)
    }

    fun translations(project: PhraseProject, locale: PhraseLocale): Translations? {
        val response = client.translations(project.id, locale.id)
        return processResponse("GET/v2/projects/${project.id}/locales/${locale.id}/translationsn", response)
    }

    private fun getETag(key: String, response: Response): String? {
        val eTagHeader = response.headers().entries.find { it.key == "etag" }
        val eTag = eTagHeader?.value?.first()
        if (eTag != null) {
            eTagCache.put(key, eTag)
        }
        return eTag
    }

    private inline fun <reified T> processResponse(key: String, response: Response): T? {
        if (response.status() !in 200..400) {
            val message = response.body().asReader().readText()
            log.log(Level.WARNING, message)
            throw PhraseAppApiException(message)
        }

        val eTag = getETag(key, response)
        return if (response.status() == 304) {
            responseCache.getIfPresent(eTag!!) as T
        } else {
            val responseObject: T = getObject(response)
            if (eTag != null) {
                responseCache.put(eTag, responseObject)
            }
            return responseObject
        }
    }

    private inline fun <reified T> getObject(response: Response): T {
        try {
            return gson.fromJson(response.body().asReader(), T::class.java)
        } catch (ex: Exception) {
            log.log(Level.WARNING, ex.message)
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
                it.header("If-None-Match", eTagCache.getIfPresent(it.request().method() + it.request().url()))
                it.header("Authorization", "token $authKey")
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
