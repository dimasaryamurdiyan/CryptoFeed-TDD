package com.singaludra.cryptoapp.api

interface HttpClient {
    fun get()
}

class LoadCryptoFeedRemoteUseCase constructor(
    private val httpClient: HttpClient
) {
    fun load(){
        httpClient.get()
    }
}
