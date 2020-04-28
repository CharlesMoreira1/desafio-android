package com.desafioandroid.core.util

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.enqueueResponse(body: String, code: Int) {
    enqueue(MockResponse()
            .setResponseCode(code)
            .setBody(body))
}

fun MockWebServer.enqueue200Response(body: String) {
    enqueueResponse(body, 200)
}
