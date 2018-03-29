package com.github.bassaer.todomvp.util

/**
 * Contains a static reference to [IdlingResource], only available in the 'mock' build type.
 * Created by nakayama on 2018/03/24.
 */
object EspressoIdlingResource {
    private val RESOURCE = "GLOBAL"
    val countingIdlingResource = SimpleCountingIdlingResource(RESOURCE)

    fun inclement() {
        countingIdlingResource.increment()
    }

    fun declement() {
        countingIdlingResource.decrement()
    }
}