package posts.processing

import java.util.NoSuchElementException

import io.circe.{HCursor, Json}
import io.circe.parser.parse
import posts.data.{Comment, Post}

class PostGatherer {

  def getPostsFromUrl(url: String): Iterable[Post] = {
    val json = gatherWholeJson(url)
    gatherPostsFromJson(json)
  }

  def getCommentsFromUrl(url: String): Iterable[Comment] = {
    val json = gatherWholeJson(url)
    val hCursor = json.hcursor
    hCursor.values.get.map(mapJsonToComment)
  }

  private def gatherWholeJson(url: String): Json = {
    val rawJson = scala.io.Source.fromURL(url).mkString
    parseJson(rawJson)
  }

  def getPostsWithComments(posts: Iterable[Post], comments: Iterable[Comment]): Iterable[Post] = {
    posts.map(post => {
      attachCommentsToPost(post, comments)
    })
  }

  private def attachCommentsToPost(post: Post, comments: Iterable[Comment]): Post = {
    val postId = post.id
    val filteredComments = comments.filter(comment => {
      comment.postId == postId
    })
    post.copy(comments = filteredComments)
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
    val either = for {
      userId <- cursor.get[Long]("userId")
      id <- cursor.get[Long]("id")
      title <- cursor.get[String]("title")
      body <- cursor.get[String]("body")
    } yield Post(userId, id, title, body, Iterable())
    either.right.get
  }
    catch
    {
      case e: NoSuchElementException => throw new NullPointerException("Cannot map given JSON to Post because of missing field." + e.getMessage)
    }
  }

  private def mapJsonToComment(json: Json): Comment = {
    val cursor = json.hcursor
    try {
      val postId = cursor.get[Long]("postId").right.get
      val id = cursor.get[Long]("id").right.get
      val name = cursor.get[String]("name").right.get
      val email = cursor.get[String]("email").right.get
      val body = cursor.get[String]("body").right.get
      Comment(postId, id, name, email, body)
    }
  }
}
