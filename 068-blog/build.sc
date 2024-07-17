import mill._
import $ivy.`com.lihaoyi::mill-contrib-playlib:`,  mill.playlib._

object 068blog extends PlayModule with SingleModule {

  def scalaVersion = "2.13.13"
  def playVersion = "2.8"
  def twirlVersion = "2.0.1"

  object test extends PlayTests
}
