package com.singaludra.cryptoapp

import app.cash.turbine.test
import com.singaludra.cryptoapp.api.Connectivity
import com.singaludra.cryptoapp.api.ConnectivityException
import com.singaludra.cryptoapp.api.HttpClient
import com.singaludra.cryptoapp.api.LoadCryptoFeedRemoteUseCase
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class LoadCryptoFeedRemoteUseCaseTest {

    private val httpClient = spyk<HttpClient>()
    private lateinit var sut : LoadCryptoFeedRemoteUseCase


    @Before
    fun setUp(){
        MockKAnnotations.init(this, relaxed = true)
        sut = LoadCryptoFeedRemoteUseCase(httpClient)
    }

    @Test
    fun testInitDoesNotRequestData(){
        // verify httpClient does not request
       verify(exactly = 0) {
           httpClient.get()
       }

        confirmVerified(httpClient)
    }

    @Test
    fun testLoadRequestData() = runBlocking {
        //Given
        every {
            httpClient.get()
        } returns flowOf(Connectivity())

        //When
        sut.load().test {
            awaitComplete()
        }

        //Then
        verify(exactly = 1) {
            httpClient.get()
        }

        //verify client has been called
        confirmVerified(httpClient)
    }

    @Test
    fun testLoadTwiceRequestDataTwice() = runBlocking {
        //Given
        every {
            httpClient.get()
        } returns flowOf()

        //When
        sut.load().test {
            awaitComplete()
        }
        sut.load().test {
            awaitComplete()
        }

        //Then
        verify(exactly = 2) {
            httpClient.get()
        }

        confirmVerified(httpClient)
    }


    @Test
    fun testLoadRequestDataResultError() = runBlocking {
        //Given
        every {
            httpClient.get()
        } throws (Connectivity())

        //When
        sut.load().test {
            awaitError()
        }

        //Then
        verify(exactly = 1) {
            httpClient.get()
        }

        //verify client has been called
        confirmVerified(httpClient)
    }

    @Test
    fun testLoadDeliversErrorOnClientError() = runBlocking {
        //Given
        every {
            httpClient.get()
        } returns flowOf(ConnectivityException())

        //When
        sut.load().test{
            assertEquals(Connectivity::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        //Then
        verify(exactly = 1) {
            httpClient.get()
        }

        confirmVerified(httpClient)
    }
}