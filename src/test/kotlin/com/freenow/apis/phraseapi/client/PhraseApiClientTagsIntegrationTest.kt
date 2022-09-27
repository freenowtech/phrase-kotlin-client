package com.freenow.apis.phraseapi.client

import com.freenow.apis.phraseapi.client.config.TestConfig
import com.freenow.apis.phraseapi.client.model.DownloadPhraseLocaleProperties
import org.aeonbits.owner.ConfigFactory
import org.junit.Assume.assumeFalse
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Ignore
class PhraseApiClientTagsIntegrationTest {

    private val cfg = ConfigFactory.create(TestConfig::class.java, System.getProperties(), System.getenv())

    private var phraseApiClient: PhraseApiClient
    private var clientConfig: PhraseApiClientConfig
    private var branch: String
    private var properties: DownloadPhraseLocaleProperties
    private var projectId: String
    private var localeIdDe: String
    private var localeIdDeBranch: String
    private var tagName: String

    init {
        //GIVEN
        assumeFalse(System.getenv("TRAVIS")?.toBoolean() ?: false)

        assertNotNull(cfg.authToken())
        assertNotNull(cfg.host())

        clientConfig = PhraseApiClientConfig(authKey = cfg.authToken())
        phraseApiClient = PhraseApiClientImpl(clientConfig)
        projectId = cfg.projectId()
        branch = cfg.branch()
        properties = DownloadPhraseLocaleProperties(true, true, null, branch, tags = cfg.tagName())
        localeIdDe = cfg.localeIdDe()
        localeIdDeBranch = cfg.localeIdDeBranch()
        tagName = cfg.tagName()
    }


    @Test
    fun `Should a single tag and download locale by tag equals`() {
        //WHEN
        val tag = phraseApiClient.getSingleTag(projectId, tagName)
        val messages = phraseApiClient.downloadLocale(projectId, localeIdDe, properties)

        //THEN
        assertNotNull(messages)
        assertNotNull(tag)
        assertEquals(tag.keysCount, messages.size)
    }

}
