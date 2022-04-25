package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import com.elong.lightboat.download.Interceptor
import kotlin.jvm.Throws

abstract class BaseChain<INPUT, OUTPUT>(
    override val task: DownloadTask,
    private val interceptors: List<Interceptor<INPUT, OUTPUT, Interceptor.Chain<INPUT, OUTPUT>>>
): Interceptor.Chain<INPUT, OUTPUT> {
    private var index = 0

    override fun proceed(input: INPUT): OUTPUT {
        check(interceptors.isNotEmpty() && index < interceptors.size)
        val interceptor = interceptors[index++]
        return interceptor.intercept(this)
    }
}

@Throws(IllegalArgumentException::class)
fun throwInterceptorSetupError(interceptorName: String, suitedChainName: String): Nothing {
    throw IllegalArgumentException("$interceptorName should be used in $suitedChainName")
}