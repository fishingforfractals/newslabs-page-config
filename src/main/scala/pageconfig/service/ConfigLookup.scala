package pageconfig.service

import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.jackson.JsonMethods._
import pageconfig.repository.ConfigRepository

class ConfigLookup(repo: ConfigRepository) {

  def configForPath(keyPath: String): Option[String] = {
    val keys = keyPath.split("/").toList

    repo.get(keys.head) map { json =>
      findValueAtPath(json, keys)
    } flatMap {
      _ map { f =>
        compact(render(f))
      }
    }
  }

  def configFurthestAlongPath(keyPath: String): String = {
    val keys = keyPath.split("/").toList

    val furthest = repo.get(keys.head)
      .fold(parse("{}")) { foundJson =>
        findValueFurthestAlongPath(foundJson, foundJson, keys)
    }
    compact(render(furthest))
  }

  private def findValueAtPath(json: JValue, keyPath: List[String]): Option[JValue] = {
    val thisKey = (json \ "id").values
    val keyToFind = keyPath.head

    json \ "value" match {
      case s: JString =>
        if (keyPath.size == 1 && thisKey == keyToFind) Some(json) else None
      case o: JObject => findValueAtPath(o, keyPath.drop(1))
      case _ => None
    }

  }

  private def findValueFurthestAlongPath(testJson: JValue, furthestJson: JValue, keyPath: List[String]): JValue = {
    def onThePath: Boolean = {
      val thisKey = (testJson \ "id").values
      val nextKeyOnPath = keyPath.head
      thisKey == nextKeyOnPath
    }

    testJson \ "value" match {
      case s: JString => if (onThePath) testJson else furthestJson
      case o: JObject => if (onThePath) findValueFurthestAlongPath(o, testJson, keyPath.drop(1)) else furthestJson
      case _ => furthestJson
    }
  }

}
