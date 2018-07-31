package play.api.libs

import java.net.URISyntaxException

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Demonstrates inconsistencies in [[StandaloneAhcWSClient.url()]]
  * whose behavior should be defined.
  */
object UrlTest extends App {

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val wsClient = StandaloneAhcWSClient()
  val wsRequest = wsClient.url("http://postman-echo.com/get?pipe=|")
  val wsResponse = Await.result(wsRequest.execute(), 1.minute)
  println("Response body: " + wsResponse.body) // success!

  println("Request Query params: " + wsRequest.queryString) // empty, but the server disagrees.

  try {
    println("URI: " + wsRequest.uri) // throws! and yet, AHC figured it out somehow.
  } catch {
    case t: URISyntaxException => t.printStackTrace(System.out)
  }
  finally {
    wsClient.close()
    materializer.shutdown()
    actorSystem.terminate()
  }
}
