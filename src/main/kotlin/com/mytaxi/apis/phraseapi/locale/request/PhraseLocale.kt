package com.mytaxi.apis.phraseapi.locale.request

data class CreatePhraseLocale(
    val name: String,
    val code: String,
    val default: Boolean? = null,
    val mail: Boolean? = null,
    val rtl: Boolean? = null,
    val sourceLocaleId: String? = null,
    val unverifyNewTranslations: String? = null,
    val unverifyUpdatedTranslations: String? = null,
    val autotranslate: String? = null
)
