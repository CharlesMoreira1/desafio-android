package com.desafioandroid.core.util

import androidx.test.platform.app.InstrumentationRegistry

fun getJson(fileName: String) =
    InstrumentationRegistry
        .getInstrumentation().context.assets.open(fileName)
        .bufferedReader()
        .use { it.readText() }

