package pageconfig

import akka.actor.{Props, ActorSystem}
import akka.event.Logging
import akka.io.IO
import akka.util.Timeout
import pageconfig.api.PageConfigApiActor
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

object Boot extends App {

  //an ActorSystem to host our application in
  implicit val system = ActorSystem("spray-system")

  val log = Logging(system, getClass)
  log.info("starting page-config application")

  // create and start our service actor
  val service = system.actorOf(Props[PageConfigApiActor], "page-config-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  implicit val timeout = Timeout(1.seconds)
  private val port: Int = 8080
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = port)
}
