package posts.data

case class Post(userId: Long, id: Long, title: String, body: String, comments: Iterable[Comment]) {
}
