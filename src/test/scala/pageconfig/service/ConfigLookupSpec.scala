package pageconfig.service

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import pageconfig.repository.ConfigRepository
import org.json4s.JsonDSL._

class ConfigLookupSpec extends Specification with Mockito {

  "ConfigLookup" should {

    "return values for keys one level deep" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("foo") returns Some(("id"->"foo") ~ ("value"->"i am foo"))

      val result = underTest.configForPath("foo")
      result mustEqual Some("""{"id":"foo","value":"i am foo"}""")
    }

    "return nothing for absent keys one level deep" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("foo") returns None

      val result = underTest.configForPath("foo")
      result mustEqual None
    }

    "return values for keys two levels deep" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("bar") returns Some(("id"->"bar") ~ ("value"-> ("id"->"apple") ~ ("value"->"i am apple")))

      val result = underTest.configForPath("bar/apple")
      result mustEqual Some("""{"id":"apple","value":"i am apple"}""")
    }

    "return nothing for absent keys two levels deep" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("bar") returns Some(("id"->"bar") ~ ("value"-> ("id"->"apple") ~ ("value"->"i am apple")))

      val result = underTest.configForPath("bar/peach")
      result mustEqual None
    }

    "return values for keys four levels deep" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("foo") returns
        Some(
        ("id" -> "foo") ~
          ("value" -> ("id" -> "bar") ~
            ("value" -> ("id" -> "apple") ~
              ("value" -> ("id" -> "jam") ~
                ("value" -> "i am jam")))))

      val result = underTest.configForPath("foo/bar/apple/jam")

      result mustEqual Some("""{"id":"jam","value":"i am jam"}""")
    }

    "return values furthest along the path as possible" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("foo") returns
        Some(
          ("id" -> "foo") ~
            ("value" -> ("id" -> "bar") ~
              ("value" -> ("id" -> "apple") ~
                ("value" -> ("id" -> "jam") ~
                  ("value" -> "i am jam")))))

      val result = underTest.configFurthestAlongPath("foo/bar/peach/car")

      result mustEqual """{"id":"bar","value":{"id":"apple","value":{"id":"jam","value":"i am jam"}}}"""
    }

    "return empty json document from configFurthestAlongPath if no config found" in {
      val mockRepo = mock[ConfigRepository]
      val underTest = new ConfigLookup(mockRepo)

      mockRepo.get("foo") returns None

      val result = underTest.configFurthestAlongPath("foo/bar/peach/car")

      result mustEqual """{}"""
    }

  }
}
