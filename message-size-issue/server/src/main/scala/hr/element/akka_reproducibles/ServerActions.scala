package hr.element.akka_reproducibles

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import akka.actor.Actor
import scala.util._

import actions._

private object ServerActions {
  implicit val executionContext = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool()
  )
}

class ServerActions extends Actor {
  import ServerActions.executionContext

  def receive = {
    case TestOK =>
      sender ! "OK"

    case TestSlow =>
      val origin = sender
      Future {
        Thread.sleep(1000)
        origin ! ((201, "Phew... finished!"))
      }

    case TestTimeout =>
      val origin = sender
      Future {
        Thread.sleep(300000)
        origin ! "I was too slow ;_;"
      }

    case TestError =>
      val origin = sender
      Future {
        origin ! Try {
          sys.error("You requested an error, so here it is!"): String
        }
      }

    case TestEcho(body) =>
      sender ! body

    case Process(body) =>
      val origin = sender
      Future {
        origin ! ("Received body of size: " + body.length)
      }
  }
}
