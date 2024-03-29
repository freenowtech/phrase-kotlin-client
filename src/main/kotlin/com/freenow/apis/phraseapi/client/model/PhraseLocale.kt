package com.freenow.apis.phraseapi.client.model

import java.util.Date

data class CreatePhraseLocale(
    val name: String,
    val code: String,
    val branch: String? = null,
    val default: Boolean? = null,
    val mail: Boolean? = null,
    val rtl: Boolean? = null,
    val sourceLocaleId: String? = null,
    val unverifyNewTranslations: String? = null,
    val unverifyUpdatedTranslations: String? = null,
    val autotranslate: String? = null
)

data class DownloadPhraseLocaleProperties(
    val escapeSingleQuotes: Boolean,
    val includeEmptyTranslations: Boolean,
    val fallbackLocaleId: String?,
    val branch: String?,
    val tags: String? = null
)

data class PhraseLocale(
    val id: String,
    val name: String,
    val code: String,
    val default: Boolean? = null,
    val mail: Boolean? = null,
    val rtl: Boolean? = null,
    val sourceLocaleId: String? = null,
    val unverifyNewTranslations: String? = null,
    val unverifyUpdatedTranslations: String? = null,
    val autotranslate: String? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)

class PhraseLocales : ArrayList<PhraseLocale>()

class PhraseLocaleMessages : HashMap<String, Message>()

data class Message(
    val message: String,
    val description: String? = null
)
