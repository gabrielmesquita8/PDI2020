package com.PSN.crudPDI.integrationTest

import com.PSN.crudPDI.app.CrudPsnApplication
import com.PSN.crudPDI.model.PSN4
import com.PSN.crudPDI.repository.PSRepository
import io.restassured.RestAssured.*
import io.restassured.config.EncoderConfig.encoderConfig
import io.restassured.http.ContentType
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [CrudPsnApplication::class])
class PSControllerTest {

    @Autowired
    private lateinit var repository: PSRepository

    companion object {
        private const val CONTENT_TYPE_JSON: String = "{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{\"charset\":\"UTF-8\"},\"qualityValue\":1.0,\"concrete\":true,\"wildcardType\":false,\"wildcardSubtype\":false,\"charset\":\"UTF-8\"}"
        private const val PATH: String = "/CrudPDI"
    }

    @LocalServerPort
    internal var port: Int = 0

    @Test
    fun`Dado que a operação seja um sucesso é retornado uma lista com todos os jogadores`()
    {
        val id: Long = 1
        // val findBD = repository.findById(id)
        val findAPI = given()
                .config(
                        config()
                                .encoderConfig(encoderConfig().encodeContentTypeAs(CONTENT_TYPE_JSON, ContentType.TEXT))
                )
                .port(port)
                .log().all()
                .contentType(APPLICATION_JSON.toString())
                .`when`()
                .get("$PATH/allPlayers")
                .then()
                .log().all()
                .statusCode(200)
                .spec(
                        expect()
                                .header("content-type", `is`(equalToCompressingWhiteSpace(APPLICATION_JSON.toString())))
                                .body("$", not(emptyOrNullString()))
                )
                .extract().`as`(PSN4::class.java)
        //assertThat(findAPI).isEqualTo(findBD.get())
    }

}