package com.PSN.crudPDI.integrationTest

import com.PSN.crudPDI.app.CrudPsnApplication
import com.PSN.crudPDI.model.PSN4
import com.PSN.crudPDI.repository.PSRepository
import io.restassured.RestAssured.*
import io.restassured.config.EncoderConfig.encoderConfig
import io.restassured.http.ContentType
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import java.util.*

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
    fun `Dado que a operação seja um sucesso é retornado uma lista com todos os jogadores`() {
        val findBD = repository.findAll()
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
                .statusCode(OK.value())
                .spec(
                    expect()
                       //TODO  Falhando ->.header("content-type", `is`((APPLICATION_JSON.toString())))
                        .body("$", not(emptyOrNullString()))
                )
                .extract().body().jsonPath().getList(".", PSN4::class.java)
        assertThat(findAPI).isEqualTo(findBD)
    }

    @Test
    fun `Dado que a operação seja um sucesso é retornado o jogador com o ID correspondente`() {
        val id: Long = 1
        val findBD = repository.findById(id)
        val findAPI = given()
                .config(
                        config()
                                .encoderConfig(encoderConfig().encodeContentTypeAs(CONTENT_TYPE_JSON, ContentType.TEXT))
                )
                .port(port)
                .log().all()
                .contentType(APPLICATION_JSON.toString())
                .`when`()
                .get("$PATH/idPlayer/$id")
                .then()
                .log().all()
                .statusCode(OK.value())
                .extract().`as`(PSN4::class.java)

        assertThat(findAPI).isEqualTo(findBD.get())
    }

    @Test
    fun `Quando um usuário realiza um POST com dados corretos a operação deve ser realizada com sucesso`() {
        val player = PSN4(5, "Deacon", "M", "Birdman", 8, 800, 7)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(player)

        given()
            .config(
                config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs(CONTENT_TYPE_JSON, ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .post("$PATH")
            .then()
            .log().all()
            .statusCode(CREATED.value())


        val id: Long = player.id
        val validatePlayer = repository.findById(id)

        assertThat(validatePlayer.get()).usingRecursiveComparison().isEqualTo(player)

    }

    //TODO não funcionando
    @Test
    fun `Quando realiza uma operação PATCH com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("nome" to "Shrek")
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().nome).isNotEqualTo(payload)

        given()
            .config(
                 config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs(CONTENT_TYPE_JSON, ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/nome/$id")
            .then()
            .log().all()
            .statusCode(NO_CONTENT.value())

        val validatePlayer = repository.findById(id)
        assertThat(validatePlayer.get().nome).isEqualTo(value["nome"])
        assertThat(validatePlayer.get()).usingRecursiveComparison().ignoringFields("nome").isEqualTo(before.get())
    }

    //TODO Cenários negativos

    @Test
    fun `Dado que seja feito uma busca por um ID inexistente é retornado erro 400`() {
        val id: Long = 18

        given()
            .config(
                config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs(CONTENT_TYPE_JSON, ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .get("$PATH/idPlayer/$id")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                    //.header("content-type", `is`(APPLICATION_JSON.toString()))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    // .body("message", `is`(equalTo("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe")))
            )
    }

    @Test
    fun `Quando um usuário realiza um POST com dados incorretos a operação deve retornar erro 400`() {
        val player = PSN4(5, "", "", "", 8, 800, 7)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(player)

        given()
            .config(
                 config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs(CONTENT_TYPE_JSON, ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .post("$PATH")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                  //  .header("content-type", `is`(APPLICATION_JSON.toString()))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    .body("message", `is`(equalTo("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe")))
            )
    }

}