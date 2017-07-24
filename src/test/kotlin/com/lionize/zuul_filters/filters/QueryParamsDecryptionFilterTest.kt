package com.lionize.zuul_filters.filters

import com.netflix.zuul.context.RequestContext
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.springframework.mock.web.MockHttpServletRequest

class QueryParamsDecryptionFilterTest {
    private val filter = QueryParamsDecryptionFilter()
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
    fun `should filter if encryptedValue param containing encrypted string`() {
        val encrypted = "ABCDEFG123456"
        val ctx = RequestContext.getCurrentContext()

        ctx.requestQueryParams = mutableMapOf(
                "encryptedValue" to listOf(encrypted)
        )

        assertEquals(true, filter.shouldFilter())
    }

    @Test
    fun `should replace encrypted string in query param with decrypted value`() {
        val encrypted = "ABCDEFG123456"
        val decrypted = encrypted.reversed()
        val ctx = RequestContext.getCurrentContext()

        ctx.requestQueryParams = mutableMapOf(
                "encryptedValue" to listOf(encrypted)
        )
        filter.run()

        val params = ctx.requestQueryParams
        assertEquals(decrypted, params["encryptedValue"]!![0])
    }
}
