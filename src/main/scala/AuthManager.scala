import HttpManager.{Response, TextResponse}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import io.circe.generic.JsonCodec
import io.circe.syntax.EncoderOps
import org.mongodb.scala.MongoCollection
import org.slf4j.LoggerFactory
import slick.lifted._

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import DBManager._

import scala.language.postfixOps

object AuthManager {

  private val logger = LoggerFactory.getLogger(this.toString)
  private val usersCollectionName = "users"

  trait AuthMessage
  case class Login(replyTo: ActorRef[Response]) extends AuthMessage
  case class GetUsers(replyTo: ActorRef[Response]) extends AuthMessage

  @JsonCodec case class User(id: String, login: String, password: String)
  class UserTable(tag: Tag) extends Table[User] (tag, "users") {
    val id = column[String]("id")
    val login = column[String]("login")
    val password = column[String]("password")
    override def * = (id, login, password) <> ((User.apply _).tupled, User.unapply)
  }

  def apply(): Behavior[AuthMessage] = Behaviors.receiveMessage({
    case Login(replyTo) =>
      addUser("01")
      addUser("02")
      replyTo.tell(TextResponse("success".asJson.noSpaces))
      Behaviors.same
    case GetUsers(replyTo) =>
//      getUsersPG().onComplete {
//        case Success(value) =>
//          replyTo.tell(TextResponse(value.map(x => User.apply(x.id, x.login, x.password)).toList.asJson.noSpaces))
//        case Failure(exception) =>
//          logger.error(exception.toString)
//          replyTo.tell(TextResponse("server error"))
//      }
      getUsersMongo.onComplete{
        case Success(users) =>
          replyTo.tell(TextResponse(users.toList.asJson.noSpaces))
        case Failure(exception) =>
          logger.error(exception.toString)
          replyTo.tell(TextResponse("server error"))
      }
      Behaviors.same
  })
  private def getUsersMongo: Future[Seq[User]] = {
    val mongo = MongoDB
    val usersCollection: MongoCollection[User] = mongo.getCollection(usersCollectionName)
    usersCollection.find().toFuture()
  }
  private def addUser(id: String): Unit = {
    val user1 = User(UUID.randomUUID().toString, "log" + id, "pass" + id)
    val usersCollection: MongoCollection[User] = DBManager.MongoDB.getCollection(usersCollectionName)
    usersCollection.insertOne(user1).subscribe(() => _)
  }

  private def getUsersPG(): Future[Seq[UserTable#TableElementType]] = {
    val usersTable = TableQuery[UserTable]
    PostgresSQL.run(usersTable.result)
  }
}
