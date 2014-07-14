package hr.element.akka_reproducibles

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import com.typesafe.config.ConfigFactory

import java.util.concurrent.Executors

import scala.concurrent._, duration._

import spray.util._
import spray.http._, MediaTypes._
import spray.routing._
import spray.httpx.unmarshalling._
import spray.httpx.encoding.Gzip

import scala.util._

import actions._

trait ProxyActor extends HttpService {
  protected val serverActor: ActorSelection

  private implicit val timeout = Timeout(30 seconds)

  private implicit val proxyExecutionContext =
    BridgeServices.executionContext

  protected val proxyRoutes = (
    path("server" / "test" / "ok") {
      get {
        complete((serverActor ? TestOK).mapTo[String])
      }
    }
    ~
    path("server" / "test" / "slow") {
      get {
        complete((serverActor ? TestSlow).mapTo[(Int, String)])
      }
    }
    ~
    path("server" / "test" / "timeout") {
      get {
        complete((serverActor ? TestTimeout).mapTo[String])
      }
    }
    ~
    path("server" / "test" / "error") {
      get {
        complete((serverActor ? TestError).mapTo[Try[String]])
      }
    }
    ~
    path("server" / "test" / "echo") {
      post {
        entity(as[String]) { body =>
          detach() {
            complete {
              if (body.getBytes("UTF-8").length > 10000000) {
                Promise.successful(
                  HttpResponse(413, "Message too big!")
                ).future
              }
              else {
                (serverActor ? TestEcho(body)).mapTo[String]
              }
            }
          }
        }
      }
    }
    ~
    path("server" / "process") {
      post {
        entity(as[String]) { body =>
          detach() {
            complete((serverActor ? Process(body)).mapTo[String])
          }
        }
      }
    }
  )

}
