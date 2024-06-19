import scopt.OParser

import java.io.FileNotFoundException
import scala.io.StdIn

case class Config(
    delim: String = "\t",
    fields: String = "",
    file: String = "-",
)

case class Selections(lines: List[String] = Nil) {
  private def process(config: Config, line: String): Selections = {
    val fields = line.split(config.delim)
    // Split on "," or " ", convert from 1 to 0 indexed, only accept valid fields, return in order without duplicates
    val fieldsToGet = config.fields.split("[ ,]").filter(_.nonEmpty).map(_.toInt - 1).filter(x => 0 <= x && x < fields.length).sorted.toSet
    val newLine = (fieldsToGet map fields).mkString(config.delim) // Separate correct fields with delim
    Selections(lines :+ newLine)
  }

  override def toString: String = {
    lines.mkString("\n")
  }
}

object Selections {
  def apply(config: Config, lines: Iterator[String]): Selections = {
    lines.foldLeft(Selections())((selections, line) => selections.process(config, line))
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
          "Comma or whitespace separated set of numbers and/or number ranges." +
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
      case _   => selectionsFromFile(config)
    }

    selections.foreach(println)
  }

  private def selectionsFromFile(config: Config): Option[Selections] = {
    val path = if (config.file.startsWith("/")) config.file else (os.pwd / os.RelPath(config.file)).toString()
    try {
      val bufferedSource = io.Source.fromFile(path)
      val selections = Selections(config, bufferedSource.getLines())
      bufferedSource.close()
      Some(selections)
    } catch {
      case _: FileNotFoundException => Console.err.println(s"${config.file}: No such file."); sys.exit(1)
    }
  }
}
