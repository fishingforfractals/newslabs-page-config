package pageconfig.api

import org.specs2.mutable.Specification
import pageconfig.repository.MapConfigRepository
import pageconfig.service.ConfigService
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.testkit.Specs2RouteTest

class PageConfigApiSpec extends Specification with Specs2RouteTest with PageConfigApi {

  val configService = new ConfigService(prePopulatedRepo)

  def actorRefFactory = system

  "PageConfigApi" should {

    "return OK for GET requests to the status path" in {
      Get("/status") ~> apiRoutes ~> check {
        status === OK
        responseAs[String] must contain("OK")
      }
    }

    "return config value at one level of depth" in {
      Get("/page/frank") ~> apiRoutes ~> check {
        status === OK
        responseAs[String] mustEqual """{"id":"frank","value":"i am frank"}"""
      }
    }

    "return config value at two levels of depth" in {
      Get("/page/chart/top") ~> apiRoutes ~> check {
        status === OK
        responseAs[String] mustEqual """{"id":"top","value":"i am top"}"""
      }
    }

    "return not found status with config furthest along the path as the response body when no config match found" in {
      Get("/page/foo/bar/apple") ~> apiRoutes ~> check {
        status === NotFound
        responseAs[String] mustEqual """{"id":"foo","value":{"id":"buzz","value":{"id":"peach","value":"i am peach"}}}"""
      }
    }

    "return created status and new created entity when POSTing config for id that does not yet exist" in {
      Post("/page", """{"id":"betty","value":"i am betty"}""") ~> apiRoutes ~> check {
        status === Created
        header("Location") mustEqual Some(Location("/page/betty"))
        responseAs[String] mustEqual """{"id":"betty","value":"i am betty"}"""
      }
    }

    "return conflict status and error when POSTing config for id that already exists" in {
      Post("/page", """{"id":"frank","value":"i am frank"}""") ~> apiRoutes ~> check {
        status === Conflict
          responseAs[String] mustEqual """{"error": "Config for frank already exists"}"""
      }
    }

    "return created status and new created entity when PUTing config for id that does not yet exist" in {
      Put("/page/maud", """{"id":"maud","value":"i am maud"}""") ~> apiRoutes ~> check {
        status === Created
        header("Location") mustEqual Some(Location("/page/maud"))
        responseAs[String] mustEqual """{"id":"maud","value":"i am maud"}"""
      }
    }

    "return conflict status and error when PUTing config for id that already exists" in {
      Put("/page/frank", """{"id":"frank","value":"i am frank updated"}""") ~> apiRoutes ~> check {
        status === OK
        responseAs[String] mustEqual """{"id":"frank","value":"i am frank updated"}"""
      }
    }

    "return user error when POSTing invalid json" in {
      Post("/page", """{invalid json}""") ~> apiRoutes ~> check {
        status === BadRequest
        responseAs[String] mustEqual """{"error": "Request body is not valid JSON"}"""
      }
    }

    "return user error when PUTing invalid json" in {
      Put("/page/frank", """{invalid json}""") ~> apiRoutes ~> check {
        status === BadRequest
        responseAs[String] mustEqual """{"error": "Request body is not valid JSON"}"""
      }
    }
  }

  def prePopulatedRepo = {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._

    val repo = new MapConfigRepository
    repo.set("frank", parse("""{"id":"frank", "value":"i am frank"}"""))
    repo.set("chart", parse("""{"id":"chart", "value":{"id":"top", "value":"i am top"}}"""))
    repo.set("foo", parse("""{"id":"foo","value":{"id":"buzz","value":{"id":"peach","value":"i am peach"}}}"""))
    repo
  }
}
