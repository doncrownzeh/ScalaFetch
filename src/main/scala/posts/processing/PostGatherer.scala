package posts.processing

import java.util.NoSuchElementException

import io.circe.{HCursor, Json}
import io.circe.parser.parse
import posts.data.Post

class PostGatherer {

  def getPostsFromUrl(url: String): Iterable[Post] = {
    val json = gatherWholeJson(url)
    gatherPostsFromJson(json)
  }

  private def gatherWholeJson(url: String): Json = {
    val rawJson = scala.io.Source.fromURL(url).mkString
    parseJson(rawJson)
  }

  private[processing] def parseJson(rawJson: String): Json = {
    parse(rawJson) match {
      case Left(failure) => throw new IllegalArgumentException(s"Provided JSON is invalid: " + failure.message)
      case Right(json) => json
    }
  }

  private[processing] def gatherPostsFromJson(json: Json): Iterable[Post] = {
    val hCursor: HCursor = json.hcursor
    hCursor.values.get.map(mapJsonToPost)
  }

  private[processing] def mapJsonToPost(json: Json): Post = {
    val cursor = json.hcursor
    try {
      val userId = cursor.get[Long]("userId").right.get
      val id = cursor.get[Long]("id").right.get
      val title = cursor.get[String]("title").right.get
      val body = cursor.get[String]("body").right.get
      Post(userId, id, title, body)
    } catch {
      case e: NoSuchElementException => throw new NullPointerException("Cannot map given JSON to Post because of missing field." + e.getMessage)
    }
  }
}
