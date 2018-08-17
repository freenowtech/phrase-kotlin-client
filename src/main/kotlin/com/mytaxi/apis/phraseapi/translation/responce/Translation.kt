package com.mytaxi.apis.phraseapi.translation.responce

import com.mytaxi.apis.phraseapi.locale.reponse.PhraseLocale

class Translations: ArrayList<Translation>()

data class Translation(
    val id: String,
    val content: String,
    val locale: PhraseLocale,
    val key: TranslationKey
)

data class TranslationKey(
    val id: String,
    val name: String
)
