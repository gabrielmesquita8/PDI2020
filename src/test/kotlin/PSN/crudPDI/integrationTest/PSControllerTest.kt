package com.PSN.crudPDI.integrationTest

import PSN.crudPDI.integrationTest.tokenTest
import com.PSN.crudPDI.app.CrudPsnApplication
import com.PSN.crudPDI.model.PSN4
import com.PSN.crudPDI.repository.PSRepository
import io.restassured.RestAssured.expect
import io.restassured.RestAssured.given
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus.*
import org.springframework.test.annotation.DirtiesContext

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CrudPsnApplication::class])
class PSControllerTest : tokenTest() {

    @Autowired
    private lateinit var repository: PSRepository
    private val PATH: String = "/CrudPDI"
    private lateinit var dump: List<PSN4>

    @BeforeEach
    fun setUpEach() {
        dump = repository.findAll() as List<PSN4>
    }

    @AfterEach
    fun cleanUpEach() {
        repository.saveAll(dump)
    }

    @LocalServerPort
    override var port: Int = 0

    @Test
    fun `Dado que a operação seja um sucesso é retornado uma lista com todos os jogadores`() {
        val findBD = repository.findAll()
        val findAPI = given()
                .header(
                        "Authorization",
                        "Bearer $token"
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
                .header(
                        "Authorization",
                        "Bearer $token"
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
                .header(
                        "Authorization",
                        "Bearer $token"
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


    @Test
    fun `Quando realiza uma operação PATCH para alterar o nome com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("nome" to "Shrek")
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().nome).isNotEqualTo(value["nome"])

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
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

    @Test
    fun `Quando realiza uma operação PATCH para alterar o genero com dados corretos a operação deve ser executada com sucesso`() {
        val value = mapOf("genero" to "F")
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().genero).isNotEqualTo(value["genero"])

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
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

        assertThat(before.get().idtag).isNotEqualTo(value["idtag"])

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
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

        assertThat(before.get().jogos).isNotEqualTo(value["jogos"])

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
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

        assertThat(before.get().trofeu).isNotEqualTo(value["trofeu"])

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
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
        val value = mapOf("avaliacao" to 9)
        val id: Long = 1
        val before = repository.findById(id)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(value)

        assertThat(before.get().avaliacao).isNotEqualTo(value["avaliacao"])

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
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

        val id: Long = 2

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
                .port(port)
                .log().all()
                .contentType(APPLICATION_JSON.toString())
                .`when`()
                .delete("$PATH/delPSN/$id")
                .then()
                .log().all()
                .statusCode(OK.value())

        given()
                .header(
                        "Authorization",
                        "Bearer $token"
                )
                .port(port)
                .log().all()
                .contentType("application/json")
                .`when`()
                .get("$PATH/idPlayer/$id")
                .then()
                .log().all()
                .statusCode(NOT_FOUND.value())
                .spec(
                        expect()
                                .header("content-type", `is`(("application/json")))
                                .body("timestamp", `is`(not(emptyOrNullString())))
                                .body("status", `is`(equalTo(NOT_FOUND.value())))
                                .body("message", hasItem("O Id buscado não existe!"))
                )
    }

    @Test
    fun `Dado que seja feito uma busca por um ID inexistente é retornado erro 400`() {
        val id: Long = 18

        given()
                .header(
                    "Authorization",
                    "Bearer $token"
                )
                .port(port)
                .log().all()
                .contentType("application/json")
                .`when`()
                .get("$PATH/idPlayer/$id")
                .then()
                .log().all()
                .statusCode(NOT_FOUND.value())
                .spec(
                        expect()
                                .header("content-type", `is`(("application/json")))
                                .body("timestamp", `is`(not(emptyOrNullString())))
                                .body("status", `is`(equalTo(NOT_FOUND.value())))
                                .body("message", hasItem("O Id buscado não existe!"))
                )
    }

    @Test
    fun `Quando um usuário realiza um POST com dados incorretos a operação deve retornar erro 400`() {
        val player = PSN4(5, "", "", "", 8, 800, 7)
        val payload = com.fasterxml.jackson.databind.ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(player)

        given()
            .header(
                "Authorization",
                "Bearer $token"
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
                    .body("message", hasItem("Ocorreu um erro em sua requisição, verifique sua sintaxe!"))
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
                .header(
                    "Authorization",
                    "Bearer $token"
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
                        .body("message", hasItem("Ocorreu um erro em sua requisição, verifique sua sintaxe!"))
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
            .header(
                "Authorization",
                "Bearer $token"
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
                    .body("message", hasItem("Ocorreu um erro em sua requisição, verifique sua sintaxe!"))
            )

        val after = repository.findById(id)
        assertThat(after).isNotEqualTo(payload)
    }

   //TODO validar se vale a pena realizar testes negativos sobre o token( como fazer um refresh no token após executar cada teste?)

}