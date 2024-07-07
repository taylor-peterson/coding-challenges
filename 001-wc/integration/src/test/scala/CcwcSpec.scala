import com.github.taylorpeterson.CliSpec

import scala.sys.process._

class CcwcSpec extends CliSpec {
  override val command: String = "ccwc"
  val testTxtPath: String = getClass.getResource("test.txt").getPath
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
        validateCli(ccwc + " -h", expectedStatus = 0, expectedUsage)
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
          validateCli(ccwc + s" $args $testTxtPath", expectedStatus = 0, s"$counts\t$testTxtPath\n")
        }
      }
    }
    "given invalid file path" should {
      "print error message" in { ccwc =>
        validateCli(
          ccwc + " nonexistent-file",
          expectedStatus = 1,
          expectedStdErr = "nonexistent-file: No such file.\n",
        )
      }
    }
    "with stdin" should {
      "return the correct counts for bytes, lines, and words" in { ccwc =>
        validateCli(s"cat $testTxtPath" #| ccwc, expectedStatus = 0, expectedStdOut = s"7145\t58164\t342190\t\n")
      }
    }
  }
}
