package com.desafioandroid.core.rule

import com.desafioandroid.core.service.ApiClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockWebServerRule : TestWatcher() {

    val mockWebServer: MockWebServer by lazy {
        MockWebServer()
    }

    override fun starting(description: Description?) {
        super.starting(description)
        mockWebServer.start()
        ApiClient.BASER_URL = mockWebServer.url("").toString()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        mockWebServer.shutdown()
    }
}