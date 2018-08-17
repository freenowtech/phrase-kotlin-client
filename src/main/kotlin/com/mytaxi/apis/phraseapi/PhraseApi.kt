package com.mytaxi.apis.phraseapi

import feign.Param
import feign.RequestLine
import feign.Response

interface PhraseApi {

    @RequestLine("GET /v2/projects/{projectId}/locales")
    fun locales(@Param("projectId") projectId: String): Response

    @RequestLine("GET /v2/projects/{projectId}/locales/{localeId}/download?file_format=json")
    fun downloadLocale(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String
    ): Response

    @RequestLine("GET /v2/projects/{projectId}")
    fun project(@Param("projectId") projectId: String): Response

    @RequestLine("GET /v2/projects/{projectId}/locales/{localeId}/translations")
    fun translations(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String
    ): Response

}
