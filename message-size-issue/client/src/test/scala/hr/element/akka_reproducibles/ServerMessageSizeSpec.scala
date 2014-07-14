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
class ServerMessageSizeSpec
    extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Server will accept requests up to a limit") {

    scenario("echo A * 10K") {
      When("a request for 10K A echo is sent")
      val echoFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/echo") << ("A" * 10000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 200")
      echoRes.getStatusCode should be(200)

      And("the response body should be 10.000 bytes of As")
      echoRes.getResponseBodyAsBytes() should be ("A" * 10000 getBytes)
    }

    scenario("echo B * 1M") {
      When("a request for 1M B echo")
      val echoFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/echo") << ("B" * 1000000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 200")
      echoRes.getStatusCode should be(200)

      And("the response body should be 1.000.000 bytes of Bs")
      echoRes.getResponseBodyAsBytes() should be ("B" * 1000000 getBytes)
    }

    scenario("echo C * 10M") {
      When("a request for 10M C echo")
      val echoFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/echo") << ("C" * 10000000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 200")
      echoRes.getStatusCode should be(200)

      And("the response body should be 10.000.000 bytes of Cs")
      echoRes.getResponseBodyAsBytes() should be ("C" * 10000000 getBytes)
    }

    scenario("echo F * 11M (expecting failure)") {
      Given("That the bridge limit for spray messages is 100.000.000 bytes")
      And("that the netty.tcp buffer limit is set to ~ 10.500.000 bytes")
      When("a request is made with a payload of 11.000.000 Fs")

      val echoFut = Http(url("http://akka-reproducibles-bridge:8080/server/test/echo") << ("F" * 11000000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 413")
      echoRes.getStatusCode should be(413)
    }

  }
}
