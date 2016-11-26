package mastermind

import org.scalatest._

class DefaultGameSetupTest extends FunSpecLike with Matchers {

  describe("Default Setup") {
    val defaultSetup = new Setup
    it("shuffles colors during new game setup") {
      val game = defaultSetup.newGame
      val matches = game.target.zip(defaultSetup.palette).filter(p => p._1 == p._2)
      assert(matches.length !== game.target.length)
    }
  }

}