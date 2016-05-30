package intergation

/**
  * User: aloise
  * Date: 30.05.16
  * Time: 13:20
  */

import akka.stream.Materializer
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

class ServicesApiSpec extends PlaySpec with OneServerPerSuite with InitialSetup {

  // Override app if you need an Application with other than
  // default parameters.

  val wsClient = app.injector.instanceOf[WSClient]

  val db = app.injector.instanceOf[DBAccessProvider]

  val address =  s"localhost:$port"
  val apiGateway = s"http://$address/api/"

  var authToken:String = ""


  // setup the DB data
  await( setupInitialData() )

  "server should return a homepage" in {
    val response = await(wsClient.url(s"http://$address/").get())
    response.status mustBe OK
  }

  "authorize the user" in {
    val requestBody = Json.obj( "email" -> "test1@email.com", "password" -> "pass1" )
    val response = await( wsClient.url(s"http://$address/api/users/auth").post(requestBody) )
    val js = Json.parse( response.body )

    val newToken = ( js \ "token" ).asOpt[String]

    response.status mustBe OK
    ( js \ "status" ).as[String] mustBe "ok"
    newToken mustBe defined

    authToken = newToken.getOrElse("")

  }



}