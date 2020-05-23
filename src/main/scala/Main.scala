import posts.processing.{PostGatherer, PostSaver}

object Main {

  val url = "https://jsonplaceholder.typicode.com/posts"
  val commentsUrl = "https://jsonplaceholder.typicode.com/comments"
  val gatherer: PostGatherer = new PostGatherer
  val saver: PostSaver = new PostSaver

  def main(args: Array[String]): Unit = {
    gatherer.getPostsWithComments(url, commentsUrl) match {
      case Left(failure) =>  Console.err.println(failure)
      case Right(posts) => posts.foreach(saver.saveToFile)
    }
  }

}

