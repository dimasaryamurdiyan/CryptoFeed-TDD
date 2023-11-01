package com.singaludra.cryptoapp

import app.cash.turbine.test
import com.singaludra.cryptoapp.api.BadRequest
import com.singaludra.cryptoapp.api.BadRequestException
import com.singaludra.cryptoapp.api.Connectivity
import com.singaludra.cryptoapp.api.ConnectivityException
import com.singaludra.cryptoapp.api.HttpClient
import com.singaludra.cryptoapp.api.HttpClientResult
import com.singaludra.cryptoapp.api.InternalServerError
import com.singaludra.cryptoapp.api.InternalServerErrorException
import com.singaludra.cryptoapp.api.InvalidData
import com.singaludra.cryptoapp.api.InvalidDataException
import com.singaludra.cryptoapp.api.LoadCryptoFeedRemoteUseCase
import com.singaludra.cryptoapp.api.LoadCryptoFeedResult
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
        } returns flowOf(HttpClientResult.Failure(Connectivity()))

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
        expect(
            httpClient = httpClient,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(ConnectivityException()),
            expectedResult = Connectivity(),
            exactly = 1,
            confirmVerified = httpClient
        )
    }

    @Test
    fun testLoadDeliversInvalidDataError() = runBlocking {
        expect(
            httpClient = httpClient,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(InvalidDataException()),
            expectedResult = InvalidData(),
            exactly = 1,
            confirmVerified = httpClient
        )
    }

    @Test
    fun testLoadDeliversBadRequestError() = runBlocking {
        expect(
            httpClient = httpClient,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(BadRequestException()),
            expectedResult = BadRequest(),
            exactly = 1,
            confirmVerified = httpClient
        )
    }

    @Test
    fun testLoadDeliversInternalServerError() = runBlocking {
        expect(
            httpClient = httpClient,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(InternalServerErrorException()),
            expectedResult = InternalServerError(),
            exactly = 1,
            confirmVerified = httpClient
        )
    }

    private fun expect(
        httpClient: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult: HttpClientResult,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient
    ) = runBlocking{
        //Given
        every {
            httpClient.get()
        } returns flowOf(receivedHttpClientResult)

        //When
        sut.load().test {
            when(val receivedResult = awaitItem()) {
                is LoadCryptoFeedResult.Failure -> {
                    assertEquals(
                        expectedResult::class.java,
                        receivedResult.exception::class.java
                    )
                }
            }
            awaitComplete()
        }

        //Then
        verify(exactly = exactly) {
            httpClient.get()
        }

        confirmVerified(confirmVerified)
    }
}