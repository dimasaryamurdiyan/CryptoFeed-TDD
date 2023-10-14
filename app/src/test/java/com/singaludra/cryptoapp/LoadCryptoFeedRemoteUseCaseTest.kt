package com.singaludra.cryptoapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadCryptoFeedRemoteUseCase {
    fun load(){
        HttpClient.instance.getCount = 1
    }
}

class HttpClient private  constructor() {
    companion object {
        var instance = HttpClient()
    }

    var getCount = 0
}
class LoadCryptoFeedRemoteUseCaseTest {
    @Test
    fun testInitDoesNotLoad(){
        val client = HttpClient.instance
        LoadCryptoFeedRemoteUseCase()

        assertTrue(client.getCount == 0)
        
    }

    @Test
    fun testLoadRequestData(){
        //Given
        val client = HttpClient.instance
        val sut = LoadCryptoFeedRemoteUseCase()

        //When
        sut.load()

        //Then
        assertEquals(1, client.getCount)
    }
}