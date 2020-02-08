package posts.data

import scala.collection.immutable.Iterable

case class Post(userId: Long, id: Long, title: String, body: String, comments: Iterable[Comment]) {
}
