package hr.element.akka_reproducibles

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.TypedActor
import akka.actor.TypedProps

import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

import spray.util._
import spray.http._
import spray.http.MediaTypes._
import spray.routing._
import spray.httpx.unmarshalling._
import spray.httpx.encoding.Gzip

class ReceiverActor() extends Actor with HttpService {
  def actorRefFactory = context

  def receive = runRoute(route)

  implicit def myExceptionHandler =
    ExceptionHandler {
      case e: Exception => ctx =>
        ctx.complete((400, e.getMessage))
    }

  private lazy val system = ActorSystem("calculator-spray", ConfigFactory.load("calculator-spray.conf"))

  private lazy val calculator =
    TypedActor(system).typedActorOf(
      TypedProps[ICalculator].copy(timeout = Some(300000 millis))
    , system.actorFor("akka.tcp://ip-calculator@santiago.instantor.com:8401/user/calc0")
    )

  private implicit val executionContext = scala.concurrent.ExecutionContext.fromExecutor(
    java.util.concurrent.Executors.newCachedThreadPool()
  )

  val route = {
    path("calculator-push" / "Domain.svc" / "submit" / "CalculatorEvents.process") {
      post {
        entity(as[String]) { body =>
          detach() {
            complete(processRequest(body))
          }
        }
      }
    } ~
    path("calculator-push" / "ping") {
      (get | post) {
        complete(doPong())
      }
    }
  }

  def processRequest(body: String) = {
/*
    val jsonSerialization   = locator.resolve(classOf[JsonSerialization])
    logger.info("Deserialising request body...")
    val processDeserialised = jsonSerialization.deserialize[com.instantor.bsp.CalculatorEvents.process](
        JsonSerialization.buildType(classOf[com.instantor.bsp.CalculatorEvents.process]), body
        )

    logger.info("Processing request for ID {}", processDeserialised.getScrapeID())
    val procesRes = calculator.process(
          processDeserialised.getScrapeID()
        , processDeserialised.getScrapeTime()
        , processDeserialised.getScrape()
        , processDeserialised.getNativeData()
        , processDeserialised.getTransactionInformation())

    procesRes onComplete {
      case Success(x) =>
        logger.info(" Process for ID {} successfuly finshed", processDeserialised.getScrapeID())
      case Failure(t) =>
        val msg = "An error has accured while sending request to calculator.process for scrapeID: %s!" format(processDeserialised.getScrapeID())
        logger.error(msg, t)
    }
*/
    HttpResponse(201, "\"0\"")
  }

  def doPong() = {
    "pong! " + DateTime.now.toString()
  }
}
