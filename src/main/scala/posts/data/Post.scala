package posts.data

import scala.collection.immutable.List

case class Post(userId: Long, id: Long, title: String, body: String, comments: List[Comment] = List[Comment]())



