import HttpManager.startHttpServer
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.util.logging.{Level, Logger}
import scala.util.control.NonFatal

object Main {

  private val logger = LoggerFactory.getLogger("master")
  Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF)

  def main(args: Array[String]): Unit = {
    if (DBManager.start()){
      lazy val system: ActorSystem[Nothing] = ActorSystem[Nothing](Behaviors.setup[Nothing](context => {

        val auth = context.spawn(AuthManager(), "auth")
        val stability = context.spawn(StabilityCheck(), "stability")

        logger.info("actors spawned")
        startHttpServer(system, auth)
        Behaviors.empty
      }), "master")
      try {
        system
      } catch {
        case NonFatal(e) =>
          logger.error("Terminating due to initialization failure.", e)
          system.terminate()
      }
    }
  }
}
