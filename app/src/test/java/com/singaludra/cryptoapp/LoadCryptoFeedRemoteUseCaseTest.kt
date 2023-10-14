package com.singaludra.cryptoapp

import com.singaludra.cryptoapp.api.HttpClient
import com.singaludra.cryptoapp.api.LoadCryptoFeedRemoteUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class LoadCryptoFeedRemoteUseCaseTest {
    @Test
    fun testInitDoesNotLoad(){
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

    private fun makeSut(): Pair<LoadCryptoFeedRemoteUseCase, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(httpClient = client)

        return Pair(sut, client)
    }

    private class HttpClientSpy: HttpClient {
        var getCount = 0

        override fun get() {
            getCount += 1
        }
    }
}