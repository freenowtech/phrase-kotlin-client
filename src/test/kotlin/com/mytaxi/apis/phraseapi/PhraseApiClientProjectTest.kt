package com.mytaxi.apis.phraseapi

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.project.reponse.CreatePhraseProject
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProject
import com.mytaxi.apis.phraseapi.project.reponse.UpdatePhraseProject
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PhraseApiClientProjectTest {
    private var client: PhraseApi = Mockito.mock(PhraseApi::class.java)

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
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val response = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            projectString,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.project(projectId)).thenReturn(response)

        //WHEN
        val actualProject = phraseApiClient.project(projectId)

        //THEN
        assertNotNull(actualProject)
        assertEquals(actualProject, expectedProject)
    }

    @Test
    fun `Should return projects when projects exist`() {

        //GIVEN
        val expectedProject1 = PhraseProject(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val expectedProject2 = PhraseProject(UUID.randomUUID().toString(), UUID.randomUUID().toString())


        val expectedProjects = listOf(
            expectedProject1, expectedProject2
        )

        val projectsJSON = Gson().toJson(expectedProjects)

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val response = Response.create(
            HttpStatus.SC_OK,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.projects()).thenReturn(response)

        //WHEN
        val actualProjects = phraseApiClient.projects()

        //THEN
        assertNotNull(actualProjects)
        assertEquals(actualProjects!![0], actualProjects[0])
        assertEquals(actualProjects[1], actualProjects[1])
    }

    @Test
    fun `Should delete project when project exist`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val response = Response.create(
            HttpStatus.SC_NO_CONTENT,
            "OK",
            headers,
            "{}",
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.deleteProject(projectId)).thenReturn(response)

        //WHEN
        val actualResponse = phraseApiClient.deleteProject(projectId)

        //THEN
        assertTrue(actualResponse)
    }

    @Test
    fun `Should create project`() {

        //GIVEN
        val projectName = UUID.randomUUID().toString()

        val createProjectEntity = CreatePhraseProject(
            name = projectName,
            mainFormat = null,
            sharesTranslationMemory = null,
            removeProjectImage = false,
            accountId = null
        )

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val projectsJSON = Gson().toJson(createProjectEntity)

        val response = Response.create(
            HttpStatus.SC_NO_CONTENT,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.createProject(
            name = projectName,
            projectImage = null,
            mainFormat = null,
            sharesTranslationMemory = null,
            removeProjectImage = false,
            accountId = null
        )).thenReturn(response)

        val expectedProject = PhraseProject(
            id = UUID.randomUUID().toString(),
            name = projectName,
            mainFormat = null,
            sharesTranslationMemory = null,
            projectImageUrl = null,
            removeProjectImage = null,
            accountId = null,
            createdAt = null,
            updatedAt = null
        )

        //WHEN
        val actualResponse = phraseApiClient.createProject(createProjectEntity)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse!!.name, expectedProject.name)
    }

    @Test
    fun `Should update project`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val projectName = UUID.randomUUID().toString()

        val updateProjectEntity = UpdatePhraseProject(
            name = projectName,
            mainFormat = null,
            sharesTranslationMemory = null,
            removeProjectImage = false,
            accountId = null
        )

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val projectsJSON = Gson().toJson(mapOf(
            "id" to projectId,
            "name" to projectName
        ))

        val response = Response.create(
            HttpStatus.SC_NO_CONTENT,
            "OK",
            headers,
            projectsJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.updateProject(
            projectId = projectId,
            name = projectName,
            projectImage = null,
            mainFormat = null,
            sharesTranslationMemory = null,
            removeProjectImage = false,
            accountId = null
        )).thenReturn(response)

        val expectedProject = PhraseProject(
            id = projectId,
            name = UUID.randomUUID().toString(),
            mainFormat = null,
            sharesTranslationMemory = null,
            projectImageUrl = null,
            removeProjectImage = null,
            accountId = null,
            createdAt = null,
            updatedAt = null
        )

        //WHEN
        val actualResponse = phraseApiClient.updateProject(projectId, updateProjectEntity)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse!!.id, expectedProject.id)
        assertNotEquals(actualResponse.name, expectedProject.name)
    }
}
