package com.elong.lightboat.download

fun interface Interceptor<INPUT, OUTPUT, CHAIN: Interceptor.Chain<INPUT, OUTPUT>> {

    fun intercept(chain: CHAIN): OUTPUT

    interface Chain<INPUT, OUTPUT> {
        val task: DownloadTask
        fun proceed(input: INPUT): OUTPUT

        interface Factory<INPUT, OUTPUT> {
            fun create(): Chain<INPUT, OUTPUT>
        }
    }
}

