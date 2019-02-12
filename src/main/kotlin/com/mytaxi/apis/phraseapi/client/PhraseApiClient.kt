package com.mytaxi.apis.phraseapi.client

import com.mytaxi.apis.phraseapi.client.model.CreateKey
import com.mytaxi.apis.phraseapi.client.model.CreatePhraseLocale
import com.mytaxi.apis.phraseapi.client.model.CreatePhraseProject
import com.mytaxi.apis.phraseapi.client.model.CreateTranslation
import com.mytaxi.apis.phraseapi.client.model.Key
import com.mytaxi.apis.phraseapi.client.model.PhraseLocale
import com.mytaxi.apis.phraseapi.client.model.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.client.model.PhraseLocales
import com.mytaxi.apis.phraseapi.client.model.PhraseProject
import com.mytaxi.apis.phraseapi.client.model.PhraseProjects
import com.mytaxi.apis.phraseapi.client.model.Translation
import com.mytaxi.apis.phraseapi.client.model.Translations
import com.mytaxi.apis.phraseapi.client.model.UpdatePhraseProject

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

    fun downloadLocale(projectId: String, localeId: String): PhraseLocaleMessages?

    fun downloadLocaleAsProperties(projectId: String, localeId: String, escapeSingleQuotes: Boolean): ByteArray?

    fun deleteLocale(projectId: String, localeId: String)

    fun translations(project: PhraseProject, locale: PhraseLocale): Translations?

    fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation?

    fun createTranslation(projectId: String, localeId: String, keyId: String, content: String): Translation?

    fun createKey(project: String, createKey: CreateKey): Key?

    fun createKey(project: String, name: String, tags: ArrayList<String>?): Key?

}
