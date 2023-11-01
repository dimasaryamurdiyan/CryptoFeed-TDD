package com.singaludra.cryptoapp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

//region infrastructure
sealed class HttpClientResult {
    data class Failure(val exception: Exception): HttpClientResult()
}
interface HttpClient {
    fun get() : Flow<HttpClientResult>
}

class ConnectivityException: Exception()
class InvalidDataException: Exception()
class BadRequestException: Exception()
class InternalServerErrorException: Exception()
//endregion


sealed class LoadCryptoFeedResult {
    data class Failure(val exception: Exception): LoadCryptoFeedResult()
}

class LoadCryptoFeedRemoteUseCase constructor(
    private val httpClient: HttpClient
) {
    fun load(): Flow<LoadCryptoFeedResult> = flow {
        httpClient.get().collect{ result ->
            when (result) {
                is HttpClientResult.Failure -> {
                    when(result.exception) {
                        is ConnectivityException -> {
                            emit(LoadCryptoFeedResult.Failure(Connectivity()))
                        }
                        is InvalidDataException -> {
                            emit(LoadCryptoFeedResult.Failure(InvalidData()))
                        }
                        is BadRequestException -> {
                            emit(LoadCryptoFeedResult.Failure(BadRequest()))
                        }
                        is InternalServerErrorException -> {
                            emit(LoadCryptoFeedResult.Failure(InternalServerError()))
                        }
                    }
                }
            }
        }
    }
}

class Connectivity: Exception()
//HTTP 422, etc
class InvalidData: Exception()
class BadRequest: Exception()
class InternalServerError: Exception()