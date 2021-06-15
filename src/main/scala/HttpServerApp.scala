import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorAttributes, Attributes}
import org.json4s.DefaultFormats
import org.json4s.ParserUtil.ParseException
import org.json4s.native.Json
import org.json4s.native.JsonMethods.parse

import java.time.LocalDateTime
import scala.concurrent.duration.DurationInt
import scala.sys.process._
import scala.util.{Failure, Success, Try}

object HttpServerApp {
  implicit val formats = DefaultFormats
  implicit val system = ActorSystem(Behaviors.empty, "HttpServerApp")
  implicit val executionContext = system.executionContext

  val eventCounter = EventCounter()

  val commandFilePath = getClass.getResource("/blackbox.macosx").getPath

  val cmd = "/Users/vkt/Dev/sbtprojects/Ziverge/blackbox.macosx" // Your command
  val output = (commandFilePath lineStream_!)
  val commandOutputSource: Source[String, NotUsed] = Source
    .fromIterator(() => output.iterator)

  val parseLine: Flow[Seq[String], Map[String, Seq[EventData]], NotUsed] = Flow[Seq[String]].map {

    list =>
      println(s"New group at ${LocalDateTime.now} with size of ${list.size}")

      val eventDataList = list.map {
        l =>
          Try {
            parse(l).extract[EventData]
          } match {
            case Success(value) =>
              value
            case Failure(ex: ParseException) =>
              throw ex
            case Failure(ex) =>
              throw ex
          }
      }
      eventDataList.groupBy(_.event_type)
  }
    .withAttributes(ActorAttributes.supervisionStrategy(StreamSupervisions.inputDataErrorDecider))

  def main(args: Array[String]): Unit = {
    val route: Route =
      concat(
        get {
          pathPrefix("eventCounts") {
            val jsonString = Json(DefaultFormats).write(eventCounter.eventCounts)
            complete(jsonString)
          }
        }
      )

    println(s"Server online at http://localhost:8080/eventCounts ")
    Http().newServerAt("localhost", 8080).bind(route)

    commandOutputSource
      .withAttributes(Attributes.asyncBoundary)
      .groupedWithin(1000, 5.seconds)
      .via(parseLine)
      .map(eventCounter.addCounter)
      .runWith(Sink.ignore)
  }
}