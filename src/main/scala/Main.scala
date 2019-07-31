import posts.processing.{PostGatherer, PostSaver}

object Main {
  def main(args: Array[String]): Unit = {
    val url = "https://jsonplaceholder.typicode.com/posts"
    val extractor: PostGatherer = new PostGatherer
    val saver: PostSaver = new PostSaver
    val posts = extractor.getPostsFromUrl(url)
    posts.foreach(saver.saveToFile)
  }
}