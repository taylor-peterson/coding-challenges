import scopt.OParser

import java.io.FileNotFoundException
import scala.io.StdIn

case class Config(
    lines: Boolean = false,
    words: Boolean = false,
    chars: Boolean = false,
    bytes: Boolean = false,
    file: String = "-",
)

case class Counts(
    lines: BigInt = 0,
    words: BigInt = 0,
    chars: BigInt = 0,
    bytes: BigInt = 0,
) {
  private val CARRIAGE_RETURN_NEWLINE = 2

  private def increment(line: String): Counts = {
    Counts(
      lines + 1,
      words + line.split("""\s""").count(_.nonEmpty),
      chars + line.length + CARRIAGE_RETURN_NEWLINE,
      bytes + line.getBytes().length + CARRIAGE_RETURN_NEWLINE,
    )
  }
}
object Counts {
  def apply(lines: Iterator[String]): Counts = {
    lines.foldLeft(Counts())((counts, line) => counts.increment(line))
  }
}

object Main extends App {
  private val builder = OParser.builder[Config]

  private val parser = {
    import builder._
    OParser.sequence(
      programName("ccwc"),
      head("ccwc", "0.1"),
      opt[Unit]('l', "lines")
        .action((_, c) => c.copy(lines = true))
        .text("print the line counts"),
      opt[Unit]('w', "words")
        .action((_, c) => c.copy(words = true))
        .text("print the word counts"),
      opt[Unit]('m', "chars")
        .action((_, c) => c.copy(chars = true))
        .text("print the char counts"),
      opt[Unit]('c', "bytes")
        .action((_, c) => c.copy(bytes = true))
        .text("print the byte counts"),
      arg[String]("file")
        .action((file, c) => c.copy(file = file))
        .optional()
        .text("file to process (defaults to '-' meaning STDIN)"),
      help("help")
        .abbr("h")
        .text("prints this usage text"),
      note(sys.props("line.separator") + "When no flags are set, line, word, and byte counts will be provided."),
    )
  }

  // TODO extract
  private def countsFromFile(file: String): Option[Counts] = {
    val path = if (file.startsWith("/")) file else (os.pwd / os.RelPath(file)).toString()
    try {
      val bufferedSource = io.Source.fromFile(path)
      val counts = Counts(bufferedSource.getLines())
      bufferedSource.close()
      Some(counts)
    } catch {
      case _: FileNotFoundException => Console.err.println(s"$file: No such file."); sys.exit(1)
    }
  }

  private def run(config: Config): Unit = {
    val counts = config.file match {
      case "-"  => Some(Counts(Iterator.continually(StdIn.readLine).takeWhile(_ != null)))
      case file => countsFromFile(file)
    }

    counts.foreach(c => {
      val output = "%s%s%s%s%s".format(
        if (config.lines) s"${c.lines}\t" else "",
        if (config.words) s"${c.words}\t" else "",
        if (config.chars) s"${c.chars}\t" else "",
        if (config.bytes) s"${c.bytes}\t" else "",
        if (config.file != "-") config.file else "",
      )
      println(output)
    })
  }

  OParser.parse(parser, args, Config()) match {
    case Some(config) =>
      config match {
        // Default to bytes, lines, words if no other counts were specified.
        case Config(false, false, false, false, file) =>
          run(Config(lines = true, words = true, chars = false, bytes = true, file))
        case _ => run(config)
      }
    case _ => sys.exit(1)
  }
}
