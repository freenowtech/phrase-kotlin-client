package com.mytaxi.apis.phraseapi.client

import com.mytaxi.apis.phraseapi.client.config.TestConfig
import com.mytaxi.apis.phraseapi.client.model.CreatePhraseLocale
import com.mytaxi.apis.phraseapi.client.model.DownloadPhraseLocale
import org.aeonbits.owner.ConfigFactory
import org.junit.Assume.assumeFalse
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@Ignore
class PhraseApiClientLocalesIntegrationTest {

    private val cfg = ConfigFactory.create(TestConfig::class.java, System.getenv(), System.getProperties())

    private var phraseApiClient: PhraseApiClient
    private var clientConfig: PhraseApiClientConfig
    private var branch: String
    private var downloadLocale: DownloadPhraseLocale
    private var projectId: String
    private var localeIdDe: String
    private var localeIdDeBranch: String

    init {
        //GIVEN
        assumeFalse(System.getenv("TRAVIS")?.let { it.toBoolean() } ?: false)

        assertNotNull(cfg.authToken())
        assertNotNull(cfg.host())

        clientConfig = PhraseApiClientConfig(cfg.host(), cfg.authToken())
        phraseApiClient = PhraseApiClientImpl(clientConfig)
        projectId = cfg.projectId()
        branch = cfg.branch()
        downloadLocale = DownloadPhraseLocale(true, true, null, branch)
        localeIdDe = cfg.localeIdDe()
        localeIdDeBranch = cfg.localeIdDeBranch()
    }


    @Test
    fun `Should list locales`() {

        //WHEN
        val masterLocales = phraseApiClient.locales(projectId)
        val branchLocales = phraseApiClient.locales(projectId, branch)

        //THEN
        assertNotNull(masterLocales)
        assertNotNull(branchLocales)
    }

    @Test
    fun `Should retrieve a locale`() {

        //WHEN
        val masterLocale = phraseApiClient.locale(projectId, localeIdDe)
        val branchLocale = phraseApiClient.locale(projectId, localeIdDeBranch, branch)

        //THEN
        assertNotNull(masterLocale)
        assertNotNull(masterLocale!!.id)
        assertNotNull(branchLocale)
        assertNotNull(branchLocale!!.id)
    }

    @Test
    fun `Should download locale`() {

        //WHEN
        val masterLocaleMessages = phraseApiClient.downloadLocale(projectId, localeIdDe)
        val branchLocaleMessages = phraseApiClient.downloadLocale(projectId, localeIdDeBranch, downloadLocale)

        //THEN
        assertNotNull(branchLocaleMessages)
        assertNotNull(masterLocaleMessages)
    }

    @Test
    fun `Should download locale as properties`() {

        //WHEN
        val masterLocales = phraseApiClient.downloadLocaleAsProperties(projectId, localeIdDe, true)
        val branchLocales = phraseApiClient.downloadLocaleAsProperties(projectId, localeIdDeBranch, true, branch)

        //THEN
        assertNotNull(branchLocales)
        assertNotNull(masterLocales)
    }

    @Test
    fun `Should create and delete locales`() {

        //GIVEN
        val masterLocale = phraseApiClient.createLocale(projectId, CreatePhraseLocale("es", "es-ES"))
        val branchLocale = phraseApiClient.createLocale(projectId, CreatePhraseLocale("es", "es-ES", branch))

        //AND
        assertNotNull(masterLocale)
        assertNotNull(masterLocale!!.id)
        assertNotNull(branchLocale)
        assertNotNull(branchLocale!!.id)

        //WHEN
        phraseApiClient.deleteLocale(projectId, masterLocale.id)
        phraseApiClient.deleteLocale(projectId, branchLocale.id, branch)

        //THEN
        assertFailsWith<PhraseAppApiException> { phraseApiClient.locales(projectId, masterLocale.id) }
        assertFailsWith<PhraseAppApiException> { phraseApiClient.locale(projectId, masterLocale.id, branch) }
    }
}
