package hr.element.akka_reproducibles

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props }

object Server extends App {
  val system = ActorSystem("server", ConfigFactory.load("server.conf"))

  system.actorOf(Props[ServerActions], "server-actor")
}
