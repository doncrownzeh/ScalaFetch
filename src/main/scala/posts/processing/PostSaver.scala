package posts.processing

import io.circe.Json
import posts.data.Post

import scala.collection.immutable.List


class PostSaver {

  def saveToFile(fileName: String, content: String): Unit = {
    import java.io._
    try {
      val pw = new PrintWriter(new File(fileName))
      pw.write(content)
      pw.close()
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
    val userId = Json.fromLong(post.id)
    val id = Json.fromLong(post.userId)
    val title = Json.fromString(post.title)
    val body = Json.fromString(post.body)

   Json.fromFields(List(("userId", userId), ("id", id), ("title", title), ("body", body)))
  }

}
