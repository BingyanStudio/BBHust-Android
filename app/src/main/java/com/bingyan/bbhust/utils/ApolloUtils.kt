package com.bingyan.bbhust.utils

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

fun apollo(): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl(kv.getString("remote_url") ?: "https://api.hust.online/bbhust2/api/graphql")
        .addInterceptor(authInterceptor())
        .build()
}

fun authInterceptor(): ApolloInterceptor {
    return object : ApolloInterceptor {
        override fun <D : Operation.Data> intercept(
            request: ApolloRequest<D>,
            chain: ApolloInterceptorChain
        ): Flow<ApolloResponse<D>> {
            val token = kv.token ?: return chain.proceed(request)
            val newRequest = request.newBuilder()
                .addHttpHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }
    }
}


fun <T : Operation.Data> Flow<ApolloResponse<T>>.onSuccess(onSuccess: (T) -> Unit): Flow<ApolloResponse<T>> {
    return onEach {
        println("dataIt:${it.data}")
        val data = it.data
        if (data != null) {
            onSuccess(data)
        }
    }
}

@OptIn(FlowPreview::class)
fun <T : Operation.Data, A, S> Flow<Pair<ApolloResponse<T>, A>>.onSuccessFlatMapConcat(onSuccess: (T, A) -> Flow<S>): Flow<S> {
    return flatMapConcat { pair ->
        val data = pair.first.data
        if (data != null) {
            return@flatMapConcat onSuccess(data, pair.second)
        } else {
            return@flatMapConcat flow { }
        }
    }
}

fun <T : Operation.Data> Flow<ApolloResponse<T>>.defaultErrorHandler(handler: (String) -> Unit): Flow<ApolloResponse<T>> {
    return onEach { response ->
        val errors = response.errors
        if (errors != null) {
            handler(errors.joinToString(separator = "\n") { it.message })
        }
    }
}

fun <T : Operation.Data, A> Flow<Pair<ApolloResponse<T>, A>>.errorHandler(handler: (String) -> Unit): Flow<Pair<ApolloResponse<T>, A>> {
    return onEach { pair ->
        val errors = pair.first.errors
        if (errors != null) {
            handler(errors.joinToString(separator = "\n") { it.message })
        }
    }
}