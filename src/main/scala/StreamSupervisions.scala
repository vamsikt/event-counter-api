import akka.stream.Supervision
import org.json4s.ParserUtil.ParseException

object StreamSupervisions {

  val inputDataErrorDecider: Supervision.Decider = {
    case _: Exception =>
      Supervision.Resume
    case _:  ParseException =>
      Supervision.Resume
    case _: Exception =>
      Supervision.Resume
  }

}
