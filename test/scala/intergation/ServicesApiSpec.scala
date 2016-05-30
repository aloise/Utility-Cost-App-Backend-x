package intergation

/**
  * User: aloise
  * Date: 30.05.16
  * Time: 13:20
  */

import akka.stream.Materializer
import controllers.helpers.AuthAction
import models.base.DBAccessProvider
import org.scalatestplus.play._
import play.api.cache.EhCacheModule
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws._
import play.api.mvc._
import play.api.test._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.libs.functional.syntax._

class ServicesApiSpec extends PlaySpec with InitialSetup {

  "Service Api" must {


    var authToken:String = ""

    "authorize the user" in {
      val requestBody = Json.obj("email" -> "test1@email.com", "password" -> "pass1")
      val response = await(wsClient.url(s"$apiGateway/users/auth").post(requestBody))
      val js = Json.parse(response.body)

      val newToken = (js \ "token").asOpt[String]

      response.status mustBe OK
      (js \ "status").as[String] mustBe "ok"
      newToken mustBe defined

      authToken = newToken.getOrElse("")

    }

    "have the service access" in {
      val response = await( wsClient.url( s"$apiGateway/services/1/access" ).withHeaders( authHeaders(authToken):_* ).get() )

      response.status mustBe OK

      val js = Json.parse(response.body)

      ( js \ "service" \ "access" ).as[Boolean] mustBe true

    }

    "return a service by id" in {
      val response = await( wsClient.url( s"$apiGateway/services/1" ).withHeaders( authHeaders(authToken):_* ).get() )
      val js = Json.parse(response.body)

      response.status mustBe OK
      ( js \ "service" \ "id").as[Int] mustBe 1

    }


  }





}
