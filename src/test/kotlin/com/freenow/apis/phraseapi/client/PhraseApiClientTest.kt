package com.freenow.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.isadounikau.phrase.api.client.CacheApi
import com.isadounikau.phrase.api.client.PhraseApi
import com.isadounikau.phrase.api.client.PhraseApiClient
import com.isadounikau.phrase.api.client.PhraseApiClientImpl
import com.isadounikau.phrase.api.client.model.Message
import com.isadounikau.phrase.api.client.model.PhraseLocale
import com.isadounikau.phrase.api.client.model.PhraseLocaleMessages
import com.isadounikau.phrase.api.client.model.PhraseLocales
import com.isadounikau.phrase.api.client.model.PhraseProject
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as on

class PhraseApiClientTest {

    private var client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should return project locales from cache when locale exist and bean already called`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val projectName = UUID.randomUUID().toString()
        val expectedProject = PhraseProject(projectId, projectName)
        val projectString = Gson().toJson(expectedProject)

        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val responseFirst = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_OK)
            .body(projectString, StandardCharsets.UTF_8)
            .build()

        val responseSecond = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_NOT_MODIFIED)
            .body(projectString, StandardCharsets.UTF_8)
            .build()

        //WHEN
        on(client.project(projectId)).thenReturn(responseFirst)
        val actualProjectFirst = phraseApiClient.project(projectId)

        //WHEN
        on(client.project(projectId)).thenReturn(responseSecond)
        val actualProjectSecond = phraseApiClient.project(projectId)

        //THEN
        assertNotNull(actualProjectFirst)
        assertNotNull(actualProjectSecond)
        assertEquals(actualProjectFirst, expectedProject)
        assertEquals(actualProjectSecond, expectedProject)
        assertEquals(actualProjectFirst, actualProjectSecond)
    }

    @Test
    fun `Should return project locales when project locales exist`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val localeName = UUID.randomUUID().toString()
        val localeCode = Locale.CANADA.toLanguageTag()

        val listLocales = PhraseLocales()
        listLocales.add(PhraseLocale(localeId, localeName, localeCode))

        val listLocalesString = Gson().toJson(listLocales)
        val headers = mapOf(
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val response = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_OK)
            .body(listLocalesString, StandardCharsets.UTF_8)
            .build()

        on(client.locales(projectId)).thenReturn(response)

        //WHEN
        val projectLocales = phraseApiClient.locales(projectId)

        //THEN
        assertNotNull(projectLocales)
        assertEquals(listLocales, projectLocales)
    }

    @Test
    fun `Should return project from cache when project exist and bean already called`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val localeName = UUID.randomUUID().toString()
        val localeCode = Locale.CANADA.toLanguageTag()

        val listLocales = PhraseLocales()
        listLocales.add(PhraseLocale(localeId, localeName, localeCode))

        val listLocalesString = Gson().toJson(listLocales)

        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val responseFirst = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_OK)
            .body(listLocalesString, StandardCharsets.UTF_8)
            .build()

        val responseSecond = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_NOT_MODIFIED)
            .body(listLocalesString, StandardCharsets.UTF_8)
            .build()

        //WHEN
        on(client.locales(projectId)).thenReturn(responseFirst)
        val actualLocalesFirst = phraseApiClient.locales(projectId)

        //WHEN
        on(client.locales(projectId)).thenReturn(responseSecond)
        val actualLocalesSecond = phraseApiClient.locales(projectId)

        //THEN
        assertNotNull(actualLocalesFirst)
        assertNotNull(actualLocalesSecond)
        assertEquals(actualLocalesFirst, listLocales)
        assertEquals(actualLocalesSecond, listLocales)
    }

    @Test
    fun `Should return project locales file when project locales file exist`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val messageKey = UUID.randomUUID().toString()
        val message = UUID.randomUUID().toString()

        val messages = PhraseLocaleMessages()
        messages[messageKey] = Message(message, null)

        val messagesString = Gson().toJson(messages)
        val headers = mapOf("content-type" to listOf(MediaType.JSON_UTF_8.toString()))

        val response = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_OK)
            .body(messagesString, StandardCharsets.UTF_8)
            .build()

        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(response)

        //WHEN
        val actualMessages = phraseApiClient.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(actualMessages)
        assertEquals(messages, actualMessages)
    }

    @Test
    fun `Should return project locales file from cache when project file and bean already called`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val messageKey = UUID.randomUUID().toString()
        val message = UUID.randomUUID().toString()

        val messages = PhraseLocaleMessages()
        messages[messageKey] = Message(message, null)

        val messagesString = Gson().toJson(messages)

        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val responseFirst = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_OK)
            .body(messagesString, StandardCharsets.UTF_8)
            .build()

        val responseSecond = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_NOT_MODIFIED)
            .body(messagesString, StandardCharsets.UTF_8)
            .build()

        //WHEN
        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseFirst)
        val actualLocalesFileFirst = phraseApiClient.downloadLocale(projectId, localeId)

        //WHEN
        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseSecond)
        val actualLocalesFileSecond = phraseApiClient.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(actualLocalesFileFirst)
        assertNotNull(actualLocalesFileSecond)
        assertEquals(actualLocalesFileFirst, messages)
        assertEquals(actualLocalesFileSecond, messages)
    }
}
