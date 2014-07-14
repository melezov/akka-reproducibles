package hr.element.akka_reproducibles

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.TypedActor
import akka.actor.TypedProps

import com.typesafe.config.ConfigFactory

import java.util.concurrent.Executors

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import spray.util._
import spray.http._
import spray.http.MediaTypes._
import spray.routing._
import spray.httpx.unmarshalling._
import spray.httpx.encoding.Gzip

trait BridgeActor extends HttpService {
  private implicit val bridgeExecutionContext =
    BridgeServices.executionContext

  val bridgeRoutes = (
    path("bridge" / "test" / "ok") {
      get {
        // do not require detach, return directly
        complete("OK")
      }
    }
    ~
    path("bridge" / "test" / "slow") {
      get {
        detach() {
          complete {
            // timeout set to 5s in bridge-spray.conf
            Thread.sleep(1000)
            HttpResponse(201, "Phew... finished!")
          }
        }
      }
    }
    ~
    path("bridge" / "test" / "timeout") {
      get {
        detach() {
          complete {
            // this should always timeout (500 ISE)
            Thread.sleep(300000)
            "I was too slow ;_;"
          }
        }
      }
    }
    ~
    path("bridge" / "test" / "error") {
      get {
        complete {
          // should throw 500 ISE with the message "You requested..."
          sys.error("You requested an error, so here it is!"): String
        }
      }
    }
    ~
    path("bridge" / "test" / "echo") {
      post {
        entity(as[String]) { body =>
          detach() {
            complete {
              body
            }
          }
        }
      }
    }
    ~
    path("bridge" / "process") {
      post {
        entity(as[String]) { body =>
          detach() {
            complete(processRequest(body))
          }
        }
      }
    }
  )

  def processRequest(body: String) = {
    HttpResponse(200, "Received body of size: " + body.length)
  }
}
