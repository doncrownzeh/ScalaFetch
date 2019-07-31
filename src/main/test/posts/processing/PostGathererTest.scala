package posts.processing

import io.circe.Json
import org.scalatest.{BeforeAndAfter, FeatureSpec}
import org.scalatest.GivenWhenThen
import posts.data.Post

class PostGathererTest extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var postGatherer: PostGatherer = _

  before {
    postGatherer = new PostGatherer
  }

  feature("Gathering posts from JSON") {

    scenario("Incorrect JSON should cause an exception") {
      Given("a not valid JSON")
      val rawJson = "broken file"
      When("trying to parse it")
      val caught = intercept[IllegalArgumentException] {
        postGatherer.parseJson(rawJson)
      }
      Then("exception with proper message is expected")
      assert(caught.getMessage.contains("Provided JSON is invalid"))
    }

    scenario("Correct JSON should return proper number of posts") {
      Given("JSON containing exactly 3 posts")
      val rawJson =
        """
          |[
          |  {
          |    "userId": 1,
          |    "id": 1,
          |    "title": "lorem ipsum",
          |    "body": "lorem ipsum lorem ipsum"
          |  },
          |  {
          |    "userId": 1,
          |    "id": 2,
          |    "title": "lorem ipsum",
          |    "body": "lorem ipsum lorem ipsum"
          |  },
          |  {
          |    "userId": 1,
          |    "id": 3,
          |    "title": "lorem ipsum",
          |    "body": "lorem ipsum lorem ipsum lorem ipsum"
          |  }
          |]
        """.stripMargin
      When("gathering posts from it")
      val json: Json = postGatherer.parseJson(rawJson)
      val posts: Iterable[Post] = postGatherer.gatherPostsFromJson(json)

      Then("collection of 3 posts is generated")
      assert(posts.size == 3)
    }

    scenario("Correct post object should be created from valid JSON post") {
      Given("valid JSON post")
      val rawJson =
        """
          |  {
          |    "userId": 1,
          |    "id": 1,
          |    "title": "lorem ipsum",
          |    "body": "lorem ipsum lorem ipsum"
          |  }
        """.stripMargin
      val expected = Post(1, 1, "lorem ipsum", "lorem ipsum lorem ipsum")
      When("creating object from it")
      val json = postGatherer.parseJson(rawJson)
      val result = postGatherer.mapJsonToPost(json)
      Then("object is created as expected")
      assert(result == expected)
    }

    scenario("An exception should be thrown when trying to create post object out of valid JSON with missing fields") {
      Given("valid JSON with missing fields")
      val rawJson =
        """
          |  {
          |    "userId": 1,
          |    "body": "lorem ipsum lorem ipsum"
          |  }
        """.stripMargin
      When("creating object from it")
      val json = postGatherer.parseJson(rawJson)
      val caught = intercept[NullPointerException] {
        postGatherer.mapJsonToPost(json)
      }
      Then("an exception with proper message is thrown")
      assert(caught.getMessage.contains("Cannot map given JSON to Post because of missing field."))
    }
  }
}
