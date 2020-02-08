import posts.processing.{PostGatherer, PostSaver}

import scala.util.{Failure, Success}

object Main {
  def main(args: Array[String]): Unit = {
    val url = "https://jsonplaceholder.typicode.com/posts"
    val commentsUrl = "https://jsonplaceholder.typicode.com/comments"
    val gatherer: PostGatherer = new PostGatherer
    val saver: PostSaver = new PostSaver
    val postsWithComments = gatherer.getPostsWithComments(url, commentsUrl)
    postsWithComments match {
      case Success(posts) => posts.foreach(saver.saveToFile)
      case Failure(failure) =>  println(failure)
    }
  }
}