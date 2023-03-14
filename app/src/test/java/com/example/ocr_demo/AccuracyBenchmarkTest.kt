package com.example.ocr_demo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AccuracyBenchmarkTest {

    private val accuracyBenchMarker = AccuracyBenchMarker()
    @Test
    fun minDistance_1() {
        val result = accuracyBenchMarker.minimumTransformOperation("horse", "ros")
        assertEquals(3, result.minTransform)
        assertEquals(2, result.numOfCorrectChar)
    }

    @Test
    fun minDistance_2() {
        val result = accuracyBenchMarker.minimumTransformOperation("intention", "execution")
        assertEquals(5, result.minTransform)
        assertEquals(5, result.numOfCorrectChar)
    }

    @Test
    fun minDistance_3() {
        val result = accuracyBenchMarker.minimumTransformOperation("61g375Z8", "619375128")
        assertEquals(3, result.minTransform)
        assertEquals(6, result.numOfCorrectChar)
    }

    @Test
    fun minDistance_4() {
        val result = accuracyBenchMarker.minimumTransformOperation("", "a")
        assertEquals(1, result.minTransform)
        assertEquals(0, result.numOfCorrectChar)
    }

    @Test
    fun minDistance_5() {
        val result = accuracyBenchMarker.minimumTransformOperation("steam", "team")
        assertEquals(1, result.minTransform)
        assertEquals(4, result.numOfCorrectChar)
    }

    @Test
    fun minDistance_6() {
        val result = accuracyBenchMarker.minimumTransformOperation("mart", "karma")
        assertEquals(3, result.minTransform)
        assertEquals(2, result.numOfCorrectChar)
    }

    // execution
    // excuetion
    @Test
    fun minDistance_7() {
        val result = accuracyBenchMarker.minimumTransformOperation("excuetion", "execution")
        assertEquals(2, result.minTransform)
        assertEquals(8, result.numOfCorrectChar)
    }

    @Test
    fun testFormat() {
        val formatted = "abc\nxyz\tki\n".formatText()
        assertEquals("abc xyz ki", formatted)
    }
}