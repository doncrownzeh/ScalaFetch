package posts.processing

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.auto._
import io.circe.parser.parse
import posts.data.{Comment, Post}

import scala.collection.immutable.Iterable
import scala.util.Try

class PostGatherer {

  private def getPostsFromUrl(url: String): Iterable[Result[Post]] = {
    val json = gatherWholeJson(url)
    gatherPostsFromJson(json)
  }

  private def gatherWholeJson(url: String): Json = {
    val rawJson = scala.io.Source.fromURL(url).mkString
    parseJson(rawJson)
  }

  private def getCommentsFromUrl(url: String): Iterable[Result[Comment]] = {
    val json = gatherWholeJson(url)
    val values = json.hcursor.values
    values.get.map(json => json.as[Comment])(collection.breakOut)
  }

  def getPostsWithComments(postsUrl: String, commentsUrl: String): Try[Iterable[Post]] = {
    val posts = getPostsFromUrl(postsUrl)
    val comments = getCommentsFromUrl(commentsUrl)
    Try(posts.map(post => attachCommentsToPost(post, comments)))
  }

  private def attachCommentsToPost(post: Result[Post], comments: Iterable[Result[Comment]]): Post = {
   post.map(_.copy(comments = comments.map(_.right.get).filter(_.postId == post.right.get.id))).right.get
  }


  private[processing] def parseJson(rawJson: String): Json = {
    parse(rawJson) match {
      case Left(failure) => throw new IllegalArgumentException(s"Provided JSON is invalid: $failure.message")
      case Right(json) => json
    }
  }

  private[processing] def gatherPostsFromJson(json: Json): Iterable[Either[DecodingFailure, Post]] = json.hcursor.values.get.map(asPost)(collection.breakOut)

  private def asPost(json: Json): Either[DecodingFailure, Post] = {
    val hCursor = json.hcursor
    for {
      userId <- hCursor.downField("userId").as[Long]
      id <- hCursor.downField("id").as[Long]
      title <- hCursor.downField("title").as[String]
      body <- hCursor.downField("body").as[String]
    } yield Post(userId, id, title, body, Iterable.empty)
  }
}
