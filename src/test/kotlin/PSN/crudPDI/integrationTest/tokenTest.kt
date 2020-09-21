package PSN.crudPDI.integrationTest

import com.PSN.crudPDI.app.CrudPsnApplication
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [CrudPsnApplication::class])
class tokenTest {

    private val ENDPOINT: String = "/CrudPDI/login"

    @LocalServerPort
    internal var port: Int = 0

    internal var token: String = ""

    @BeforeEach
    fun tokenGen()
    {
        token = given()
                .port(port)
                .log().all()
                .contentType("application/json")
                .`when`()
                .body(mapOf("nome" to "Jon Snow",("idtag" to "Targaryen")) )
                .post(ENDPOINT)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .spec(
                    RestAssured.expect()
                        .header("content-type", Matchers.`is`(("application/json")))
                        .body(Matchers.not(Matchers.emptyOrNullString()))
                )
                .extract()
                .asString()
    }
}