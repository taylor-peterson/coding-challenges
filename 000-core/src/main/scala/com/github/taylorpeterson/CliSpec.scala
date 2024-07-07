import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.FixtureAnyWordSpec

import scala.sys.process.ProcessLogger
import scala.sys.process._

package com.github.taylorpeterson {

  import org.scalatest.Outcome

  abstract class CliSpec extends FixtureAnyWordSpec with Matchers with TableDrivenPropertyChecks {
    def validateCli(
                     command: ProcessBuilder,
                     expectedStatus: Int,
                     expectedStdOut: String = "",
                     expectedStdErr: String = "",
                   ): Assertion = {
      val stdout = new StringBuilder
      val stderr = new StringBuilder
      val status = command ! ProcessLogger(stdout append _ + "\n", stderr append _ + "\n")
      status shouldBe expectedStatus
      stdout.toString shouldBe expectedStdOut
      stderr.toString shouldBe expectedStdErr
    }

    type FixtureParam = String
    val command: FixtureParam
    def withFixture(test: OneArgTest): Outcome = {
      withFixture(test.toNoArgTest(test.configMap.getRequired[String](command)))
    }
  }
}
