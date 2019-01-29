package com.nes.rgeo.ui

import org.junit.Test

import org.junit.Assert.*

class MainActivityTest {

    @Test
    fun plusNumbers() {
        val activity = MainActivity()

        assertEquals(activity.plusNumbers(2,4), 4)
    }
}