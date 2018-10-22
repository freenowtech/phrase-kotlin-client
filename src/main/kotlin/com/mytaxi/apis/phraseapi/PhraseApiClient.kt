package com.mytaxi.apis.phraseapi

import com.mytaxi.apis.phraseapi.locale.request.CreatePhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocale
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocaleMessages
import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocales
import com.mytaxi.apis.phraseapi.project.reponse.CreatePhraseProject
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProject
import com.mytaxi.apis.phraseapi.project.reponse.PhraseProjects
import com.mytaxi.apis.phraseapi.project.reponse.UpdatePhraseProject
import com.mytaxi.apis.phraseapi.translation.responce.Translations

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

    fun downloadLocaleAsProperties(projectId: String, localeId: String): ByteArray?

    fun deleteLocale(projectId: String, localeId: String)

    fun translations(project: PhraseProject, locale: PhraseLocale): Translations?

}
