package com.freenow.apis.phraseapi.client.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class PhraseTagWithStats(
    val name: String,
    @JsonProperty("keys_count") val keysCount: Int,
    @JsonProperty("created_at") val createdAt: Date? = null,
    @JsonProperty("updated_at") val updatedAt: Date? = null,
    val statistics: List<TagWithStatsStatistics>
)

data class TagWithStatsStatistics(
    val locale: LocalePreview
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocalePreview(
    val id: String,
    val name: String,
    val code: String
)