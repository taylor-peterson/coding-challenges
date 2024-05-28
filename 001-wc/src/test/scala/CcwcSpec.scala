import org.scalatest.Outcome
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec

import scala.sys.process._

class CcwcSpec extends FixtureAnyWordSpec with Matchers {
  val testTxtPath: String = getClass.getResource("test.txt").getPath

  type FixtureParam = String
  def withFixture(test: OneArgTest): Outcome = {
    val targetDir = test.configMap.getRequired[String]("targetDir")
    val ccwc: FixtureParam = targetDir + s"/native-image/ccwc"
    withFixture(test.toNoArgTest(ccwc))
  }

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
            |""".stripMargin
        (ccwc + " -h").!! shouldBe expectedUsage
      }
    }
    "-c with test file" should {
      "return the correct byte count" in { ccwc =>
        (ccwc + s" -c $testTxtPath").!! shouldBe s"342190\t$testTxtPath\n"
      }
    }
    "-l with test file" should {
      "return the correct line count" in { ccwc =>
        (ccwc + s" -l $testTxtPath").!! shouldBe s"7145\t$testTxtPath\n"
      }
    }
    "-w with test file" should {
      "return the correct word count" in { ccwc =>
        (ccwc + s" -w $testTxtPath").!! shouldBe s"58164\t$testTxtPath\n"
      }
    }
    "-m with test file" should {
      "return the correct character count" in { ccwc =>
        (ccwc + s" -m $testTxtPath").!! shouldBe s"339292\t$testTxtPath\n"
      }
    }
    "with test file" should {
      "return the correct counts for bytes, lines, and words" in { ccwc =>
        (ccwc + s" $testTxtPath").!! shouldBe s"7145\t58164\t342190\t$testTxtPath\n"
      }
    }
    "with stdin" should {
      "return the correct counts for bytes, lines, and words" in { ccwc =>
        (s"cat $testTxtPath" #| ccwc.toString()).!! shouldBe s"7145\t58164\t342190\t\n"
      }
    }
  }
}
