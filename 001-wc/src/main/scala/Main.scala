import scopt.OParser

case class Config(
    bytes: Boolean = false,
    lines: Boolean = false,
    words: Boolean = false,
    chars: Boolean = false,
    file: String = "-",
)

object Main extends App {
  private val builder = OParser.builder[Config]

  private val parser1 = {
    import builder._
    OParser.sequence(
      programName("ccwc"),
      head("ccwc", "0.1"),
      opt[Unit]('c', "bytes")
        .action((_, c) => c.copy(bytes = true))
        .text("print the byte counts"),
      opt[Unit]('l', "lines")
        .action((_, c) => c.copy(lines = true))
        .text("print the line counts"),
      opt[Unit]('w', "words")
        .action((_, c) => c.copy(words = true))
        .text("print the word counts"),
      opt[Unit]('m', "chars")
        .action((_, c) => c.copy(chars = true))
        .text("print the char counts"),
      opt[String]("file")
        .action((file, c) => c.copy(file = file))
        .text("file to process (defaults to '-' meaning STDIN)"),
      help("help")
        .abbr("h")
        .text("prints this usage text"),
    )
  }

  def run(config: Config): Unit = {
    println(config)
  }

  OParser.parse(parser1, args, Config()) match {
    case Some(config) => run(config)
    case _            => // Messaging handled by OParser
  }
}
