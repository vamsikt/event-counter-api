import java.time.LocalDateTime

case class EventCounter() {

  val eventCounts = scala.collection.mutable.Map[String, Int]()

  def addCounter(eventDataGroups: Map[String, Seq[EventData]]) = {
    println(s"\n Started addCounter eventData1 at ${LocalDateTime.now} \n")

    eventDataGroups.map {

      eventData =>
        val hasEventExists = eventCounts.get(eventData._1)
        if (hasEventExists.isDefined) {
          val newValues = hasEventExists.get + eventData._2.size
          eventCounts(eventData._1) = newValues

        } else {
          eventCounts(eventData._1) = eventData._2.size
        }
    }

  }

}
