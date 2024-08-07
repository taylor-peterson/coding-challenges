import com.github.taylorpeterson.CliSpec
import scala.sys.process._

class CutSpec extends CliSpec {
  override val command: String = "cut"

  private val sampleTsvPath = getClass.getResource("sample.tsv").getPath
  private val fourChordsCsvPath = getClass.getResource("fourchords.csv").getPath

  "cut" when {
    "-h" should {
      "display usage information" in { cut =>
        val expectedUsage =
          """cut 0.1
            |Usage: cut [options] [file]
            |
            |The cut utility cuts out selected portions of each line (as specified by list) from each file and writes them to standard output.Output fields are separated by a single occurrence of the field delimiter character.
            |
            |  -d, --delim <value>   Use delim as the field delimiter character instead of the tab character
            |  -f, --fields <value>  Comma and/or whitespace separated set of numbers.
            |                        Specifies fields, separated in the input by the field delimiter character (see the -d option)
            |                        Numbers may be repeated and in any order.
            |                        If a field or column is specified multiple times, it will appear only once in the output.
            |                        It is not an error to select columns or fields not present in the input line.
            |  file                  Source of lines to cut; if not file arguments are specified, or a file argument is a single ('-') cut reads from the standard input
            |  -h, --help            prints this usage text
            |
            |Column and field numbering start from 1.
            |""".stripMargin
        validateCli(s"$cut -h", expectedStatus = 0, expectedUsage)
      }
    }
    "provided invalid options" should {
      "provide info and fail" in { cut =>
        val command = s"$cut -n"
        val expectedOutput =
          """Error: Unknown option -n
            |Try --help for more information.
            |""".stripMargin
        validateCli(command, expectedStatus = 1, expectedStdErr = expectedOutput)
      }
    }
    "provided invalid file" should {
      "provided info and fail" in { cut =>
        val command = s"$cut nonexistent.file"
        val expectedOutput = "nonexistent.file: No such file.\n"
        validateCli(command, expectedStatus = 1, expectedStdErr = expectedOutput)
      }
    }
    "cutting single field from file" should {
      "yield f1" in { cut =>
        val expectedOutput =
          """f0
            |0
            |5
            |10
            |15
            |20
            |""".stripMargin
        validateCli(s"$cut -f 1 $sampleTsvPath", expectedStatus = 0, expectedOutput)
      }
      "yield f2" in { cut =>
        val expectedOutput =
          """f1
            |1
            |6
            |11
            |16
            |21
            |""".stripMargin
        validateCli(s"$cut -f 2 $sampleTsvPath", expectedStatus = 0, expectedOutput)
      }
    }
    "given a delimiter" should {
      "split on commas" in { cut =>
        val expectedOutput = // Test files starts with ZWNBSP as `byte-order-mark`
          """\uFEFFSong title
            |"10000 Reasons (Bless the Lord)"
            |"20 Good Reasons"
            |"Adore You"
            |"Africa"
            |""".stripMargin
        validateCli(s"$cut -f 1 -d , $fourChordsCsvPath" #| "head -n5", expectedStatus = 0, expectedOutput)
      }
    }
    "given a list of fields" should {
      "parse on comma correctly" in { cut =>
        val expectedOutput =
          s"""f0\tf1
            |0\t1
            |5\t6
            |10\t11
            |15\t16
            |20\t21
            |""".stripMargin
        validateCli(s"$cut -f 1,2 $sampleTsvPath", expectedStatus = 0, expectedOutput)
      }
      "parse on space correctly" in { cut =>
        val expectedOutput =
          """\uFEFFSong title,Artist
            |"10000 Reasons (Bless the Lord)",Matt Redman\u00A0and\u00A0Jonas Myrin
            |"20 Good Reasons",Thirsty Merc
            |"Adore You",Harry Styles
            |"Africa",Toto
            |""".stripMargin
        validateCli(s"$cut -d , -f \"1,2\" $fourChordsCsvPath" #| "head -n5", expectedStatus = 0, expectedOutput)
      }
      "parse on space and comma, re-order, de-duplicate, and ignore out of bounds fields" in { cut =>
        val expectedOutput =
          s"""f0\tf1
             |0\t1
             |5\t6
             |10\t11
             |15\t16
             |20\t21
             |""".stripMargin
        val command = s"$cut -f \"2,1,1, 8\" $sampleTsvPath"
        validateCli(command, expectedStatus = 0, expectedOutput)
      }
    }
    "no file is provided" should {
      "read from standard in" in { cut =>
        val expectedOutput =
          """"Young Volcanoes",Fall Out Boy
            |"You Found Me",The Fray
            |"You'll Think Of Me",Keith Urban
            |"You're Not Sorry",Taylor Swift
            |"Zombie",The Cranberries
            |""".stripMargin
        validateCli(s"tail -n5 $fourChordsCsvPath" #| s"$cut -d , -f \"1,2\"", expectedStatus = 0 , expectedOutput)
      }
    }
    "- is provided as file" should {
      "read from standard in" in { cut =>
        val expectedOutput =
          """"Young Volcanoes",Fall Out Boy
            |"You Found Me",The Fray
            |"You'll Think Of Me",Keith Urban
            |"You're Not Sorry",Taylor Swift
            |"Zombie",The Cranberries
            |""".stripMargin
        validateCli(s"tail -n5 $fourChordsCsvPath" #| s"$cut -d , -f \"1 2\" -", expectedStatus = 0, expectedOutput)
      }
    }
  }
}
