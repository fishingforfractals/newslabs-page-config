package pageconfig.api

import akka.actor.{Actor, ActorLogging, ActorRefFactory}
import pageconfig.repository.MapConfigRepository
import pageconfig.service.ConfigService
import spray.http.MediaTypes.`application/json`
import spray.routing.HttpService
import spray.routing.RejectionHandler.Default

class PageConfigApiActor extends Actor with PageConfigApi with ActorLogging {
  def actorRefFactory: ActorRefFactory = context

  implicit val system = context.system

  val configService: ConfigService = new ConfigService(new MapConfigRepository)

  def receive: Receive = runRoute(apiRoutes)
}

trait PageConfigApi extends HttpService {

  val configService: ConfigService

  val apiRoutes =
    path("status") {
      get {
        respondWithMediaType(`application/json`) {
          complete("{ \"status\": \"OK\" }")
        }
      }
    } ~ path("page" / Rest) { key =>
      get {
        complete(configService.configAtPath(key))
      } ~ put {
        entity(as[String]) { body =>
          complete(configService.set(key, body))
        }
      }
    } ~ path("page") {
      post {
        entity(as[String]) { body =>
          complete(configService.create(body))
        }
      }
    }


}
