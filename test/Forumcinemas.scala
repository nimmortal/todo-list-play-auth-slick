import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WS
import play.api.test.WithServer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Forumcinemas extends PlaySpec {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  "XML response forumcinemas" should {

    "return something" in new WithServer {
      val request = WS.url("http://www.forumcinemas.lv/eng/xml/Events/").get()

      val res = Await.result(request, Duration.Inf)

      print(res.body)

      val events = res.xml \\ "event"

      print("Elements " + events.size)

    }

  }

}
