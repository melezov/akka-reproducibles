package hr.element.akka_reproducibles

import dispatch._

import org.junit.runner.RunWith
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

@RunWith(classOf[JUnitRunner])
class ServerDirectSpec
    extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Bridge proxies requests through to server") {

    scenario("200 OK") {
      When("a request for 200 OK is sent")
      val okFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/ok"))
      val okRes = Await.result(okFut, 10 seconds)

      Then("the status code should be 200")
      okRes.getStatusCode should be(200)

      And("the response body should be OK")
      okRes.getResponseBody should be ("OK")
    }

    scenario("Slow 201") {
      Given("that the timeout spray can timeout was set to 5 seconds")
      And("that the timeout on server is set to 3 seconds")

      timed {

        When("a request for 201 after 1 second wait is sent")
        val slowFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/slow"))
        val slowRes = Await.result(slowFut, 10 seconds)

        Then("the status code should be 201")
        slowRes.getStatusCode should be(201)

        And("the response body should be Phew... finished!")
        slowRes.getResponseBody should be ("Phew... finished!")

      } should be >= 1000

      And("the response should take more than 1000 ms")
    }

    scenario("Timeout 500") {
      Given("that the timeout spay can timeout was set to 5 seconds")

      timed {

        When("a request for 500 timout")
        val timeoutFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/timeout"))
        val timeoutRes = Await.result(timeoutFut, 10 seconds)

        Then("the status code should be 500")
        timeoutRes.getStatusCode should be(500)

      } should be (5000 +- 1000)

      And("the response should take around 5 seconds")
    }

    scenario("Error 500") {
      Given("that we want to verify what happens on error without detach")

      When("a request for an error is sent")
      val errorFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/error"))
      val errorRes = Await.result(errorFut, 10 seconds)

      Then("the status code should be 500")
      errorRes.getStatusCode should be(500)

      And("the response body should contain the error message")
      errorRes.getResponseBody should startWith ("You requested an error")
    }

  }
}
