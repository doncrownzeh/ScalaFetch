package posts.processing
import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.auto._
import posts.data.{Comment, Post}
import io.circe.parser.parse

import scala.collection.immutable.List


class PostGatherer {

  def getPostsWithComments(postsUrl: String, commentsUrl: String): Either[Exception, List[Post]] = getFromUrl[Post](postsUrl, decodePost) match {
    case Left(exception) => Left(exception)
    case Right(posts) => Right(posts.map(post => post.copy(comments = getFromUrl[Comment](commentsUrl, decodeComment) match {
      case Left(exception) => throw new IllegalArgumentException(s"Invalid comment $exception")
      case Right(comments) => comments.filter(comment => comment.postId == post.id)
    })))
  }

  private def getFromUrl[A](url: String, decode: Json => Result[A]): Either[Exception, List[A]] = {
    val rawJson = scala.io.Source.fromURL(url).mkString
    parse(rawJson) match {
      case Left(failure) => Left(failure)
      case Right(json) =>
        json.hcursor.values match {
          case None => Left(new IllegalStateException("Missing JSON"))
          case Some(elements) =>
            val mappedElements = elements.map(decode)
            val failures = mappedElements.collect { case Left(l) => l }
            if (failures.nonEmpty) Left(new IllegalStateException(s"JSON contains invalid fields"))
            else Right(mappedElements.map(_.right.get).toList)
        }
    }
  }

  private def decodePost(json: Json): Result[Post] = {
    val hCursor = json.hcursor
    for {
      userId <- hCursor.downField("userId").as[Long]
      id <- hCursor.downField("id").as[Long]
      title <- hCursor.downField("title").as[String]
      body <- hCursor.downField("body").as[String]
    } yield Post(userId, id, title, body)
  }

  def decodeComment(json: Json): Result[Comment] = {
    json.as[Comment]
  }
}