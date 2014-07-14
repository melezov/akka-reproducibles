package hr.element.akka_reproducibles
package actions

trait Action

case object TestOK extends Action

case object TestSlow extends Action

case object TestTimeout extends Action

case object TestError extends Action

case class TestEcho(body: String) extends Action

case class Process(body: String) extends Action
