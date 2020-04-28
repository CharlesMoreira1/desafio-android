package com.desafioandroid.feature.home.presentation.view.activity

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.desafioandroid.core.rule.MockWebServerRule
import com.desafioandroid.feature.home.presentation.robot.HomeRobot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest{

    @get:Rule
    val activityRule = ActivityTestRule(HomeActivity::class.java)

    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    @Test
    fun whenClickingItem_shouldShowAnotherScreen(){
        HomeRobot {
            mockRegisterSuccessResponse(mockWebServerRule.mockWebServer)
            waitForResourcesToLoad(5000)
            clickItemRecycler(1)
        }
    }

    @Test
    fun shouldScrolling(){
        HomeRobot {
            mockRegisterSuccessResponse(mockWebServerRule.mockWebServer)
            waitForResourcesToLoad(5000)
            performScroll(5)
        }
    }
}