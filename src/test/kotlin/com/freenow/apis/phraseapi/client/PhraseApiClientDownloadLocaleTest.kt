package com.freenow.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.freenow.apis.phraseapi.client.model.DownloadPhraseLocale
import com.freenow.apis.phraseapi.client.model.Message
import com.freenow.apis.phraseapi.client.model.PhraseLocaleMessages
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.`when` as on
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PhraseApiClientDownloadLocaleTest {
    private var client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should return locale messages AS Object direct from PhraseApi`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val messageKey = UUID.randomUUID().toString()
        val message = UUID.randomUUID().toString()
        val description = UUID.randomUUID().toString()
        val fallbackLocaleId = UUID.randomUUID().toString()
        val branch = "branch"
        val downloadLocale = DownloadPhraseLocale(
            escapeSingleQuotes = true,
            includeEmptyTranslations = true,
            fallbackLocaleId = fallbackLocaleId,
            branch = branch
        )

        val expectedLocaleMessages = PhraseLocaleMessages()
        expectedLocaleMessages[messageKey] = Message(message, description)

        val projectsJSON = Gson().toJson(expectedLocaleMessages)


        val response = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch)).thenReturn(response)

        //WHEN
        val actualLocaleMessages = phraseApiClient.downloadLocale(projectId, localeId, downloadLocale)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertEquals(expectedLocaleMessages, actualLocaleMessages)
    }

    @Test
    fun `Should return cached locale messages AS Object`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val messageKey = UUID.randomUUID().toString()
        val message = UUID.randomUUID().toString()
        val description = UUID.randomUUID().toString()

        val expectedLocaleMessages = PhraseLocaleMessages()
        expectedLocaleMessages[messageKey] = Message(message, description)

        val projectsJSON = Gson().toJson(expectedLocaleMessages)


        val responseFirst = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseFirst)
        val actualLocaleMessages = phraseApiClient.downloadLocale(projectId, localeId)


        val responseSecond = Response.create(
            HttpStatus.SC_NOT_MODIFIED,
            "OK",
            headers,
            "",
            StandardCharsets.UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseSecond)
        //WHEN
        val actualLocaleMessagesCached = phraseApiClient.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertNotNull(actualLocaleMessagesCached)
        assertEquals(expectedLocaleMessages, actualLocaleMessages)
        assertEquals(expectedLocaleMessages, actualLocaleMessagesCached)
    }


    @Test(expected = PhraseAppApiException::class)
    fun `Should throw PhraseAppApiException exception with Too Many Requests`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val messageKey = UUID.randomUUID().toString()
        val message = UUID.randomUUID().toString()
        val description = UUID.randomUUID().toString()

        val expectedLocaleMessages = PhraseLocaleMessages()
        expectedLocaleMessages[messageKey] = Message(message, description)

        val responseFirst = Response.create(
            429,
            "Not Ok",
            headers,
            "{\"message\":\"Rate limit exceeded\",\"documentation_url\":\"https://developers.phraseapp.com/api/#rate-limit\"}",
            StandardCharsets.UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseFirst)

        //WHEN
        phraseApiClient.downloadLocale(projectId, localeId)
    }

    @Test
    fun `Should return locale messages AS ByteArray direct from PhraseApi`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.OCTET_STREAM.toString())
        )

        val expectedLocaleMessages = "property = value".toByteArray()

        val response = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            expectedLocaleMessages
        )

        on(client.downloadLocale(projectId, localeId, "properties", true)).thenReturn(response)

        //WHEN
        val actualLocaleMessages = phraseApiClient.downloadLocaleAsProperties(projectId, localeId, true)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertTrue(Arrays.equals(expectedLocaleMessages, actualLocaleMessages))
    }

    @Test
    fun `Should return cached locale messages AS ByteArray`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.OCTET_STREAM.toString())
        )

        val expectedLocaleMessages = "property = value".toByteArray()


        val responseFirst = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            expectedLocaleMessages
        )

        on(client.downloadLocale(projectId, localeId, "properties", true)).thenReturn(responseFirst)
        val actualLocaleMessages = phraseApiClient.downloadLocaleAsProperties(projectId, localeId, true)


        val responseSecond = Response.create(
            HttpStatus.SC_NOT_MODIFIED,
            "OK",
            headers,
            "",
            StandardCharsets.UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "properties")).thenReturn(responseSecond)
        //WHEN
        val actualLocaleMessagesCached = phraseApiClient.downloadLocaleAsProperties(projectId, localeId, false)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertNotNull(actualLocaleMessagesCached)
        assertTrue(Arrays.equals(expectedLocaleMessages, actualLocaleMessages))
        assertTrue(Arrays.equals(expectedLocaleMessages, actualLocaleMessagesCached))
    }


    @Test(expected = PhraseAppApiException::class)
    fun `Should throw PhraseAppApiException exception with Too Many Requests for ByteArray`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val eTag = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.OCTET_STREAM.toString())
        )

        val responseFirst = Response.create(
            429,
            "Not Ok",
            headers,
            "{\"message\":\"Rate limit exceeded\",\"documentation_url\":\"https://developers.phraseapp.com/api/#rate-limit\"}",
            StandardCharsets.UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "properties")).thenReturn(responseFirst)
        //WHEN
        phraseApiClient.downloadLocaleAsProperties(projectId, localeId, false)
    }
}
