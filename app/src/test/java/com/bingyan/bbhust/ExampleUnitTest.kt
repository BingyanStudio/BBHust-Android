package com.bingyan.bbhust

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun request1(): String {
        // 模拟阻塞操作
        Thread.sleep(1000)
        return "Data from request 1"
    }

    fun request2(data: String): Flow<String> = flow {
        // 模拟异步操作
        delay(1000)
        emit("Data from request 2 using $data")
    }

    @OptIn(FlowPreview::class)
    @Test
    fun main() = runBlocking {
        val result = listOf("A", "B", "C")
            .map { request1() + it }
            .asFlow()
            .flatMapConcat { request2(it) }
            .flowOn(Dispatchers.IO)
            .toList()

        println(result)
    }
}