package com.singaludra.cryptoapp

import app.cash.turbine.test
import com.singaludra.cryptoapp.api.BadRequest
import com.singaludra.cryptoapp.api.BadRequestException
import com.singaludra.cryptoapp.api.Connectivity
import com.singaludra.cryptoapp.api.ConnectivityException
import com.singaludra.cryptoapp.api.HttpClient
import com.singaludra.cryptoapp.api.InvalidData
import com.singaludra.cryptoapp.api.InvalidDataException
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
import java.lang.Exception


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
            //this will check expected is Connectivity and actual was ConnectivityException
            assertEquals(Connectivity::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        //Then
        verify(exactly = 1) {
            httpClient.get()
        }

        confirmVerified(httpClient)
    }

    @Test
    fun testLoadDeliversInvalidDataError() = runBlocking {
        //Given
        every {
            httpClient.get()
        } returns flowOf(InvalidDataException())

        //When
        sut.load().test {
            assertEquals(InvalidData::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        //Then
        verify(exactly = 1) {
            httpClient.get()
        }

        confirmVerified(httpClient)
    }

    @Test
    fun testLoadDeliversBadRequestError() = runBlocking {
        expect(
            httpClient = httpClient,
            sut = sut,
            toCompleteWith = BadRequestException(),
            expectedResult = BadRequest(),
            exactly = 1,
            confirmVerified = httpClient
        )
    }

    private fun expect(
        httpClient: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        toCompleteWith: Exception,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient
    ) = runBlocking{
        //Given
        every {
            httpClient.get()
        } returns flowOf(toCompleteWith)

        //When
        sut.load().test {
            assertEquals(expectedResult::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        //Then
        verify(exactly = exactly) {
            httpClient.get()
        }

        confirmVerified(confirmVerified)
    }
}