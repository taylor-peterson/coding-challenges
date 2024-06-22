import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec

class MainSpec extends AnyWordSpec with Matchers with TableDrivenPropertyChecks {
  "Selections" when {
    "processLine" should {
      val testCases = Table(
        ("test case", "fields", "lines", "correct result"),
        ("get the first field", "1", Iterator("1\t2"), "1"),
        ("split on ,", "1,2", Iterator("1\t2\t3"), "1\t2"),
        ("split on ' '", "1 2", Iterator("1\t2\t3"), "1\t2"),
        ("split on ' ' and ,", "1 2,3", Iterator("1\t2\t3"), "1\t2\t3"),
        ("split and strip extra spaces", "1 2, 3", Iterator("1\t2\t3"), "1\t2\t3"),
        ("ignore out of bounds fields (too big)", "1 2, 5", Iterator("1\t2\t3"), "1\t2"),
        ("ignore out of bounds fields (too small)", "-1 2", Iterator("1\t2\t3"), "2"),
        ("ignore duplicate fields", "1 2 2", Iterator("1\t2\t3"), "1\t2"),
        ("return fields in order", "2 1", Iterator("1\t2\t3"), "1\t2"),
        ("handle multiple lines", "1", Iterator("1\t2","3\t4"), "1\n3"),
        ("handle different line lengths", "1,2", Iterator("1","2\t3"), "1\n2\t3"),
      )

      forEvery(testCases) { (testCase, fields, lines, correctResult) =>
        testCase in {
          val config = Config(fields = fields)
          Selections(config, lines).toString shouldBe correctResult
        }
      }
    }
  }
}
