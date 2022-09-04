package com.jhon.tutorial

import org.junit.*

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    // region Before and After
    @Before
    fun before(){
        println("Executing before")
    }

    @After
    fun after(){
        println("Executing after")
    }
    // endregion

    // region BeforeClass and AfterClass
    companion object{
        @BeforeClass @JvmStatic
        fun beforeClass(){
            println("Executing before class")
        }

        @AfterClass @JvmStatic
        fun afterClass(){
            println("Executing after class")
        }
    }
    // endregion

    @Test
    fun firstTest() {
        println("Executing first test")
    }

    @Test
    fun secondTest() {
        println("Executing second test")
    }
}