package mastermind

import org.scalatest._
import org.scalatest.funspec._

class DefaultGameSetupTest extends AnyFunSpec {

  describe("Default Setup") {
    val defaultSetup = new Setup
    it("shuffles colors during new game setup") {
      val game = defaultSetup.newGame
      val matches = game.target.zip(defaultSetup.palette).filter(p => p._1 == p._2)
      print("Matches:")
      assert(matches.length !== game.target.length)
    }
  }

}