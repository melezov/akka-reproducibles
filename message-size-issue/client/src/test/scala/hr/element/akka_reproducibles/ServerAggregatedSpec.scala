package hr.element.akka_reproducibles

import dispatch._

import org.junit.runner.RunWith
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

@RunWith(classOf[JUnitRunner])
class ServerAggregatedSpec
    extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Server processes requests asynchronously") {

    scenario("Slow 201") {
      Given("that the slow tests takes around 1 second")
      When("200 requests for slow 201 are sent")

      timed {
        val slowFutures =
          (for (i <- 1 to 200) yield {
            Http(url("http://akka-reproducibles-bridge:8080/server/test/slow"))
          })

        val slowResults = Await.result(
          Future.sequence(slowFutures),
          10 seconds
        )

        slowResults map { slowRes =>
          slowRes.getStatusCode should be(201)
          slowRes.getResponseBody should be ("Phew... finished!")
        }
      } should be < 5000

      And("all requests should be processed under 5 seconds")
    }

    scenario("Timeout 500") {
      Given("that the timeout in spray was set to 5 seconds")
      When("200 requests for that timeout are sent")

      timed {
        val timeoutFutures =
          (for (i <- 1 to 200) yield {
            Http(url("http://akka-reproducibles-bridge:8080/server/test/timeout"))
          })

        val timeoutResults = Await.result(
          Future.sequence(timeoutFutures),
          10 seconds
        )

        timeoutResults map { timeoutRes =>
          timeoutRes.getStatusCode should be(500)
        }
      } should be < 10000

      And("all requests should be processed under 10 seconds")
    }

  }
}
