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

class BridgeActor extends Actor with HttpService {
  def actorRefFactory = context

  implicit val executionContext = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool()
  )

  def receive = runRoute(
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
        entity(as[Array[Byte]]) { body =>
          detach() {
            complete {
              println("Received body: " + body.length)

              body
            }
          }
        }
      }
    }
    ~
    path("bridge" / "listener") {
      post {
        entity(as[String]) { body =>
          detach() {
            complete(processRequest(body))
          }
        }
      }
    }
  )

  implicit def exceptionHandler = ExceptionHandler {
    case e: Exception => _.complete((500, e.getMessage))
  }

  def processRequest(body: String) = {
    HttpResponse(200, "Received body of size: " + body.length)
  }
}
