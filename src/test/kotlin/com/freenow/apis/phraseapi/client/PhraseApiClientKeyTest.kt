package com.freenow.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType.JSON_UTF_8
import com.google.gson.Gson
import com.isadounikau.phrase.api.client.CacheApi
import com.isadounikau.phrase.api.client.PhraseApi
import com.isadounikau.phrase.api.client.PhraseApiClient
import com.isadounikau.phrase.api.client.PhraseApiClientImpl
import com.isadounikau.phrase.api.client.model.CreateKey
import com.isadounikau.phrase.api.client.model.Key
import com.isadounikau.phrase.api.client.model.Keys
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.mockito.Mockito.`when` as on

class PhraseApiClientKeyTest {
    private var client: PhraseApi = mock(PhraseApi::class.java, withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should create a key with optional parameters`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val keyName = UUID.randomUUID().toString()
        val tags = arrayListOf("abc", "def")

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(JSON_UTF_8.toString())
        )

        val createKey = CreateKey(name = keyName, tags = tags, description = "desc", plural = false)

        val keyJSON = Gson().toJson(createKey)

        val response = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_CREATED)
            .body(keyJSON, StandardCharsets.UTF_8)
            .build()

        on(client.createKey(
            projectId = projectId,
            name = keyName,
            tags = tags,
            description = createKey.description,
            plural = createKey.plural
        )).thenReturn(response)

        val expectedKey = Key(id = UUID.randomUUID().toString(), name = keyName, tags = tags, description = "desc", plural = "false")

        //WHEN
        val actualResponse = phraseApiClient.createKey(projectId, createKey)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse.name, expectedKey.name)
        assertEquals(actualResponse.tags, expectedKey.tags)
    }


    @Test
    fun `Should create a key with only the required parameters`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val keyName = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(JSON_UTF_8.toString())
        )

        val expectedKey = Key(id = UUID.randomUUID().toString(), name = keyName)

        val keyJSON = Gson().toJson(expectedKey)

        val response = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_CREATED)
            .body(keyJSON, StandardCharsets.UTF_8)
            .build()

        on(client.createKey(projectId = projectId, name = keyName)).thenReturn(response)


        //WHEN
        val actualResponse = phraseApiClient.createKey(projectId, keyName, null)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse!!.name, expectedKey.name)
    }


    @Test
    fun `Should search for a key`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val keyName = UUID.randomUUID().toString()
        val q = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(JSON_UTF_8.toString())
        )

        val expectedKey = Key(id = UUID.randomUUID().toString(), name = keyName)
        val keys = Keys()
        keys.add(expectedKey)

        val keysJSON = Gson().toJson(keys)

        val response = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_OK)
            .body(keysJSON, StandardCharsets.UTF_8)
            .build()

        on(client.searchKey(projectId, localeId, q)).thenReturn(response)


        //WHEN
        val actualResponse = phraseApiClient.searchKey(projectId, localeId, q)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse!!.get(0).name, expectedKey.name)
    }


    @Test
    fun `Should delete a key`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val keyId = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(JSON_UTF_8.toString())
        )

        val response = Response.builder()
            .headers(headers)
            .status(HttpStatus.SC_NO_CONTENT)
            .body("{}", StandardCharsets.UTF_8)
            .build()

        on(client.deleteKey(projectId, keyId)).thenReturn(response)


        //WHEN
        val actualResponse = phraseApiClient.deleteKey(projectId, keyId)

        //THEN
        assertTrue(actualResponse)
    }

}
