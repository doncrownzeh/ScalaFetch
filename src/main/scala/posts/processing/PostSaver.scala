package posts.processing

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import posts.data.Post

class PostSaver {

  def saveToFile(fileName: String, content: Json): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(fileName))
    try {
      pw.write(content.toString())
    } catch {
      case e: FileNotFoundException => throw new IOException(s"Failed to create file: $fileName. " + e.getMessage)
    }
    finally {
      pw.close()
    }
  }

  def saveToFile(post: Post): Unit = {
    val fileName = post.id + ".json"
    val content = post.asJson
    saveToFile(fileName, content)
  }
}
