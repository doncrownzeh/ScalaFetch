package posts.processing

import io.circe.Json
import posts.data.{Comment, Post}

import scala.collection.immutable.List


class PostSaver {

  def saveToFile(fileName: String, content: String): Unit = {
    import java.io._
    try {
      val pw = new PrintWriter(new File(fileName))
      pw.write(content)
      pw.close() // wyciek (monada Resource w catz)
    } catch {
      case e: FileNotFoundException => throw new IOException(s"Failed to create file: $fileName. " + e.getMessage)
    }
  }

  def saveToFile(post: Post): Unit = {
    val fileName = post.id.toString + ".json"
    val content = mapPostToJson(post).toString
    saveToFile(fileName, content)
  }

  private def mapPostToJson(post: Post): Json = {
    val userId = Json.fromLong(post.userId)
    val id = Json.fromLong(post.id)
    val title = Json.fromString(post.title)
    val body = Json.fromString(post.body)
    val comments = Json.fromValues(post.comments.map(mapCommentToJson))
   Json.fromFields(List(("userId", userId), ("id", id), ("title", title), ("body", body), ("comments", comments)))
  }

  private def mapCommentToJson(comment: Comment): Json = {
    val postId = Json.fromLong(comment.postId)
    val id = Json.fromLong(comment.id)
    val name = Json.fromString(comment.name)
    val email = Json.fromString(comment.email)
    val body = Json.fromString(comment.body)
    Json.fromFields(List(("postId", postId), ("id", id), ("name", name), ("email", email), ("body", body)))
  }

}
