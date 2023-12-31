package com.singaludra.cryptoapp.domain

import kotlinx.coroutines.flow.Flow
import java.lang.Exception

sealed class CryptoFeedResult{
    data class Success(val cryptoFeed: List<CryptoFeed>): CryptoFeedResult()
    data class Error(val exception: Exception): CryptoFeedResult()
}

interface LoadCryptoFeedUseCase {
    fun load(): Flow<CryptoFeedResult>
}