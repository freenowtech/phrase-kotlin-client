package com.freenow.apis.phraseapi.client

import com.freenow.apis.phraseapi.client.model.DownloadPhraseLocaleProperties
import com.freenow.apis.phraseapi.client.model.Message
import com.freenow.apis.phraseapi.client.model.PhraseLocaleMessages
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import feign.Response
import org.apache.commons.httpclient.HttpStatus.SC_NOT_MODIFIED
import org.apache.commons.httpclient.HttpStatus.SC_OK
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.mockito.Mockito.`when` as on

@Suppress("MaxLineLength")
class PhraseApiClientDownloadLocaleTest {
    private var client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient = PhraseApiClientImpl(client)

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
        val downloadLocale = DownloadPhraseLocaleProperties(
            escapeSingleQuotes = true,
            includeEmptyTranslations = true,
            fallbackLocaleId = fallbackLocaleId,
            branch = branch
        )

        val expectedLocaleMessages = PhraseLocaleMessages()
        expectedLocaleMessages[messageKey] = Message(message, description)

        val projectsJSON = Gson().toJson(expectedLocaleMessages)


        val response = Response.create(
            SC_OK,
            "OK",
            headers,
            projectsJSON,
            UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch))
            .thenReturn(response)

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
            SC_OK,
            "OK",
            headers,
            projectsJSON,
            UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseFirst)
        val actualLocaleMessages = phraseApiClient.downloadLocale(projectId, localeId)


        val responseSecond = Response.create(
            SC_NOT_MODIFIED,
            "OK",
            headers,
            "",
            UTF_8
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
            UTF_8
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
            SC_OK,
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
            SC_OK,
            "OK",
            headers,
            expectedLocaleMessages
        )

        on(client.downloadLocale(projectId, localeId, "properties", true)).thenReturn(responseFirst)
        val actualLocaleMessages = phraseApiClient.downloadLocaleAsProperties(projectId, localeId, true)


        val responseSecond = Response.create(
            SC_NOT_MODIFIED,
            "OK",
            headers,
            "",
            UTF_8
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
            UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "properties")).thenReturn(responseFirst)
        //WHEN
        phraseApiClient.downloadLocaleAsProperties(projectId, localeId, false)
    }

    @Test
    fun `Should return locale messages by tag`() {

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
        val tags = "tag"
        val downloadLocale = DownloadPhraseLocaleProperties(
            escapeSingleQuotes = true,
            includeEmptyTranslations = true,
            fallbackLocaleId = fallbackLocaleId,
            branch = branch,
            tags = tags
        )

        val expectedLocaleMessages = PhraseLocaleMessages()
        expectedLocaleMessages[messageKey] = Message(message, description)

        val projectsJSON = Gson().toJson(expectedLocaleMessages)

        val response = Response.create(
            SC_OK,
            "OK",
            headers,
            projectsJSON,
            UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch, tags))
            .thenReturn(response)

        //WHEN
        val actualLocaleMessages = phraseApiClient.downloadLocale(projectId, localeId, downloadLocale)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertEquals(expectedLocaleMessages, actualLocaleMessages)
    }

    @Test
    fun `Should return cached locale messages by tag`() {

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
            SC_OK,
            "OK",
            headers,
            projectsJSON,
            UTF_8
        )

        val fallbackLocaleId = UUID.randomUUID().toString()
        val branch = "branch"
        val tags = "tag"
        val downloadLocale = DownloadPhraseLocaleProperties(
            escapeSingleQuotes = true,
            includeEmptyTranslations = true,
            fallbackLocaleId = fallbackLocaleId,
            branch = branch,
            tags = tags
        )

        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch, tags))
            .thenReturn(responseFirst)

        val actualLocaleMessages = phraseApiClient.downloadLocale(projectId, localeId, downloadLocale)

        val responseSecond = Response.create(
            SC_NOT_MODIFIED,
            "OK",
            headers,
            "",
            UTF_8
        )

        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch, tags))
            .thenReturn(responseSecond)
        //WHEN
        val actualLocaleMessagesCached = phraseApiClient.downloadLocale(projectId, localeId, downloadLocale)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertNotNull(actualLocaleMessagesCached)
        assertEquals(expectedLocaleMessages, actualLocaleMessages)
        assertEquals(expectedLocaleMessages, actualLocaleMessagesCached)
    }

    @Test
    fun `Should return cached different locale messages for different tags`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val eTag = UUID.randomUUID().toString()
        val fallbackLocaleId = UUID.randomUUID().toString()
        val branch = "branch"

        val headers = mapOf(
            HttpHeaders.ETAG to listOf(eTag),
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        // AND a response 1
        val tag1 = "tag1"
        val messageKey1 = UUID.randomUUID().toString()
        val expectedLocaleMessages1 = PhraseLocaleMessages()
        expectedLocaleMessages1[messageKey1] = Message(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        val response1 = Response.create(SC_OK, "OK", headers, Gson().toJson(expectedLocaleMessages1), UTF_8)

        // AND a response 2
        val tag2 = "tag2"
        val messageKey2 = UUID.randomUUID().toString()
        val expectedLocaleMessages2 = PhraseLocaleMessages()
        expectedLocaleMessages2[messageKey2] = Message(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        val response2 = Response.create(SC_OK, "OK", headers, Gson().toJson(expectedLocaleMessages2), UTF_8)

        //WHEN
        val downloadLocale1 = DownloadPhraseLocaleProperties(
            escapeSingleQuotes = true,
            includeEmptyTranslations = true,
            fallbackLocaleId = fallbackLocaleId,
            branch = branch,
            tags = tag1
        )
        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch, tag1))
            .thenReturn(response1)

        val downloadLocale2 = DownloadPhraseLocaleProperties(
            escapeSingleQuotes = true,
            includeEmptyTranslations = true,
            fallbackLocaleId = fallbackLocaleId,
            branch = branch,
            tags = tag2
        )
        on(client.downloadLocale(projectId, localeId, "json", true, true, fallbackLocaleId, branch, tag2))
            .thenReturn(response2)

        //WHEN
        val download1 = phraseApiClient.downloadLocale(projectId, localeId, downloadLocale1)
        val download2 = phraseApiClient.downloadLocale(projectId, localeId, downloadLocale2)

        //THEN
        assertNotNull(download1)
        assertNotNull(download2)
        assertNotEquals(download1, download2)
    }
}
