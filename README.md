#Page Config

This service is a REST-ful application which exposes endpoints to retrieve and update configuration values

It exposes two endpoints:
`/status` and
`/page`

You can GET, POST and PUT configuration key/values in the following form:
##simple
`GET /page/foo` will return
```
{
  "id": "foo"
  "value": "i am foo"
}
```

##nested
If we assume the following format for key `foo` as returned by `/page/foo`:
```
{
  "id": "foo"
  "value": {
    "id":
    "value": "i am bar"
  }
}
```

Then `GET /page/foo/bar` will return
```
{
  "id": "bar"
  "value": "i am bar"
}
```

##Technical design

I considered using Java and implement the standard JAX-RS APIs but in the end I chose to submit the application in Scala.

I chose Scala for several reasons:
* The compiled application can be run on the JVM and I can be relatively sure that whoever runs it will have a JVM runtime installed.
* The NewsLabs team is looking for an applicant with functional programming skills and Scala supports a functional programming style
* The Spray web-services framework is simple and delightful to use, and furthermore Spray comes with a lightweight embedded HTTP server


The application consists of three layers:
* **`PageConfigApi`** : The API routing layer and entry point to the application
* **`ConfigRepository`** interface and its implementation **`MapConfigRepository`** :
* **`ConfigService`** : Composed with a `ConfigRepository` and `ConfigLookup`.  Parses the user's input before passing to the repository.  Uses the ConfigLookup to retrieve existing values from the repository.  Builds the appropriate HttpResponse to return to the user.

Also a **`ConfigLookup`** contains the logic to search the repository for config values


##Running

With [sbt](http://www.scala-sbt.org/) installed:

```
sbt run
```

As an executable jar:

Download the executable jar and run as follows
```
java -jar newslabs-page-config.jar
```

Both will start a service on http://localhost:8080

You can check if the service is running successfully by calling `http://localhost:8080/status` which will return the status `{"status":"OK"}` if all is well.

##Running Tests
Included with this application are the following tests
* an integration test `PageConfigApiSpec`
* unit tests for the `MapConfigRepository` implementation of the repository and the `ConfigLookup` class

To run the tests, with [sbt](http://www.scala-sbt.org/) installed:
```
sbt test
```

