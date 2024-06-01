import org.scalatest.Outcome
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.FixtureAnyWordSpec

import scala.sys.process._

class CcwcSpec extends FixtureAnyWordSpec with Matchers with TableDrivenPropertyChecks {
  val testTxtPath: String = getClass.getResource("test.txt").getPath

  type FixtureParam = String
  def withFixture(test: OneArgTest): Outcome = {
    val targetDir = test.configMap.getRequired[String]("targetDir")
    val ccwc: FixtureParam = targetDir + s"/native-image/ccwc"
    withFixture(test.toNoArgTest(ccwc))
  }

  def returnCounts: AfterWord = afterWord("return the correct counts for")

  "ccwc" when {
    "-h" should {
      "display usage information" in { ccwc =>
        val expectedUsage =
          """ccwc 0.1
            |Usage: ccwc [options] [file]
            |
            |  -l, --lines  print the line counts
            |  -w, --words  print the word counts
            |  -m, --chars  print the char counts
            |  -c, --bytes  print the byte counts
            |  file         file to process (defaults to '-' meaning STDIN)
            |  -h, --help   prints this usage text
            |
            |When no flags are set, line, word, and byte counts will be provided.
            |""".stripMargin
        (ccwc + " -h").!! shouldBe expectedUsage
      }
    }
    "counting from test file" should returnCounts {
      val countsTable = Table(
        ("test case", "args", "counts"),
        ("bytes", "-c", 342190),
        ("lines", "-l", 7145),
        ("words", "-w", 58164),
        ("characters", "-m", 339292),
        ("no args", "", "7145\t58164\t342190"),
      )

      forEvery(countsTable) { (testCase, args, counts) =>
        testCase in { ccwc =>
          (ccwc + s" $args $testTxtPath").!! shouldBe s"$counts\t$testTxtPath\n"
        }
      }
    }
    "given invalid file path" should {
      "print error message" in { ccwc =>
        (ccwc + " nonexistent-file").!! shouldBe "nonexistent-file: No such file."
      }
    }
    "with stdin" should {
      "return the correct counts for bytes, lines, and words" in { ccwc =>
        (s"cat $testTxtPath" #| ccwc).!! shouldBe s"7145\t58164\t342190\t\n"
      }
    }
  }
}
