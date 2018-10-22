package com.mytaxi.apis.phraseapi

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.locale.reponse.Message
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocales
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PhraseApiClientLocaleTest {
    private var client: PhraseApi = Mockito.mock(PhraseApi::class.java)

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should return locale`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val expectedLocale = PhraseLocale(
            id = localeId,
            code = Locale.US.toLanguageTag(),
            name = UUID.randomUUID().toString()
        )

        val projectsJSON = Gson().toJson(expectedLocale)

        val response = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.locale(projectId, localeId)).thenReturn(response)

        //WHEN
        val actualLocale = phraseApiClient.locales(projectId, localeId)

        //THEN
        assertNotNull(actualLocale)
        assertEquals(expectedLocale, actualLocale)
    }

    @Test
    fun `Should return locales`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val expectedLocales = PhraseLocales()
        expectedLocales.add(
            PhraseLocale(
                id = UUID.randomUUID().toString(),
                code = Locale.US.toLanguageTag(),
                name = UUID.randomUUID().toString()
            )
        )
        expectedLocales.add(
            PhraseLocale(
                id = UUID.randomUUID().toString(),
                code = Locale.US.toLanguageTag(),
                name = UUID.randomUUID().toString()
            )
        )

        val projectsJSON = Gson().toJson(expectedLocales)

        val response = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.locales(projectId)).thenReturn(response)

        //WHEN
        val actualLocales = phraseApiClient.locales(projectId)

        //THEN
        assertNotNull(actualLocales)
        assertEquals(expectedLocales, actualLocales)
    }
}
