import posts.processing.{PostGatherer, PostSaver}

object Main {
  def main(args: Array[String]): Unit = {
    val url = "https://jsonplaceholder.typicode.com/posts"
    val commentsUrl = "https://jsonplaceholder.typicode.com/comments"
    val gatherer: PostGatherer = new PostGatherer
    val saver: PostSaver = new PostSaver
    val posts = gatherer.getPostsFromUrl(url)
    val comments = gatherer.getCommentsFromUrl(commentsUrl)
    val postsWithComments = gatherer.getPostsWithComments(posts, comments) // 3 funkcje z gatherera - tylko jedna publiczna
    postsWithComments.foreach(saver.saveToFile)
  }
}