import org.scalatest.fixture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec

import scala.sys.process._

class CcwcSpec extends FixtureAnyWordSpec with fixture.ConfigMapFixture with Matchers {
  "ccwc" when {
    "-h" should {
      "display usage information" in { configMap =>
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"
        val command = ccwc + s" -h"
        val output = command.!!
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
        output shouldBe expectedUsage
      }
    }
    "-c with test file" should {
      "return the correct byte count" in { configMap =>
        val testTxtPath = getClass.getResource("test.txt").getPath
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"
        val command = ccwc + s" -c ${testTxtPath}"
        val output = command.!!
        output shouldBe s"342190\t${testTxtPath}\n"
      }
    }
    "-l with test file" should {
      "return the correct line count" in { configMap =>
        val testTxtPath = getClass.getResource("test.txt").getPath
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"
        val command = ccwc + s" -l ${testTxtPath}"
        val output = command.!!
        output shouldBe s"7145\t${testTxtPath}\n"
      }
    }
    "-w with test file" should {
      "return the correct word count" in { configMap =>
        val testTxtPath = getClass.getResource("test.txt").getPath
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"
        val command = ccwc + s" -w ${testTxtPath}"
        val output = command.!!
        output shouldBe s"58164\t${testTxtPath}\n"
      }
    }
    "-m with test file" should {
      "return the correct character count" in { configMap =>
        val testTxtPath = getClass.getResource("test.txt").getPath
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"
        val command = ccwc + s" -m ${testTxtPath}"
        val output = command.!!
        output shouldBe s"339292\t${testTxtPath}\n"
      }
    }
    "with test file" should {
      "return the correct counts for bytes, lines, and words" in { configMap =>
        val testTxtPath = getClass.getResource("test.txt").getPath
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"
        val command = ccwc + s" ${testTxtPath}"
        val output = command.!!
        output shouldBe s"7145\t58164\t342190\t${testTxtPath}\n"
      }
    }
    "with stdin" should {
      "return the correct counts for bytes, lines, and words" in { configMap =>
        val testTxtPath = getClass.getResource("test.txt").getPath
        val targetDir = configMap.getRequired[String]("targetDir")
        val ccwc = targetDir + s"/native-image/ccwc"

        (s"cat ${testTxtPath}" #| ccwc).!! shouldBe s"7145\t58164\t342190\t\n"
      }
    }
  }
}
