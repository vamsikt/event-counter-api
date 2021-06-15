
case class InvalidEventDataException(message: String = "") extends Exception

case class EventData(event_type: String, data: String, timestamp: Long) {
  override def toString = {
    event_type + " " + data
  }
}

