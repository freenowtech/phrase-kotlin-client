package com.freenow.apis.phraseapi.client

import com.freenow.apis.phraseapi.client.config.TestConfig
import com.freenow.apis.phraseapi.client.model.PhraseLocale
import com.freenow.apis.phraseapi.client.model.PhraseProject
import com.freenow.apis.phraseapi.client.model.Key
import org.aeonbits.owner.ConfigFactory
import org.junit.Assume.assumeFalse
import org.junit.Test
import kotlin.test.*

@Ignore
class PhraseApiClientTranslationsKeysIntegrationTest {

    private val cfg = ConfigFactory.create(TestConfig::class.java, System.getenv(), System.getProperties())
    private val localeCode = "de-DE"
    private val localeName = "de"
    private val projectName = "TestProject"

    private var phraseApiClient: PhraseApiClient
    private var clientConfig: PhraseApiClientConfig
    private var branch: String
    private var projectId: String
    private var keyId: String
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
        keyId = cfg.keyId()
        branch = cfg.branch()
        localeIdDe = cfg.localeIdDe()
        localeIdDeBranch = cfg.localeIdDeBranch()
    }

    @Test
    fun `Should list translations`() {

        //WHEN
        val masterTranslations = phraseApiClient.translations(
            PhraseProject(projectId, projectName),
            PhraseLocale(localeIdDe, localeName, localeCode)
        )

        val branchTranslations = phraseApiClient.translations(
            PhraseProject(projectId, projectName),
            PhraseLocale(localeIdDeBranch, localeName, localeCode),
            branch
        )

        //THEN
        assertNotNull(branchTranslations)
        assertNotNull(masterTranslations)
        assertNotEquals(branchTranslations[0].id, masterTranslations[0].id)
    }

    @Test
    fun `Should list translations by key`() {

        //WHEN
        val masterTranslations = phraseApiClient.translationsByKey(
            projectId,
            keyId
        )

        val branchTranslations = phraseApiClient.translationsByKey(
            projectId,
            keyId,
            branch
        )

        //THEN
        assertNotNull(branchTranslations)
        assertNotNull(masterTranslations)
        assertNotEquals(branchTranslations[0].id, masterTranslations[0].id)
    }

    @Test
    fun `Should create keys and translations`() {

        //GIVEN
        val masterKey: Key? = phraseApiClient.createKey(projectId, "testKeyForTranslation", null)
        val branchKey: Key? = phraseApiClient.createKey(projectId, "branchTestKeyForTranslation", null, branch)

        //AND
        assertNotNull(masterKey)
        assertNotNull(masterKey.id)
        assertNotNull(branchKey)
        assertNotNull(branchKey.id)

        //WHEN
        val branchTranslation = phraseApiClient.createTranslation(
            projectId, localeIdDeBranch, branchKey.id, "test translation", branch
        )
        val masterTranslation = phraseApiClient.createTranslation(
            projectId, localeIdDe, masterKey.id, "test translation"
        )

        //THEN
        assertNotNull(masterTranslation)
        assertNotNull(masterTranslation.id)
        assertNotNull(branchTranslation)
        assertNotNull(branchTranslation.id)

        //CLEAN UP
        phraseApiClient.deleteKey(projectId, masterKey.id)
        phraseApiClient.deleteKey(projectId, branchKey.id, branch)
    }

    @Test
    fun `Should create and delete keys`() {

        //GIVEN
        val masterKey: Key? = phraseApiClient.createKey(projectId, "testKey", null)
        val branchKey: Key? = phraseApiClient.createKey(projectId, "branchTestKey", null, branch)

        //AND
        assertNotNull(masterKey)
        assertNotNull(masterKey.id)
        assertNotNull(branchKey)
        assertNotNull(branchKey.id)

        //WHEN
        val masterResponse = phraseApiClient.deleteKey(projectId, masterKey.id)
        val branchResponse = phraseApiClient.deleteKey(projectId, branchKey.id, branch)

        //THEN
        assert(masterResponse)
        assert(branchResponse)
    }

    @Test
    fun `Should search for keys`() {

        //WHEN
        val masterKeys = phraseApiClient.searchKey(projectId, localeIdDe, null)
        val masterKeysSize = masterKeys?.size ?: 0

        val branchKeys = phraseApiClient.searchKey(projectId, localeIdDeBranch, null, branch)
        val branchKeysSize = branchKeys?.size ?: 0

        //THEN
        assert(masterKeysSize > 0)
        assert(branchKeysSize > 0)
    }
}
