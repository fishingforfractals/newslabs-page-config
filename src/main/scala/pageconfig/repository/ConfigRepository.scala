package pageconfig.repository

import org.json4s.JsonAST.JValue

trait ConfigRepository {
  /**
   *
   * @param key a configuration key
   * @return the json object value for this key
   */
  def get(key: String): Option[JValue]

  /**
   *
   * @param key a configuration key
   * @param value the json object value, represented as a string, to be stored against the given key
   * @return the json object stored, as stored in the repository
   */
  def set(key: String, value: JValue): JValue
}

/**
 * This implementation of ConfigRepository stores its keys and values in a Map.
 */
class MapConfigRepository extends ConfigRepository {

  var repo = Map[String, JValue]()

  override def get(key: String): Option[JValue] = {
    repo get key
  }

  override def set(key: String, value: JValue): JValue = {
    val repoCopy = repo + (key -> value)
    repo = repoCopy
    value
  }
}
