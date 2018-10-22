package com.mytaxi.apis.phraseapi.locale.reponse

import java.util.Date

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
