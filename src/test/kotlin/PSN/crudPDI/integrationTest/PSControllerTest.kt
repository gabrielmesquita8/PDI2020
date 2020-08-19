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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [CrudPsnApplication::class])
class PSControllerTest {

    @Autowired
    private lateinit var repository: PSRepository
    private lateinit var dump: MutableIterable<PSN4>
    private val PATH: String = "/CrudPDI"


    @BeforeEach
    fun init() {
        dump = repository.findAll()
    }

    @AfterEach
    fun save() {
        repository.saveAll(dump)
    }

    @LocalServerPort
    internal var port: Int = 0

    @Test
    fun `Dado que a operação seja um sucesso é retornado uma lista com todos os jogadores`() {
        val findBD = repository.findAll()
        val findAPI = given()
                .config(
                     config()
                        .encoderConfig(encoderConfig().encodeContentTypeAs("\"application/json\"", ContentType.TEXT))
                )
                .port(port)
                .log().all()
                .contentType("application/json")
                .`when`()
                .get("$PATH/allPlayers")
                .then()
                .log().all()
                .statusCode(OK.value())
                .spec(
                    expect()
                        .header("content-type", `is`(("application/json")))
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


    @Test
    fun `Quando realiza uma operação PATCH para alterar o nome com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("nome" to "Shrek")
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().nome).isNotEqualTo(payload)

        given()
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

    @Test
    fun `Quando realiza uma operação PATCH para alterar o genero com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("genero" to "F")
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().genero).isNotEqualTo(payload)

        given()
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/genero/$id")
            .then()
            .log().all()
            .statusCode(NO_CONTENT.value())

        val validatePlayer = repository.findById(id)
        assertThat(validatePlayer.get().genero).isEqualTo(value["genero"])
        assertThat(validatePlayer.get()).usingRecursiveComparison().ignoringFields("genero").isEqualTo(before.get())
    }

    @Test
    fun `Quando realiza uma operação PATCH para alterar o idTag com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("idtag" to "Superman")
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().idtag).isNotEqualTo(payload)

        given()
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/IdTag/$id")
            .then()
            .log().all()
            .statusCode(NO_CONTENT.value())

        val validatePlayer = repository.findById(id)
        assertThat(validatePlayer.get().idtag).isEqualTo(value["idtag"])
        assertThat(validatePlayer.get()).usingRecursiveComparison().ignoringFields("idtag").isEqualTo(before.get())
    }

    @Test
    fun `Quando realiza uma operação PATCH para alterar o campo jogos com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("jogos" to 8)
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().jogos).isNotEqualTo(payload)

        given()
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/jogos/$id")
            .then()
            .log().all()
            .statusCode(NO_CONTENT.value())

        val validatePlayer = repository.findById(id)
        assertThat(validatePlayer.get().jogos).isEqualTo(value["jogos"])
        assertThat(validatePlayer.get()).usingRecursiveComparison().ignoringFields("jogos").isEqualTo(before.get())
    }

    @Test
    fun `Quando realiza uma operação PATCH para alterar o campo trofeus com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("trofeu" to 808)
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().trofeu).isNotEqualTo(payload)

        given()
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/trofeu/$id")
            .then()
            .log().all()
            .statusCode(NO_CONTENT.value())

        val validatePlayer = repository.findById(id)
        assertThat(validatePlayer.get().trofeu).isEqualTo(value["trofeu"])
        assertThat(validatePlayer.get()).usingRecursiveComparison().ignoringFields("trofeu").isEqualTo(before.get())
    }

    @Test
    fun `Quando realiza uma operação PATCH para alterar o campo avaliação com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("avaliacao" to 8)
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().avaliacao).isNotEqualTo(payload)

        given()
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/avaliacao/$id")
            .then()
            .log().all()
            .statusCode(NO_CONTENT.value())

        val validatePlayer = repository.findById(id)
        assertThat(validatePlayer.get().avaliacao).isEqualTo(value["avaliacao"])
        assertThat(validatePlayer.get()).usingRecursiveComparison().ignoringFields("avaliacao").isEqualTo(before.get())
    }

    @Test
    fun `Quando realiza uma operação de DELETE com dados corretos a operação deve ser executada com sucesso`() {

        val id: Long = 4

        given()
            .port(port)
            .log().all()
            .contentType(APPLICATION_JSON.toString())
            .`when`()
            .delete("$PATH/delPSN/$id")
            .then()
            .log().all()
            .statusCode(OK.value())

        given()
            .config(
                config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs("application/json", ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType("application/json")
            .`when`()
            .get("$PATH/idPlayer/$id")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                    .header("content-type", `is`(("application/json")))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    .body("message", hasItem("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe"))
            )
    }

    @Test
    fun `Dado que seja feito uma busca por um ID inexistente é retornado erro 400`() {
        val id: Long = 18

        given()
            .config(
                config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs("application/json", ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType("application/json")
            .`when`()
            .get("$PATH/idPlayer/$id")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                    .header("content-type", `is`(("application/json")))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    .body("message", hasItem("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe"))
            )
    }

    @Test
    fun `Quando um usuário realiza um POST com dados incorretos a operação deve retornar erro 400`() {
        val player = PSN4(5, "", "", "", 8, 800, 7)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(player)

        given()
            .config(
                 config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs("application/json", ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType("application/json")
            .`when`()
            .body(payload)
            .post("$PATH")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                    .header("content-type", `is`(("application/json")))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    .body("message", hasItem("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe"))
            )
    }

    @Test
    fun `Quando realiza uma operação PATCH para alterar o campo nome com dados incorretos a operação deve ser retornar erro 400`() {
        val value = mapOf("nome" to "")
        val id: Long = 1
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)
        val before = repository.findById(id)
        assertThat(before.get()).isNotEqualTo(value)

        given()
            .config(
                 config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs("application/json", ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType("application/json")
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/nome/$id")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                    .header("content-type", `is`(("application/json")))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    .body("message", hasItem("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe"))
            )

        val after = repository.findById(id)
        assertThat(after).isNotEqualTo(payload)
    }

    @Test
    fun `Quando realiza uma operação PATCH para alterar o campo genero com dados incorretos a operação deve ser retornar erro 400`() {
        val value = mapOf("idtag" to "")
        val id: Long = 1
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)
        val before = repository.findById(id)
        assertThat(before.get()).isNotEqualTo(value)

        given()
            .config(
                config()
                    .encoderConfig(encoderConfig().encodeContentTypeAs("application/json", ContentType.TEXT))
            )
            .port(port)
            .log().all()
            .contentType("application/json")
            .`when`()
            .body(payload)
            .patch("$PATH/PSN/IdTag/$id")
            .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .spec(
                expect()
                    .header("content-type", `is`(("application/json")))
                    .body("timestamp", `is`(not(emptyOrNullString())))
                    .body("status", `is`(equalTo(BAD_REQUEST.value())))
                    .body("message", hasItem("O Id buscado não existe ou não foi possível realizar a operação devido a sintaxe"))
            )

        val after = repository.findById(id)
        assertThat(after).isNotEqualTo(payload)
    }

}