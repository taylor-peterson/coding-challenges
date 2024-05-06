import scopt.OParser

case class Config(
    foo: Int = -1,
    xyz: Boolean = false,
    maxCount: Int = -1,
)

object Main extends App {
  val builder = OParser.builder[Config]

  val parser1 = {
    import builder._
    OParser.sequence(
        programName("scopt"),
        head("scopt", "4.x"),
        opt[Int]('f', "foo")
      .action((x, c) => c.copy(foo = x))
      .text("foo is an integer property"),
    help("help").text("prints this usage text"),
    )
  }
  OParser.parse(parser1, args, Config()) match {
    case Some(config) => println(OParser.usage(parser1))
    case _ =>
  }
}
