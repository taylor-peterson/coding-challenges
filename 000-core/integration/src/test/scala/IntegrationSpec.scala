import org.scalatest.Outcome
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec

import scala.sys.process._

class IntegrationSpec extends FixtureAnyWordSpec with Matchers {
  case class FixtureParam(cut: String, csvFile: String, ccwc: String)

  def withFixture(test: OneArgTest): Outcome = {
    super.withFixture(
      test.toNoArgTest(
        FixtureParam(
          test.configMap.getRequired[String]("cut"),
          test.configMap.getRequired[String]("csvFile"),
          test.configMap.getRequired[String]("ccwc"),
        ),
      ),
    )
  }
  "cut and ccwc" when {
    "piped together" should {
      "work" in { f =>
        (s"${f.cut} -f 2 -d , ${f.csvFile}" #| "uniq" #| s"${f.ccwc} -l").!!.strip().toInt shouldBe 155
      }
    }
  }
}
