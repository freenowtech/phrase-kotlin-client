package com.mytaxi.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.client.model.PhraseLocale
import com.mytaxi.apis.phraseapi.client.model.PhraseLocales
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.`when` as on
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhraseApiClientLocaleTest {
    private var client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

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

        on(client.locale(projectId, localeId)).thenReturn(response)

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

        on(client.locales(projectId)).thenReturn(response)

        //WHEN
        val actualLocales = phraseApiClient.locales(projectId)

        //THEN
        assertNotNull(actualLocales)
        assertEquals(expectedLocales, actualLocales)
    }
}
