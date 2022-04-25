package com.elong.lightboat.download.interceptor

import com.elong.lightboat.common.Utils
import com.elong.lightboat.download.*

import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor: ConnectIntecepter {

    override fun intercept(chain: Interceptor.Chain<Request, Response>): Response {
        val connectChain = chain as? ConnectChain
        connectChain?.let {
            val state = it.task.state
            val response = it.proceed(it.request.newBuilder().state(state).build())
            state.contentDisposition = response.header(HTTP_CONTENT_DISPOSITION) ?: ""
            state.contentLocation = response.header(HTTP_CONTENT_LOCATION) ?: ""
            //TODO: NoClassDefFoundError: Failed resolution of: Lcom/elong/lightboat/common/Utils;
//            val mimeType: String? = Utils.normalizeMimeType(response.header(HTTP_CONTENT_TYPE))
//            state.mimeType = mimeType ?: ""
            return response
        }
        throwInterceptorSetupError(javaClass.simpleName, ConnectChain::class.java.simpleName)
    }
}

private fun Request.Builder.state(state: DownloadTask.State) = apply {
    url(state.url)

    //range

}