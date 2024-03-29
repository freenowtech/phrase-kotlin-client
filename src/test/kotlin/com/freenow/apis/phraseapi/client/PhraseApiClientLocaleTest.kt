package com.freenow.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.freenow.apis.phraseapi.client.model.PhraseLocale
import com.freenow.apis.phraseapi.client.model.PhraseLocales
import feign.Request
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.`when` as on
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhraseApiClientLocaleTest {
    private val client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private val request: Request = mock(Request::class.java)

    private val phraseApiClient = PhraseApiClientImpl(client)

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

        val response = Response.builder()
            .status(HttpStatus.SC_OK)
            .request(request)
            .headers(headers)
            .body(projectsJSON, Charset.defaultCharset())
            .build()

        on(client.locale(projectId, localeId)).thenReturn(response)

        //WHEN
        val actualLocale = phraseApiClient.locale(projectId, localeId)

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

        val response = Response.builder()
            .status(HttpStatus.SC_OK)
            .request(request)
            .headers(headers)
            .body(projectsJSON, Charset.defaultCharset())
            .build()

        on(client.locales(projectId)).thenReturn(response)

        //WHEN
        val actualLocales = phraseApiClient.locales(projectId)

        //THEN
        assertNotNull(actualLocales)
        assertEquals(expectedLocales, actualLocales)
    }
}
