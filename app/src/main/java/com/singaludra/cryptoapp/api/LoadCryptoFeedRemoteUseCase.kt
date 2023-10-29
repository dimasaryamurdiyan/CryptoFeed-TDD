package com.singaludra.cryptoapp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


interface HttpClient {
    fun get() : Flow<Exception>
}

class ConnectivityException: Exception()

class LoadCryptoFeedRemoteUseCase constructor(
    private val httpClient: HttpClient
) {
    fun load(): Flow<Exception> = flow {
        httpClient.get().collect{ error ->
            emit(Connectivity())
        }
    }
}

class Connectivity: Exception()
