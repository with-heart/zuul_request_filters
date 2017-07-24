package com.lionize.zuul_filters.filters

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.stereotype.Component

@Component
class QueryParamsDecryptionFilter : ZuulFilter() {
    private val encryptedStringRegex =
            Regex("(?=.*[A-Z]+)(?=.*\\d+)([A-Z0-9]){12,}")

    override fun run(): Any? {
        val ctx = RequestContext.getCurrentContext()
        val params = ctx.requestQueryParams
        val encrypted = params["encryptedValue"]!![0]
        val decrypted = encrypted.reversed()

        params["encryptedValue"] = listOf(decrypted)

        return null
    }

    override fun shouldFilter(): Boolean {
        val ctx = RequestContext.getCurrentContext()
        val params = ctx.requestQueryParams ?: return false
        val encrypted = params["encryptedValue"] ?: return false
        return encryptedStringRegex.matches(encrypted[0])
    }

    override fun filterOrder() = 1
    override fun filterType() = "pre"
}
