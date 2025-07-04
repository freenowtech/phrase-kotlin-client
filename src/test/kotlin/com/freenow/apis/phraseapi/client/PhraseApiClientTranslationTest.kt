package com.freenow.apis.phraseapi.client

import com.freenow.apis.phraseapi.client.model.CreateTranslation
import com.freenow.apis.phraseapi.client.model.PhraseLocale
import com.freenow.apis.phraseapi.client.model.Translation
import com.freenow.apis.phraseapi.client.model.TranslationKey
import com.freenow.apis.phraseapi.client.model.Translations
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import feign.Request
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.Charset
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as on

class PhraseApiClientTranslationTest {
    private val client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private val request: Request = mock(Request::class.java)

    private val phraseApiClient = PhraseApiClientImpl(client)

    @Test
    fun `Should translation with optional parameters`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val keyId = UUID.randomUUID().toString()
        val branch = UUID.randomUUID().toString()

        val translations = Translations()
        translations.add(
            Translation(
                "id01",
                "Test",
                PhraseLocale(UUID.randomUUID().toString(), "en", "English"),
                TranslationKey(UUID.randomUUID().toString(), "translation.key")
            )
        )

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val translationJSON = Gson().toJson(translations)

        val response = Response.builder()
            .status(HttpStatus.SC_OK)
            .headers(headers)
            .request(request)
            .body(translationJSON, Charset.defaultCharset())
            .build()

        on(client.translationsByKey(projectId, keyId, branch)).thenReturn(response)

        //WHEN
        val actualResponse = phraseApiClient.translationsByKey(projectId, keyId, branch)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse.size, 1)
        assertEquals(actualResponse.first().id, "id01")
        assertEquals(actualResponse.first().content, "Test")
        assertEquals(actualResponse.first().locale.name, "en")
        assertEquals(actualResponse.first().key.name, "translation.key")
    }

    @Test
    fun `Should create translation with optional parameters`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val keyId = UUID.randomUUID().toString()
        val keyName = "key.name"
        val translationContent = "translation"

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val createTranslation = CreateTranslation(
            localeId = localeId,
            keyId = keyId,
            content = translationContent,
            unverified = true,
            excluded = false
        )

        val translationJSON = Gson().toJson(createTranslation)

        val response = Response.builder()
            .status(HttpStatus.SC_CREATED)
            .headers(headers)
            .request(request)
            .body(translationJSON, Charset.defaultCharset())
            .build()

        on(
            client.createTranslation(
                projectId = projectId,
                localeId = localeId,
                keyId = keyId,
                content = translationContent
            )
        ).thenReturn(response)

        val expectedTranslation = Translation(
            id = UUID.randomUUID().toString(),
            key = TranslationKey(
                id = keyId,
                name = keyName
            ),
            locale = PhraseLocale(
                id = localeId,
                code = Locale.US.toLanguageTag(),
                name = UUID.randomUUID().toString()
            ),
            content = translationContent
        )

        //WHEN
        val actualResponse = phraseApiClient.createTranslation(projectId, createTranslation)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse.content, expectedTranslation.content)
    }

    @Test
    fun `Should create translation with only the required parameters`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val keyId = UUID.randomUUID().toString()
        val keyName = "key.name"
        val translationContent = "translation"

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val createTranslation = CreateTranslation(
            localeId = localeId,
            keyId = keyId,
            content = translationContent
        )

        val translationJSON = Gson().toJson(createTranslation)

        val response = Response.builder()
            .status(HttpStatus.SC_CREATED)
            .headers(headers)
            .request(request)
            .body(translationJSON, Charset.defaultCharset())
            .build()

        on(
            client.createTranslation(
                projectId = projectId,
                localeId = localeId,
                keyId = keyId,
                content = translationContent
            )
        ).thenReturn(response)

        val expectedTranslation = Translation(
            id = UUID.randomUUID().toString(),
            key = TranslationKey(
                id = keyId,
                name = keyName
            ),
            locale = PhraseLocale(
                id = localeId,
                code = Locale.US.toLanguageTag(),
                name = UUID.randomUUID().toString()
            ),
            content = translationContent
        )

        //WHEN
        val actualResponse = phraseApiClient.createTranslation(projectId, localeId, keyId, translationContent)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse.content, expectedTranslation.content)
    }
}
