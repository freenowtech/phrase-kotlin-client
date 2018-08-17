package com.mytaxi.apis.phraseapi.project.reponse

import java.io.File
import java.util.Date

data class PhraseProject(
    val id: String,
    val name: String,
    val main_format: String? = null,
    val sharesTranslationMemory: String? = null,
    val project_image_url: String? = null,
    val remove_project_image: Boolean? = null,
    val account_id: String? = null,
    val created_at: Date? = null,
    val updated_at: Date? = null
)

class PhraseProjects : ArrayList<PhraseProject>()

data class CreatePhraseProject(
    val name: String,
    val main_format: String? = null,
    val sharesTranslationMemory: String? = null,
    val project_image: File? = null, //TODO findout how to upload file
    val remove_project_image: Boolean? = null,
    val account_id: String? = null
)

data class UpdatePhraseProject(
    val name: String,
    val main_format: String? = null,
    val sharesTranslationMemory: String? = null,
    val project_image: File? = null, //TODO findout how to upload file
    val remove_project_image: Boolean? = null,
    val account_id: String? = null
)
