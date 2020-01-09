import posts.processing.{PostGatherer, PostSaver}

object Main {
  def main(args: Array[String]): Unit = {
    val url = "https://jsonplaceholder.typicode.com/posts"
    val commentsUrl = "https://jsonplaceholder.typicode.com/comments"
    val gatherer: PostGatherer = new PostGatherer
    val postsWithComments = gatherer.getPostsWithComments(url, commentsUrl)
    val saver: PostSaver = new PostSaver
    postsWithComments.foreach(saver.saveToFile)
  }
}