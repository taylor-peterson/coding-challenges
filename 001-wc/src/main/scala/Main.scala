import scopt.OParser

case class Config(
    lines: Boolean = false,
    words: Boolean = false,
    chars: Boolean = false,
    bytes: Boolean = false,
    file: String = "-",
)

object Main extends App {
  private val builder = OParser.builder[Config]

  private val parser1 = {
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
        .text("file to process (defaults to '-' meaning STDIN)"),
      help("help")
        .abbr("h")
        .text("prints this usage text"),
    )
  }

  private def run(config: Config): Unit = {
    val CARRIAGE_RETURN_NEWLINE = 2
    var (line_count, word_count, char_count, byte_count) = (BigInt(0), BigInt(0), BigInt(0), BigInt(0))

    def increment_counts(line: String): Unit = {
      line_count += 1
      word_count += line.split("""\s""").count(_.nonEmpty)
      char_count += line.length + CARRIAGE_RETURN_NEWLINE
      byte_count += line.getBytes().length + CARRIAGE_RETURN_NEWLINE
    }

    if (config.file == "-") {
      val lines = io.StdIn.toString.split("""\r\n""")
      lines.foreach(line => increment_counts(line))
    } else {
      val path = if (config.file.startsWith("/")) os.Path(config.file) else os.pwd / os.RelPath(config.file)
      if (os.exists(path)) {
        val lineStream = os.read.lines.stream(path)
        lineStream.foreach(line => increment_counts(line))
      } else {
        println(s"File ${config.file} does not exist.")
      }
    }

    var output = ""
    if (config.lines) output += line_count + "\t"
    if (config.words) output += word_count + "\t"
    if (config.chars) output += char_count + "\t"
    if (config.bytes) output += byte_count + "\t"
    if (config.file != "-") output += config.file

    println(output)
  }

  // Default to bytes, lines, words if no other counts were specified.
  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
      config match {
        case Config(false, false, false, false, file) =>
          run(Config(lines = true, words = true, chars = false, bytes = true, file))
        case _ => run(config)
      }
    case _ => // Erroneous input messaging handled by OParser
  }
}
