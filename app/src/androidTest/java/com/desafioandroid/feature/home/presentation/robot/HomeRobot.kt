package com.desafioandroid.feature.home.presentation.robot

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.desafioandroid.R
import com.desafioandroid.core.util.enqueue200Response
import com.desafioandroid.core.util.getJson
import com.desafioandroid.feature.home.presentation.view.adapter.HomeAdapter
import okhttp3.mockwebserver.MockWebServer

class HomeRobot(data: HomeRobot.() -> Unit) {

    init {
        data.invoke(this)
    }

    fun performScroll(position: Int) {
        onView(withId(R.id.recycler_home))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
    }

    fun clickItemRecycler(position: Int){
        onView(withId(R.id.recycler_home))
            .perform(RecyclerViewActions.actionOnItemAtPosition<HomeAdapter.ItemViewHolder>(position, click()))
    }

    fun waitForResourcesToLoad(millis: Long){
        Thread.sleep(millis)
    }

    fun mockRegisterSuccessResponse(server: MockWebServer) =
        server.enqueue200Response(getJson("android_jobs.json"))
}