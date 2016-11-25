package mastermind

import org.scalatest._

class GameTest extends FunSpecLike with Matchers{
  
  describe("Gameplay") {
    val red = Color("Red", (1,1,1))
    val green = Color("Green", (2,2,2))
    val blue = Color("Blue", (3,3,3))
    val yellow = Color("Yellow", (4,4,4))
    val black = Color("Black", (5,5,5))
    val game = Game(List(red, green, blue, yellow), 2, List())
    
    def badGuess(g:Game) = g.guess(Guess(List(red, red, red, red))) 
    def gameAfterBadGuess(g:Game) = badGuess(g).right.get 
    def winningGuess(g:Game) = g.guess(Guess(game.target))
    def gameAfterWinningGuess(g:Game) = winningGuess(g).right.get
    
    describe("Using up turns") {
      it("has turns left initially") {
        assert(game.hasTurnsExhausted === false) 
      }
      it("has turns left before exhausting turns") {
    	  assert(gameAfterBadGuess(game).hasTurnsExhausted === false) 
      }
      it("has no turns left after exhausting turns") {
    	  assert(gameAfterBadGuess(gameAfterBadGuess(game)).hasTurnsExhausted === true) 
      }
      it("is not possible to make more guesses after exhausting turns") {
    	  assert(badGuess(gameAfterBadGuess(gameAfterBadGuess(game))) === Left("No more turns left")) 
      }
    }
    
    describe("Game won") {
      it("is not 'won' initially") {
        assert(game.hasWon === false) 
      }
      it("can be won before exhausting turns") {
    	  assert(gameAfterWinningGuess(game).hasWon === true) 
      }
      it("can be won in the final step") {
    	  assert(gameAfterWinningGuess(gameAfterBadGuess(game)).hasWon === true) 
      }
      it("is not possible to make more guesses after winning") {
    	  assert(winningGuess(gameAfterWinningGuess(game)) === Left("Game already won")) 
      }
    }

    describe("Hints") {
      it("evaluates hints with no matching colors") {
        val turn = game.guess(Guess(List(black, black, black, black))).right.get.steps.head
    	  assert(turn.hint.aligned === List()) 
    	  assert(turn.hint.notAligned === List()) 
      }
      it("evaluates hints with one matching color not in alignment") {
    	  val turn = game.guess(Guess(List(black, game.target.head, black, black))).right.get.steps.head
			  assert(turn.hint.aligned === List()) 
			  assert(turn.hint.notAligned === List(game.target.head)) 
      }
      it("evaulautes hints with one matching color IN alignment") {
    	  val turn = game.guess(Guess(List(black, game.target.take(2).reverse.head, black, black))).right.get.steps.head
			  assert(turn.hint.aligned === List(game.target.take(2).reverse.head)) 
			  assert(turn.hint.notAligned === List()) 
      }
      it("evaulautes hints with two colors IN & NOT-IN alignment") {
    	  val turn = game.guess(Guess(List(black, game.target.take(2).reverse.head, game.target.head, black))).right.get.steps.head
			  assert(turn.hint.aligned === List(game.target.take(2).reverse.head)) 
			  assert(turn.hint.notAligned === List(game.target.head)) 
      }
      it("evaulautes hints with all colors not in alignment") {
    	  val turn = game.guess(Guess(game.target.reverse)).right.get.steps.head
			  assert(turn.hint.aligned === List())
			  assert(turn.hint.notAligned === game.target) 
      }
      it("evaulautes hints with all colors IN in alignment") {
    	  val turn = game.guess(Guess(game.target)).right.get.steps.head
			  assert(turn.hint.aligned === game.target)
			  assert(turn.hint.notAligned === List()) 
      }
    }
    
  }
  
}