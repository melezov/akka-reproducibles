package hr.element.akka_reproducibles

import akka.actor._
import akka.io.IO
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.Await
import spray.can.Http
import spray.routing.ExceptionHandler

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object BridgeServices {
  implicit val executionContext = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool()
  )
}

class BridgeServices(val serverActor: ActorSelection)
    extends Actor with BridgeActor with ProxyActor {

  def actorRefFactory = context

  def receive = runRoute(
    bridgeRoutes ~ proxyRoutes
  )

  implicit val exceptionHandler = ExceptionHandler {
    case e: Exception => _.complete((500, e.getMessage))
  }
}

object Bridge extends App {
  import BridgeServices.executionContext

  implicit val system = ActorSystem("bridge", ConfigFactory.load("bridge.conf"))

  val serverActor = system.actorSelection("akka.tcp://server@akka-reproducibles-server:8082/user/server-actor")

  val servicesActor = system.actorOf(Props(classOf[BridgeServices], serverActor), "proxy-actor")
  IO(Http) ! Http.Bind(servicesActor, interface = "akka-reproducibles-bridge", port = 8080)
}
