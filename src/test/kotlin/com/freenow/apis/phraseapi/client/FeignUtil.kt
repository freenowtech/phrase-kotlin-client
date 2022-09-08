package com.freenow.apis.phraseapi.client

import feign.Request
import feign.RequestTemplate
import feign.Response
import java.nio.charset.Charset

object FeignUtil {

    fun create(
        status: Int, reason: String, headers: Map<String, List<String>>, body: String?, charset: Charset
    ): Response {

        return Response.builder()
            .status(status)
            .reason(reason)
            .headers(headers)
            .request(Request.create(Request.HttpMethod.GET, "", emptyMap(), null, RequestTemplate()))
            .body(body, charset).build()
    }

    fun create(
        status: Int, reason: String, headers: Map<String, List<String>>, body: ByteArray
    ): Response {
        return Response.builder()
            .status(status)
            .reason(reason)
            .headers(headers)
            .request(Request.create(Request.HttpMethod.GET, "", emptyMap(), null, RequestTemplate()))
            .body(body).build()
    }
}
