import AuthManager.User
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait MongoRegistries {

  val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(classOf[User])
    , DEFAULT_CODEC_REGISTRY)
}
