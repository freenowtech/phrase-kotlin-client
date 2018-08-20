package com.mytaxi.apis.phraseapi.locale.reponse

import java.util.Locale

data class PhraseLocale(
    val id: String,
    val name: String,
    val code: String,
    val default: Boolean? = null,
    val mail: Boolean? = null,
    val rtl: Boolean? = null,
    val source_locale_id: String? = null,
    val unverify_new_translations: String? = null,
    val unverify_updated_translations: String? = null,
    val autotranslate: String? = null
)

data class CreatePhraseLocale(
    val name: String,
    val code: Locale,
    val default: Boolean? = null,
    val mail: Boolean? = null,
    val rtl: Boolean? = null,
    val source_locale_id: String? = null,
    val unverify_new_translations: String? = null,
    val unverify_updated_translations: String? = null,
    val autotranslate: String? = null
)


class PhraseLocales : ArrayList<PhraseLocale>()

class PhraseLocaleMessages : HashMap<String, Message>()

data class Message(
    val message: String,
    val description: String?
)
