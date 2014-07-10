package hr.element.akka_reproducibles

import akka.actor.{ ActorSystem, Props }
import akka.io.IO

import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec

import spray.servlet.WebBoot
import spray.can.Http

trait ReceiverService {
  val system: ActorSystem

  def spawnActor() =
    system.actorOf(Props[ReceiverActor](new ReceiverActor()), "receiver-actor")
}

class Boot extends WebBoot with ReceiverService {
  val system = ActorSystem("receiver-spray")
  val serviceActor = spawnActor()
}

object EntryPoint extends App with ReceiverService {
  implicit val system = ActorSystem("receiver-spray", ConfigFactory.load("application.conf"))
  IO(Http) ! Http.Bind(spawnActor(), interface = "10.5.100.14", port = 8001)
}


object Receiver extends App {

  sys.exit(0)
}
