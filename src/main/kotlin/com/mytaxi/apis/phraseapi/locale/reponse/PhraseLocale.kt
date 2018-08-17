package com.mytaxi.apis.phraseapi.locale.reponse

data class PhraseLocale(
    val id: String,
    val name: String,
    val code: String
)

class PhraseLocales : ArrayList<PhraseLocale>()

class PhraseLocaleMessages : HashMap<String, Message>()

data class Message(
    val message: String,
    val description: String?
)
