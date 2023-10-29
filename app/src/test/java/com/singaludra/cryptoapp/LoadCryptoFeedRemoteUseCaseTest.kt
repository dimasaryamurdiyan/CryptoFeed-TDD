package com.singaludra.cryptoapp

import com.singaludra.cryptoapp.api.Connectivity
import com.singaludra.cryptoapp.api.HttpClient
import com.singaludra.cryptoapp.api.LoadCryptoFeedRemoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class LoadCryptoFeedRemoteUseCaseTest {
    @Test
    fun testInitDoesNotRequestData(){
        val (_, client) = makeSut()
        LoadCryptoFeedRemoteUseCase(httpClient = client)

        assertTrue(client.getCount == 0)
        
    }

    @Test
    fun testLoadRequestData(){
        //Given
        val (sut, client) = makeSut()

        //When
        sut.load()

        //Then
        assertEquals(1, client.getCount)
    }

    @Test
    fun testLoadTwiceRequestDataTwice(){
        //Given
        val (sut, client) = makeSut()

        //When
        sut.load()
        sut.load()

        //Then
        assertEquals(2, client.getCount)
    }

    @Test
    fun testLoadDeliversErrorOnClientError() = runBlocking {
        val (sut, client) = makeSut()

        val capturedError = arrayListOf<Exception>()

        sut.load().collect{ error ->
            capturedError.add(error)
        }

        client.error = Exception("test")

        assertEquals(listOf(Connectivity::class.java), capturedError.map { it::class.java })
    }

    private fun makeSut(): Pair<LoadCryptoFeedRemoteUseCase, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(httpClient = client)

        return Pair(sut, client)
    }

    private class HttpClientSpy: HttpClient {
        var getCount = 0
        var error: Exception? = null

        override fun get(): Flow<Exception> = flow {
            if (error != null) {
                emit(error ?: Exception())
            }
            getCount += 1
        }
    }
}