import scopt.OParser

import scala.io.StdIn

import com.github.taylorpeterson.FileHelpers

case class Config(
    delim: String = "\t",
    fields: String = "",
    file: String = "-",
)

case class Selections(lines: List[String] = Nil) {
  private def processLine(config: Config, line: String): Selections = {
    val fields = line.split(config.delim)
    val fieldsToGet = config.fields
      .split("[ ,]") // Split on either/both " " and ,
      .filter(_.nonEmpty) // Ignore any empty matches that may arise (e.g. if were handling "1, 2"
      .map(_.toInt - 1) // Convert from 1 to 0 indexing
      .filter(x => 0 <= x && x < fields.length) // Only field indices that are in bounds for the line
      .distinct // Ignore duplicates
      .sorted // Return fields in order
    val newLine = (fieldsToGet map fields).mkString(config.delim) // Separate correct fields with delim
    Selections(lines :+ newLine)
  }

  override def toString: String = {
    lines.mkString("\n")
  }
}

object Selections {
  def apply(config: Config, lines: Iterator[String]): Selections = {
    lines.foldLeft(Selections())((selections, line) => selections.processLine(config, line))
  }
}

object Main extends App {
  private val builder = OParser.builder[Config]
  private val parser = {
    import builder._
    OParser.sequence(
      programName("cut"),
      head("cut", "0.1"),
      note(
        "The cut utility cuts out selected portions of each line (as specified by list) from each file and writes them to standard output." +
          "Output fields are separated by a single occurrence of the field delimiter character.\n",
      ),
      opt[String]('d', "delim")
        .action((delimiter, c) => c.copy(delim = delimiter))
        .text("Use delim as the field delimiter character instead of the tab character"),
      opt[String]('f', "fields")
        .action((fields, c) => c.copy(fields = fields))
        .text(
          "Comma and/or whitespace separated set of numbers." +
            "\nSpecifies fields, separated in the input by the field delimiter character (see the -d option)" +
            "\nNumbers may be repeated and in any order." +
            "\nIf a field or column is specified multiple times, it will appear only once in the output." +
            "\nIt is not an error to select columns or fields not present in the input line.",
        ),
      arg[String]("file")
        .action((file, c) => c.copy(file = file))
        .optional()
        .text(
          "Source of lines to cut; if not file arguments are specified, or a file argument is a single ('-')" +
            " cut reads from the standard input",
        ),
      help("help")
        .abbr("h")
        .text("prints this usage text"),
      note(sys.props("line.separator") + "Column and field numbering start from 1."),
    )
  }

  OParser.parse(parser, args, Config()) match {
    case Some(config) => run(config)
    case _            => sys.exit(1)
  }

  private def run(config: Config): Unit = {
    val selections = config.file match {
      case "-" => Some(Selections(config, Iterator.continually(StdIn.readLine).takeWhile(_ != null)))
      case file => FileHelpers.processFile(Selections.apply, config, file)
    }

    selections.foreach(println)
  }
}
