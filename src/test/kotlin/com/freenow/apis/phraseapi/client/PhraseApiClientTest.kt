package com.freenow.apis.phraseapi.client

import com.freenow.apis.phraseapi.client.model.Message
import com.freenow.apis.phraseapi.client.model.PhraseLocale
import com.freenow.apis.phraseapi.client.model.PhraseLocaleMessages
import com.freenow.apis.phraseapi.client.model.PhraseLocales
import com.freenow.apis.phraseapi.client.model.PhraseProject
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import feign.Request
import feign.Response
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.Charset
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as on

class PhraseApiClientTest {

    private val client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private val request: Request = mock(Request::class.java)

    private val phraseApiClient = PhraseApiClientImpl(client)

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
            .status(200)
            .request(request)
            .headers(headers)
            .body(projectString, Charset.defaultCharset())
            .build()

        val responseSecond = Response.builder()
            .status(304)
            .request(request)
            .headers(headers)
            .body(projectString, Charset.defaultCharset())
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
            .status(200)
            .request(request)
            .headers(headers)
            .body(listLocalesString, Charset.defaultCharset())
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
            .status(200)
            .headers(headers)
            .body(listLocalesString, Charset.defaultCharset())
            .request(request)
            .build()

        val responseSecond = Response.builder()
            .status(304)
            .headers(headers)
            .request(request)
            .body(listLocalesString, Charset.defaultCharset())
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
            .status(200)
            .request(request)
            .headers(headers)
            .body(messagesString, Charset.defaultCharset())
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
            .status(200)
            .request(request)
            .headers(headers)
            .body(messagesString, Charset.defaultCharset())
            .build()

        val responseSecond = Response.builder()
            .status(304)
            .request(request)
            .headers(headers)
            .body(messagesString, Charset.defaultCharset())
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
