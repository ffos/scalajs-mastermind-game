package mastermind

import org.scalajs.dom.document
import org.scalajs.dom.window

import scalatags.JsDom.all._
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.raw.Event

object App {
  val setup = new Setup

  def main(args: Array[String]): Unit = newGame

  def newGame = {
    println("Starting New Game")
    render(setup.newGame)
  }

  def decide(game: Game)(playersChoices: Guess) = {
    val either = game.guess(playersChoices)
    if (either.isLeft) {
      window.alert(either.left.get)
    } else {
      val newGameInst = either.right.get
      App.render(either.right.get)
      if (newGameInst.hasWon) window.alert("You won!")
      else if (newGameInst.hasTurnsExhausted) window.alert("You lost. No more turns left")
    }
  }

  def render(game: Game) = {
    document.body.innerHTML = ""
    document.body.appendChild(Gui(game).render)
  }

}

case class Gui(val game: Game) {
  val startNewGameBtn = button("New Game").render
  val testMoveBtn = button("Test Move").render
  val showTargetBtn = button("Show Target").render

  var playerGuesses: List[Option[Color]] = game.target.map(x => None) // !! mutable

  startNewGameBtn.onclick = (e: Event) => App.newGame
  showTargetBtn.onclick = (e: Event) => targetRow.style.setProperty("display", "flex", "")
  testMoveBtn.onclick = (e: Event) => if (playerGuesses.find(_.isEmpty).isEmpty) App.decide(game)(Guess(playerGuesses.map(_.get)))

  val activeHoles = (0 until game.target.length)
    .zip(game.target.map(x => hole(None).render))
    .map(p => {
      val el = p._2
      el.onclick = (e: Event) => cycleColor(p)
      p
    })
  /**
   * click handler for active holes for player's guesses
   * It cycles through the palette colors in the same hole
   */
  def cycleColor(p: (Int, HTMLElement)) = {
    val palette = App.setup.palette
    val holeIndex = p._1
    val elem = p._2
    val newColorIndex = Option(elem.getAttribute(s"data-color")).orElse(Some("-1")).map(v => (v.toInt + 1) % (palette.length)).get
    val newColor = palette.takeRight(palette.length - newColorIndex).head

    elem.style.backgroundColor = s"${newColor.rgbToCssString}"
    elem.setAttribute(s"data-color", newColorIndex.toString())
    playerGuesses = playerGuesses.updated(holeIndex, Some(newColor))
  }

  def guessCell(turn: Option[Turn]) = div(cls := "cell", guesses(turn.map(_.guess)))
  def hintCell(turn: Option[Turn]) = div(cls := "cell", hints(turn.map(_.hint)))
  val emptyCell = div(cls := "cell")
  val targetCell = div(cls := "cell", game.target.map(coloredHole))
  val targetRow = div(cls := "row target", emptyCell, targetCell).render
  def emptyHole = div(cls := "hole")
  def coloredHole(color: Color) = div(cls := "hole", style := s"background-color: ${color.rgbToCssString}")
  def hole(color: Option[Color]) = if (color.isEmpty) emptyHole else coloredHole(color.get)

  def render = div(cls := "game-container", gameBox).render

  def gameBox = div(cls := "box", gameBoxTitle, targetRow, rows)
  def gameName = h1(cls := "game-name", "MasterMind [", a(href := "http://bit.ly/1VnFT8U", target := "_blank", "wiki"), "]")
  def gameBoxTitle = div(cls := "box-title", gameName, gameBoxTitleButtons)
  def gameBoxTitleButtons = div(cls := "row", startNewGameBtn, testMoveBtn, showTargetBtn)

  def rows =
    (0 until game.maxTurns)
      .map(Option.apply)
      .zipAll(game.steps.reverse.map(Option.apply), None, None)
      .map(p =>
        if (p._1.get == game.steps.length) activeRow(p._2)
        else if (p._1.get < game.steps.length) div(cls := "row disabled", hintCell(p._2), guessCell(p._2))
        else div(cls := "row", hintCell(p._2), guessCell(p._2))).reverse

  def activeRow(turn: Option[Turn]) = {
    val rowTypeCls = if (game.hasWon) "row" else if (game.hasTurnsExhausted) "row disabled" else "row highlighted"
    div(cls := rowTypeCls, hintCell(turn), div(cls := "cell", activeHoles.map(x => x._2)))
  }

  def guesses(guess: Option[Guess]) =
    if (guess.isEmpty) game.target.map(x => hole(None))
    else game.target.map(Option.apply)
      .zipAll(guess.get.colors.map(Option.apply), None, None)
      .map(p => hole(p._2))

  def hints(hint: Option[Hint]) =
    if (hint.isEmpty) game.target.map(x => None).map(hole)
    else game.target.map(Option.apply)
      .zipAll(hintColors(hint.get), None, None)
      .map(p => hole(p._2))

  def hintColors(hint: Hint): List[Option[Color]] =
    hint.aligned.map(x => Some(App.setup.alignedHintColor)) union hint.notAligned.map(x => Some(App.setup.notAlignedHintColor))

}

