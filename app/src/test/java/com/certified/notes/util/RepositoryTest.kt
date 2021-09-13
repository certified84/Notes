package com.certified.notes.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {

    @Test
    fun insertNote() {
        val  test = TestingPractice()
        val courseCode = "EEE 415"
        val title = "Engineering Maths 3"
        val content = "A very useless course tbh"
        val result = test.insertNote(courseCode, title, content)

        assertThat(result).isEqualTo(true)
    }
}