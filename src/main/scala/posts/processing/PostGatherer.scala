package posts.processing

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.auto._
import posts.data.{Comment, Post}
import io.circe.parser.parse

import scala.io._
import scala.collection.immutable.List
import scala.util.{Failure, Success, Try}


class PostGatherer {

  def getPostsWithComments(postsUrl: String, commentsUrl: String): Either[Exception, List[Post]] = getFromUrl[Post](postsUrl) match {
    case Left(exception) => Left(new IllegalStateException(s"Problem with retrieving posts from given URL: ${exception.getMessage}"))
    case Right(posts) => getFromUrl[Comment](commentsUrl) match {
      case Left(exception: Exception) => Left(new IllegalStateException(s"Problem with retrieving comments from given URL: ${exception.getMessage}"))
      case Right(allComments) => Right(posts.map(post => post.copy(comments = allComments.filter(comment => comment.postId == post.id))))
    }
  }

  private def getFromUrl[A](url: String)(implicit decode: Json => Result[A]): Either[Exception, List[A]] = {
    val jsonString = Try(Source.fromURL(url).mkString)
    jsonString match {
      case Failure(_) => Left(new IllegalStateException(s"Couldn't retrieve JSON from: $url"))
      case Success(rawJson) => parse(rawJson) match {
        case Left(failure) => Left(failure)
        case Right(json) => json.hcursor.values match {
            case None => Left(new IllegalStateException("Missing JSON"))
            case Some(elements) => decodeElements[A](elements)(decode)
          }
      }
    }
  }

  private def decodeElements[A](elements: Iterable[Json])(decode: Json => Result[A]): Either[Exception, List[A]] = {
    val mappedElements = elements.map(decode)
    val failures = mappedElements.collect { case Left(l) => l }
    if (failures.nonEmpty) Left(new IllegalStateException(s"JSON contains invalid fields"))
    else Right(mappedElements.map(_.right.get).toList)
  }

  private implicit def decodePost(json: Json): Result[Post] = {
    val hCursor = json.hcursor
    for {
      userId <- hCursor.downField("userId").as[Long]
      id <- hCursor.downField("id").as[Long]
      title <- hCursor.downField("title").as[String]
      body <- hCursor.downField("body").as[String]
    } yield Post(userId, id, title, body)
  }

  private implicit def decodeComment(json: Json): Result[Comment] = {
    json.as[Comment]
  }
}