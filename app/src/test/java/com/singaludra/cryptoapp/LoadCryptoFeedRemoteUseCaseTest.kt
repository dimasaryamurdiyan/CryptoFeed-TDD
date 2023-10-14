package com.singaludra.cryptoapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadCryptoFeedRemoteUseCase constructor(
    private val httpClient: HttpClient
) {
    fun load(){
        httpClient.get()
    }
}

interface HttpClient {
    fun get()

}

class HttpClientSpy: HttpClient {
    var getCount = 0

    override fun get() {
        getCount += 1
    }
}

class LoadCryptoFeedRemoteUseCaseTest {
    @Test
    fun testInitDoesNotLoad(){
        val client = HttpClientSpy()
        LoadCryptoFeedRemoteUseCase(httpClient = client)

        assertTrue(client.getCount == 0)
        
    }

    @Test
    fun testLoadRequestData(){
        //Given
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(httpClient = client)

        //When
        sut.load()

        //Then
        assertEquals(1, client.getCount)
    }
}