package com.mytaxi.apis.phraseapi.client

import com.mytaxi.apis.phraseapi.client.model.*

@Suppress("TooManyFunctions")
interface PhraseApiClient {

    fun projects(): PhraseProjects?

    fun project(projectId: String): PhraseProject?

    fun deleteProject(projectId: String): Boolean

    fun createProject(phraseProject: CreatePhraseProject): PhraseProject?

    fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject?

    fun locales(projectId: String, localeId: String): PhraseLocale?

    fun locales(projectId: String): PhraseLocales?

    fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale?

    fun downloadLocale(projectId: String, localeId: String, downloadLocale: DownloadPhraseLocale? = null): PhraseLocaleMessages?

    fun downloadLocaleAsProperties(projectId: String, localeId: String, escapeSingleQuotes: Boolean): ByteArray?

    fun deleteLocale(projectId: String, localeId: String)

    fun translations(project: PhraseProject, locale: PhraseLocale): Translations?

    fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation?

    fun createTranslation(projectId: String, localeId: String, keyId: String, content: String): Translation?

    fun createKey(project: String, createKey: CreateKey): Key?

    fun createKey(project: String, name: String, tags: ArrayList<String>?): Key?

    fun searchKey(projectId: String, localeId: String?, q: String?): Keys?

    fun deleteKey(projectId: String, keyId: String): Boolean

}
