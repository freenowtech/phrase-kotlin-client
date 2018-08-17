package com.mytaxi.apis.phraseapi

import com.google.common.cache.CacheBuilder
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocales
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProject
import com.mytaxi.apis.phraseapi.translation.responce.Translations
import feign.Feign
import feign.RequestInterceptor
import feign.Response
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
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

    fun locales(projectId: String): PhraseLocales? {
        val response = client.locales(projectId)
        return processResponse("/v2/projects/$projectId/locales", response)
    }

    fun downloadLocale(projectId: String, localeId: String): PhraseLocaleMessages? {
        val response = client.downloadLocale(projectId, localeId)
        return processResponse("/v2/projects/$projectId/locales/$localeId/download?file_format=json", response)
    }

    fun project(projectId: String): PhraseProject? {
        val response = client.project(projectId)
        return processResponse("/v2/projects/$projectId", response)
    }

    fun translations(project: PhraseProject, locale: PhraseLocale): Translations? {
        val response = client.translations(project.id, locale.id)
        return processResponse("/v2/projects/${project.id}/locales/${locale.id}/translationsn", response)
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
                .encoder(GsonEncoder())
                .target(PhraseApi::class.java, url)
        }

        private fun getInterceptor() = RequestInterceptor {
            apply {
                it.header("If-None-Match", eTagCache.getIfPresent(it.request().url()))
                it.header("Authorization", "token $authKey")
            }
        }

        override fun locales(projectId: String): Response {
            return target.locales(projectId)
        }

        override fun downloadLocale(projectId: String, localeId: String): Response {
            return target.downloadLocale(projectId, localeId)
        }

        override fun project(projectId: String): Response {
            return target.project(projectId)
        }

        override fun translations(projectId: String, localeId: String): Response {
            return target.translations(projectId, localeId)
        }
    }
}

class PhraseAppApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}
