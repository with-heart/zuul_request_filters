package com.lionize.zuul_filters.filters

import com.netflix.zuul.context.RequestContext
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.springframework.mock.web.MockHttpServletRequest

class UriDecryptionFilterTest {
    private val filter = UriDecryptionFilter()
    private val request = MockHttpServletRequest()

    @Before
    fun init() {
        val ctx = RequestContext.getCurrentContext()
        ctx.clear()
        ctx.request = request
    }

    @Test
    fun `basic properties`() {
        assertEquals(false, filter.shouldFilter())
        assertEquals(1, filter.filterOrder())
        assertEquals("pre", filter.filterType())
    }

    @Test
    fun `should filter if encrypted string found in uri`() {
        val encrypted = "ABCDEFG123456"
        request.requestURI = "/endpoint/$encrypted"

        assertEquals(true, filter.shouldFilter())
    }

    @Test
    fun `should replace encrypted string in uri with decrypted value`() {
        val encrypted = "ABCDEFG123456"
        val decrypted = encrypted.reversed()
        val startUri = "/endpoint/$encrypted"

        request.requestURI = startUri
        filter.run()

        val ctx = RequestContext.getCurrentContext()
        val endUri = ctx.request.requestURI

        assertEquals(
                "/endpoint/$decrypted",
                endUri
        )
    }
}
