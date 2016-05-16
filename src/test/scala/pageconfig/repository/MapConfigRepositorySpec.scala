package pageconfig.repository

import org.specs2.mutable.Specification
import org.json4s.JsonDSL._

class MapConfigRepositorySpec extends Specification {

  "MapConfigRepository" should {

    "store and retrieve json values" in {
      val underTest = new MapConfigRepository
      underTest.set("foo", ("id" -> "foo") ~ ("value"->"i am foo"))
      val result = underTest.get("foo")
      result mustEqual Some(("id" -> "foo") ~ ("value"->"i am foo"))
    }

    "return nothing if a key is not found in the repository" in {
      val underTest = new MapConfigRepository
      underTest.set("foo", ("id" -> "foo") ~ ("value"->"i am foo"))
      val result = underTest.get("bar")
      result mustEqual None
    }

  }
}
