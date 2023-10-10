import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration.{Duration, SECONDS}

object StabilityCheck {

  trait StabilityCheckMessages
  private case class CheckInstances() extends StabilityCheckMessages

  private case class AppInstance(name: String, host: String, login: String, password: String)
  private val instances = getInstances


  def apply(): Behavior[StabilityCheckMessages] = {
    Behaviors.withTimers(timers => {
      timers.startTimerWithFixedDelay(CheckInstances(), Duration.Zero, Duration(5, SECONDS))
      Behaviors.receiveMessage({
        case CheckInstances() =>
          checkInstances()
          Behaviors.same
        case _ => Behaviors.same
      })
    })
  }
  private def getInstances: List[AppInstance] = {
    //todo get instances
    List.empty[AppInstance]
  }
  private def checkInstances(): Unit = {
    instances.foreach(inst => {
      //todo check and restart if error
    })
  }
}
