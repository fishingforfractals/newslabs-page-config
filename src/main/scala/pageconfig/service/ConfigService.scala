package pageconfig.service

import org.json4s._
import org.json4s.jackson.JsonMethods._
import pageconfig.repository.ConfigRepository
import spray.http.HttpHeaders.Location
import spray.http.MediaTypes.`application/json`
import spray.http.{HttpHeader, ContentType, HttpEntity, HttpResponse, StatusCodes}

import scala.util.{Failure, Success, Try}

class ConfigService(repo: ConfigRepository) {

  val configLookup = new ConfigLookup(repo)

  def configAtPath(keyPath: String) = {
    configLookup.configForPath(keyPath)
      .fold(
        HttpResponse(
          StatusCodes.NotFound,
          HttpEntity(ContentType(`application/json`), configLookup.configFurthestAlongPath(keyPath))
        )
      ) {
      HttpResponse(StatusCodes.OK, _)
    }
  }

  def create(entity: String): HttpResponse = {
    val keyForEntity = Try((parse(entity) \ "id").asInstanceOf[JString].values)

    keyForEntity match {
      case Success(key) =>
        if (configLookup.configForPath(key).isEmpty) {
          val result = repo.set(key, parse(entity))
          HttpResponse(
            status = StatusCodes.Created,
            headers = Location(s"/page/$key")::Nil,
            entity = HttpEntity(ContentType(`application/json`), compact(render(result)))
          )
        } else {
          HttpResponse(
            StatusCodes.Conflict,
            HttpEntity(ContentType(`application/json`), s"""{"error": "Config for $key already exists"}""")
          )
        }
      case Failure(e) =>
        HttpResponse(
          StatusCodes.BadRequest,
          HttpEntity(ContentType(`application/json`), s"""{"error": "Request body is not valid JSON"}""")
        )
    }
  }


  def set(keyPath: String, entityAsJson: String): HttpResponse = {
    val key: String = keyPath.split("/").head

    val entityParsed = Try(parse(entityAsJson))

    entityParsed match {
      case Success(entity) =>
        if (configLookup.configForPath(key).isEmpty) {
          val result = repo.set(key, entity)
          HttpResponse(
            status = StatusCodes.Created,
            headers = Location(s"/page/$key")::Nil,
            entity = HttpEntity(ContentType(`application/json`), compact(render(result)))
          )
        } else {
          val result = repo.set(key, entity)
          HttpResponse(
            StatusCodes.OK,
            HttpEntity(ContentType(`application/json`), compact(render(result)))
          )
        }
      case Failure(e) =>
        HttpResponse(
          StatusCodes.BadRequest,
          HttpEntity(ContentType(`application/json`), s"""{"error": "Request body is not valid JSON"}""")
        )
    }
    

  }
}
