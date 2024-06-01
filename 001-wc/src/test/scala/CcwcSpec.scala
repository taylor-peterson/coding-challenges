import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CcwcSpec extends AnyWordSpec with Matchers {
  "Counts" when {
    "initialized with lines" should {
      "contain the correct counts" in {
        val lines = io.Source.fromString("""foo
                                           |bar baz
                                           |""".stripMargin).getLines()
        Counts(lines) shouldBe Counts(2, 3, 14, 14)
      }
    }
  }
}
