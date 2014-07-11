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
class BridgeMessageSizeSpec
    extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Bridge will accept requests up to a limit") {

    info("There are some tests that do not propagate to the server,")
    info("but rather test spray acceptance of payloads in regards to their size")

    scenario("echo A * 10K") {
      When("a request for 10K A echo is sent")
      val echoFut = Http(url("http://10.5.100.14:8080/bridge/test/echo") << ("A" * 10000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 200")
      echoRes.getStatusCode should be(200)

      And("the response body should be 10.000 bytes of As")
      echoRes.getResponseBodyAsBytes() should be ("A" * 10000 getBytes)
    }

    scenario("echo B * 1M") {
      When("a request for 1M B echo")
      val echoFut = Http(url("http://10.5.100.14:8080/bridge/test/echo") << ("B" * 1000000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 200")
      echoRes.getStatusCode should be(200)

      And("the response body should be 1.000.000 bytes of Bs")
      echoRes.getResponseBodyAsBytes() should be ("B" * 1000000 getBytes)
    }

    scenario("echo C * 100M") {
      When("a request for 100M C echo")
      val echoFut = Http(url("http://10.5.100.14:8080/bridge/test/echo") << ("C" * 100000000))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 200")
      echoRes.getStatusCode should be(200)

      And("the response body should be 100.000.000 bytes of Cs")
      echoRes.getResponseBodyAsBytes() should be ("C" * 100000000 getBytes)
    }

    scenario("echo F * 100M+1 (expecting failure)") {
      Given("That the bridge limit for spray messages is 100.000.000 bytes")
      When("a request is made with a payload of 100.000.001 Fs")
      val echoFut = Http(url("http://10.5.100.14:8080/bridge/test/echo") << ("F" * 100000001))
      val echoRes = Await.result(echoFut, 10 seconds)

      Then("the status code should be 413")
      echoRes.getStatusCode should be(413)
    }

  }
}
