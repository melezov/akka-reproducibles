package hr.element

package object akka_reproducibles {
  implicit val executionContext =
    scala.concurrent.ExecutionContext.fromExecutor(
        java.util.concurrent.Executors.newCachedThreadPool())

  import System.{ currentTimeMillis => now }

  def timed[U](f: => U): Int = {
    val startAt = now()
    f
    now() - startAt toInt
  }
}
