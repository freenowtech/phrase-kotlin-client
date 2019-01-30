package com.mytaxi.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.client.model.CreateKey
import com.mytaxi.apis.phraseapi.client.model.Key
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhraseApiClientKeyTest {
    private var client: PhraseApi = Mockito.mock(PhraseApi::class.java, Mockito.withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should create a key`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val keyName = UUID.randomUUID().toString()
        val tags = arrayListOf("abc", "def")

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val createKey = CreateKey(
            name = keyName,
            tags = tags
        )

        val keyJSON = Gson().toJson(createKey)

        val response = Response.create(
            HttpStatus.SC_CREATED,
            "OK",
            headers,
            keyJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.createKey(
            projectId = projectId,
            name = keyName,
            tags = tags
        )).thenReturn(response)

        val expectedKey = Key(
            id = UUID.randomUUID().toString(),
            name = keyName,
            tags = tags
        )

        //WHEN
        val actualResponse = phraseApiClient.createKey(projectId, createKey)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse!!.name, expectedKey.name)
        assertEquals(actualResponse.tags, expectedKey.tags)
    }

}
