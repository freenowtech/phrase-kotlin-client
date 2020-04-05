package com.freenow.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.isadounikau.phrase.api.client.CacheApi
import com.isadounikau.phrase.api.client.PhraseApi
import com.isadounikau.phrase.api.client.PhraseApiClient
import com.isadounikau.phrase.api.client.PhraseApiClientImpl
import com.isadounikau.phrase.api.client.model.CreateTranslation
import com.isadounikau.phrase.api.client.model.PhraseLocale
import com.isadounikau.phrase.api.client.model.Translation
import com.isadounikau.phrase.api.client.model.TranslationKey
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

class PhraseApiClientTranslationTest {
    private var client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
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
            .headers(headers)
            .status(HttpStatus.SC_CREATED)
            .body(translationJSON, StandardCharsets.UTF_8)
            .build()

        on(client.createTranslation(
            projectId = projectId,
            localeId = localeId,
            keyId = keyId,
            content = translationContent
        )).thenReturn(response)

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
            .headers(headers)
            .status(HttpStatus.SC_CREATED)
            .body(translationJSON, StandardCharsets.UTF_8)
            .build()

        on(client.createTranslation(
            projectId = projectId,
            localeId = localeId,
            keyId = keyId,
            content = translationContent
        )).thenReturn(response)

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
        assertEquals(actualResponse!!.content, expectedTranslation.content)
    }

}
