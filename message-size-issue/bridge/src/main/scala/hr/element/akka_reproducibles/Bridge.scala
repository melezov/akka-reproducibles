package hr.element.akka_reproducibles

import akka.actor.{ ActorSystem, Props }
import akka.io.IO

import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec

import spray.servlet.WebBoot
import spray.can.Http

object Bridge extends App {
  implicit val system = ActorSystem("bridge-spray", ConfigFactory.load("bridge-spray.conf"))

val bridgeActor = system.actorOf(Props[BridgeActor](new BridgeActor()), "bridge-actor")
  IO(Http) ! Http.Bind(bridgeActor, interface = "10.5.100.14", port = 8080)

  println("Press [enter] to kill Bridge")
  System.in.read()

  // termination, full of grace
  Runtime.getRuntime.halt(0)
}
