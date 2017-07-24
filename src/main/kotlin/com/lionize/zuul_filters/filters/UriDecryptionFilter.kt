package com.lionize.zuul_filters.filters

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequestWrapper

@Component
class UriDecryptionFilter : ZuulFilter() {
    private val encryptedStringRegex =
            Regex("(?=.*[A-Z]+)(?=.*\\d+)([A-Z0-9]){12,}")

    override fun run(): Any? {
        val ctx = RequestContext.getCurrentContext()
        val uri = ctx.request.requestURI

        val encrypted = encryptedStringRegex.find(uri)!!.value
        val decrypted = encrypted.reversed()
        val newUri = encryptedStringRegex.replace(uri, decrypted)

        val newRequest = object : HttpServletRequestWrapper(ctx.request) {
            override fun getRequestURI() = newUri
        }

        ctx.request = newRequest

        return null
    }

    override fun shouldFilter(): Boolean {
        val ctx = RequestContext.getCurrentContext()
        val uri = ctx.request.requestURI

        return encryptedStringRegex.find(uri) != null
    }

    override fun filterOrder() = 1
    override fun filterType() = "pre"
}
