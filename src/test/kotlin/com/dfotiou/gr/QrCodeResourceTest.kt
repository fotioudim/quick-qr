package com.dfotiou.gr

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import kotlin.test.Test

@QuarkusTest
@TestHTTPEndpoint(QrCodeResource::class)
class QrCodeResourceTest {

    @Test
    fun testQrDownloadSvgEndpoint() {
        Given {
            port(8888)
            queryParam("text", "https://quarkus.io")
        } When {
            get("/download/svg")
        } Then {
            statusCode(200)
        }
    }

}