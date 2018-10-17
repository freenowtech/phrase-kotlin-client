package com.mytaxi.apis.phraseapi

import com.google.common.net.MediaType
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.locale.reponse.Message
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocales
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProject
import feign.Response
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhraseApiClientTest {

    private var client: PhraseApi = mock(PhraseApi::class.java)

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should return project when project exist`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val projectName = UUID.randomUUID().toString()
        val expectedProject = PhraseProject(projectId, projectName)
        val projectString = Gson().toJson(expectedProject)

        val headers = mapOf(
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val response = Response.create(
            200,
            "OK",
            headers,
            projectString,
            StandardCharsets.UTF_8
        )

        `when`(client.project(projectId)).thenReturn(response)

        //WHEN
        val actualProject = phraseApiClient.project(projectId)

        //THEN
        assertNotNull(actualProject)
        assertEquals(actualProject, expectedProject)
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
            "etag" to listOf(eTag),
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val responseFirst = Response.create(
            200,
            "OK",
            headers,
            projectString,
            StandardCharsets.UTF_8
        )

        val responseSecond = Response.create(
            304,
            "OK",
            headers,
            projectString,
            StandardCharsets.UTF_8
        )

        //WHEN
        `when`(client.project(projectId)).thenReturn(responseFirst)
        val actualProjectFirst = phraseApiClient.project(projectId)

        //WHEN
        `when`(client.project(projectId)).thenReturn(responseSecond)
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

        val response = Response.create(
            200,
            "OK",
            headers,
            listLocalesString,
            StandardCharsets.UTF_8
        )

        `when`(client.locales(projectId)).thenReturn(response)

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
            "etag" to listOf(eTag),
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val responseFirst = Response.create(
            200,
            "OK",
            headers,
            listLocalesString,
            StandardCharsets.UTF_8
        )

        val responseSecond = Response.create(
            304,
            "OK",
            headers,
            listLocalesString,
            StandardCharsets.UTF_8
        )

        //WHEN
        `when`(client.locales(projectId)).thenReturn(responseFirst)
        val actualLocalesFirst = phraseApiClient.locales(projectId)

        //WHEN
        `when`(client.locales(projectId)).thenReturn(responseSecond)
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

        val response = Response.create(
            200,
            "OK",
            headers,
            messagesString,
            StandardCharsets.UTF_8
        )

        `when`(client.downloadLocale(projectId, localeId, "json")).thenReturn(response)

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
            "etag" to listOf(eTag),
            "content-type" to listOf(MediaType.JSON_UTF_8.toString())
        )

        val responseFirst = Response.create(
            200,
            "OK",
            headers,
            messagesString,
            StandardCharsets.UTF_8
        )

        val responseSecond = Response.create(
            304,
            "OK",
            headers,
            messagesString,
            StandardCharsets.UTF_8
        )

        //WHEN
        `when`(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseFirst)
        val actualLocalesFileFirst = phraseApiClient.downloadLocale(projectId, localeId)

        //WHEN
        `when`(client.downloadLocale(projectId, localeId, "json")).thenReturn(responseSecond)
        val actualLocalesFileSecond = phraseApiClient.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(actualLocalesFileFirst)
        assertNotNull(actualLocalesFileSecond)
        assertEquals(actualLocalesFileFirst, messages)
        assertEquals(actualLocalesFileSecond, messages)
    }
}
