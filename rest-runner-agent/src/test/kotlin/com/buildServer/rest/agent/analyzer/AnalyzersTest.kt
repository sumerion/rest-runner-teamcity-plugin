package com.buildServer.rest.agent.analyzer

import com.github.kittinunf.fuel.core.Response
import jetbrains.buildServer.agent.BuildFinishedStatus
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2016
 */
class AnalyzersTest {

    @Test
    fun testGroovyAnalyzer() {
        val groovyScriptBody = """
        def parser = new groovy.json.JsonSlurper()
        def user = parser.parseText("${'$'}response")
        user.name == 'Dmitry' && headers['Content-Type'][0] == 'application/json'
        """
        val responseData = "{ \"name\": \"Dmitry\" } /* some comment */ "
        val analyzerStatus = GroovyScriptAnalyzer(groovyScriptBody).analyze(Response().apply {
            httpResponseHeaders = mapOf("Content-Type" to listOf("application/json"))
            data = responseData.toByteArray(Charsets.UTF_8)
        })
        assertEquals(BuildFinishedStatus.FINISHED_SUCCESS, analyzerStatus.buildFinishedStatus)
    }

    @Test
    fun testHeadersAnalyzer() {
        val allowedContentTypeHeader = "Content-Type" to "application/json"
        val analyzerStatus = HeadersAnalyzer(mapOf(allowedContentTypeHeader)).analyze(Response().apply { httpResponseHeaders = hashMapOf("Content-Type" to listOf("application/json")) })
        assertEquals(BuildFinishedStatus.FINISHED_SUCCESS, analyzerStatus.buildFinishedStatus)
    }

    @Test
    fun testStatusCodeAnalyzer() {
        val allowedStatusCodes = setOf(200, 201)
        val analyzerStatus = StatusCodeAnalyzer(allowedStatusCodes).analyze(Response().apply { httpStatusCode = 200 })
        assertEquals(BuildFinishedStatus.FINISHED_SUCCESS, analyzerStatus.buildFinishedStatus)
    }
}