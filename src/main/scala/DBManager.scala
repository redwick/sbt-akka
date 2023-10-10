import com.typesafe.config.{Config, ConfigFactory}
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database

import java.net.{InetSocketAddress, Socket, SocketAddress}

object DBManager extends MongoRegistries {

  private val logger = LoggerFactory.getLogger("database")
  private val config: Config = ConfigFactory.load()


  private lazy val mongoClient: MongoClient = MongoClient()
  lazy val MongoDB: MongoDatabase = mongoClient.getDatabase(config.getString("app-name")).withCodecRegistry(codecRegistry)

  lazy val PostgresSQL: JdbcBackend.Database = Database.forConfig("postgres")

  def start(): Boolean = {
    try {
      if (check()){
        MongoDB
        PostgresSQL
        true
      }
      else{
        false
      }
    }
    catch {
      case _: Throwable =>
        logger.error("Error starting Database")
        false
    }
  }

  private def check(): Boolean = {
    checkMongo && checkPostgres
  }
  private def checkMongo: Boolean = {
    try {
      val sAddr = new InetSocketAddress(config.getString("mongodb.host"), config.getInt("mongodb.port"))
      val socket = new Socket()
      socket.connect(sAddr, 1000)
      true
    }
    catch {
      case _: Throwable =>
        logger.error("Could not establish connection to MongoDB")
        false
    }
  }
  private def checkPostgres: Boolean = {
    try {
      val sAddr = new InetSocketAddress(config.getString("postgres.properties.serverName"), config.getInt("postgres.properties.portNumber"))
      val socket = new Socket()
      socket.connect(sAddr, 1000)
      true
    }
    catch {
      case _: Throwable =>
        logger.error("Could not establish connection to PostgresSQL")
        false
    }
  }

}
