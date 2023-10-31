package com.singaludra.cryptoapp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

//region infrastructure
interface HttpClient {
    fun get() : Flow<Exception>
}

class ConnectivityException: Exception()
class InvalidDataException: Exception()
class BadRequestException: Exception()
//endregion

class LoadCryptoFeedRemoteUseCase constructor(
    private val httpClient: HttpClient
) {
    fun load(): Flow<Exception> = flow {
        httpClient.get().collect{ error ->
            when(error) {
                is ConnectivityException -> {
                    emit(Connectivity())
                }
                is InvalidDataException -> {
                    emit(InvalidData())
                }
                is BadRequestException -> {
                    emit(BadRequest())
                }
            }
        }
    }
}

class Connectivity: Exception()
//HTTP 422, etc
class InvalidData: Exception()
class BadRequest: Exception()