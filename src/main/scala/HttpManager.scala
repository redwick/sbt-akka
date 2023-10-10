import AuthManager.{GetUsers, Login}
import HttpManager.config
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route}
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import java.util.Date
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.{Duration, SECONDS}
import scala.io.StdIn
import scala.util.{Success, _}

object HttpManager {

  private val logger = LoggerFactory.getLogger("http")
  private val config = ConfigFactory.load()

  trait Response
  case class TextResponse(value: String) extends Response

  def startHttpServer(system: ActorSystem[Nothing], auth: ActorRef[AuthManager.AuthMessage]): Unit = {
    implicit val sys: ActorSystem[Nothing] = system
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    implicit val timeout: Timeout = Duration(5, SECONDS)
    val route: Route = cors() {
      concat(
        (get & path("time")) {
          complete(HttpEntity(new Date().getTime.toString))
        },
        (get & path("login")) {
          forward(auth.ask(ref => Login(ref)))
        },
        (get & path("users")) {
          forward(auth.ask(ref => GetUsers(ref)))
        }
      )
    }
    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt(config.getString("http.host"), config.getInt("http.port")).bind(route)
    logger.info("http started at " + config.getString("http.host") + ":" + config.getString("http.port"))
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
  private def forward(future: Future[Any]): Route = {
    onComplete(future) {
      case Success(value) => value match {
        case TextResponse(value) => complete(HttpEntity(value))
        case _ =>
          logger.error("unidentified message received from actor")
          complete(HttpEntity("server error"))
      }
      case Failure(exception) =>
        logger.error(exception.toString)
        complete(HttpEntity("server error"))
    }
  }
}
