import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
// TODO test split fields on comma and space

class CutSpec extends AnyWordSpec with Matchers {
  "cut" when {
    "invoked" should {
      "run" in {
        Main.main(Array("-f", "1", getClass.getResource("sample.tsv").getPath))
      }
    }
  }
}
