package com.freenow.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType.JSON_UTF_8
import com.google.gson.Gson
import com.freenow.apis.phraseapi.client.model.CreateKey
import com.freenow.apis.phraseapi.client.model.Key
import com.freenow.apis.phraseapi.client.model.Keys
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito.`when` as on
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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

        val response = Response.create(HttpStatus.SC_CREATED, "OK", headers, keyJSON, StandardCharsets.UTF_8)

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
        assertEquals(actualResponse!!.name, expectedKey.name)
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

        val response = Response.create(HttpStatus.SC_CREATED, "OK", headers, keyJSON, StandardCharsets.UTF_8)

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

        val response = Response.create(HttpStatus.SC_OK, "OK", headers, keysJSON, StandardCharsets.UTF_8)

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

        val response = Response.create(HttpStatus.SC_NO_CONTENT, "OK", headers, "{}", StandardCharsets.UTF_8)

        on(client.deleteKey(projectId, keyId)).thenReturn(response)


        //WHEN
        val actualResponse = phraseApiClient.deleteKey(projectId, keyId)

        //THEN
        assertTrue(actualResponse)
    }

}
