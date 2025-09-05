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
    val container = document.getElementById("game-container")
    container.innerHTML = ""
    container.appendChild(Gui(game).render)
  }

}

case class Gui(val game: Game) {
  val startNewGameBtn = button(
    cls := "bg-gradient-to-r from-blue-500 to-blue-600 dark:from-blue-600 dark:to-blue-700 hover:from-blue-600 hover:to-blue-700 dark:hover:from-blue-700 dark:hover:to-blue-800 text-white font-semibold py-1.5 px-2 sm:py-2 sm:px-4 text-xs sm:text-sm rounded-lg shadow-md hover:shadow-lg transition-all duration-200 transform hover:scale-105",
    span(cls := "hidden sm:inline", "🎮 New Game"),
    span(cls := "sm:hidden", "🎮")
  ).render
  
  val testMoveBtn = button(
    cls := "bg-gradient-to-r from-green-500 to-green-600 dark:from-green-600 dark:to-green-700 hover:from-green-600 hover:to-green-700 dark:hover:from-green-700 dark:hover:to-green-800 text-white font-semibold py-1.5 px-2 sm:py-2 sm:px-4 text-xs sm:text-sm rounded-lg shadow-md hover:shadow-lg transition-all duration-200 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100",
    span(cls := "hidden sm:inline", "✓ Submit Guess"),
    span(cls := "sm:hidden", "✓")
  ).render
  
  val showTargetBtn = button(
    cls := "bg-gradient-to-r from-purple-500 to-purple-600 dark:from-purple-600 dark:to-purple-700 hover:from-purple-600 hover:to-purple-700 dark:hover:from-purple-700 dark:hover:to-purple-800 text-white font-semibold py-1.5 px-2 sm:py-2 sm:px-4 text-xs sm:text-sm rounded-lg shadow-md hover:shadow-lg transition-all duration-200 transform hover:scale-105",
    span(cls := "hidden sm:inline", "👁️ Show Target"),
    span(cls := "sm:hidden", "👁️")
  ).render

  var playerGuesses: List[Option[Color]] = game.target.map(x => None) // !! mutable

  startNewGameBtn.onclick = (e: Event) => App.newGame
  showTargetBtn.onclick = (e: Event) => {
    targetRow.style.setProperty("display", "flex", "")
    targetRow.className = "flex items-center justify-center gap-4 p-4 mb-4 bg-yellow-100 dark:bg-yellow-900/30 border border-yellow-300 dark:border-yellow-700 rounded-lg shadow-md"
  }
  testMoveBtn.onclick = (e: Event) => if (playerGuesses.find(_.isEmpty).isEmpty) App.decide(game)(Guess(playerGuesses.map(_.get)))

  val activeHoles = (0 until game.target.length)
    .zip(game.target.map(x => hole(None, isActive = true).render))
    .map(p => {
      val el = p._2
      el.onclick = (e: Event) => cycleColor(p)
      el.setAttribute("data-tooltip", "Click to cycle colors")
      el.className = el.className + " tooltip"
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

  def guessCell(turn: Option[Turn]) = div(
    cls := "flex items-center justify-center gap-1 sm:gap-2 p-2 sm:p-3 bg-gray-50 dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700",
    guesses(turn.map(_.guess))
  )
  
  def hintCell(turn: Option[Turn]) = div(
    cls := "flex items-center justify-center gap-1 p-2 sm:p-3 bg-gray-100 dark:bg-gray-700 rounded-lg border border-gray-300 dark:border-gray-600 mr-2 sm:mr-4",
    hints(turn.map(_.hint))
  )
  
  val emptyCell = div(cls := "flex items-center justify-center gap-1 p-2 sm:p-3 bg-gray-100 dark:bg-gray-700 rounded-lg border border-gray-300 dark:border-gray-600 mr-2 sm:mr-4")
  
  val targetCell = div(
    cls := "flex items-center justify-center gap-1 sm:gap-2 p-2 sm:p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg border border-yellow-300 dark:border-yellow-700",
    game.target.map(coloredHole)
  )
  
  val targetRow = div(
    cls := "hidden items-center justify-center gap-4 p-4 mb-4 bg-yellow-100 dark:bg-yellow-900/30 border border-yellow-300 dark:border-yellow-700 rounded-lg shadow-md",
    div(cls := "text-yellow-800 dark:text-yellow-200 font-semibold", "🎯 Target:"), 
    targetCell
  ).render
  
  def emptyHole = div(
    cls := "w-6 h-6 sm:w-8 sm:h-8 rounded-full border-2 border-gray-300 dark:border-gray-600 bg-gray-100 dark:bg-gray-800 shadow-inner"
  )
  
  def coloredHole(color: Color) = div(
    cls := "w-6 h-6 sm:w-8 sm:h-8 rounded-full border-2 border-gray-400 dark:border-gray-500 shadow-lg",
    style := s"background-color: ${color.rgbToCssString}"
  )
  
  def hole(color: Option[Color], isActive: Boolean = false) = {
    val baseClasses = if (isActive) {
      "w-6 h-6 sm:w-8 sm:h-8 rounded-full border-2 cursor-pointer hole transition-all duration-200 hover:border-blue-400 dark:hover:border-blue-500"
    } else {
      "w-6 h-6 sm:w-8 sm:h-8 rounded-full border-2 shadow-lg"
    }
    
    if (color.isEmpty) {
      div(
        cls := s"$baseClasses border-gray-300 dark:border-gray-600 bg-gray-100 dark:bg-gray-800 shadow-inner" + 
               (if (isActive) " animate-highlight-holes" else "")
      )
    } else {
      div(
        cls := s"$baseClasses border-gray-400 dark:border-gray-500",
        style := s"background-color: ${color.get.rgbToCssString}"
      )
    }
  }

  def render = div(
    cls := "bg-white dark:bg-gray-900 rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-700",
    gameBox
  ).render

  def gameBox = div(
    cls := "flex flex-col",
    gameBoxTitle, 
    targetRow, 
    div(cls := "px-3 sm:px-6 pb-3 sm:pb-6", rows)
  )
  
  def gameName = h1(
    cls := "text-xl sm:text-2xl md:text-3xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 dark:from-blue-400 dark:to-purple-400 bg-clip-text text-transparent text-center",
    span(cls := "hidden sm:inline", "🧩 MasterMind "),
    span(cls := "sm:hidden", "🧩 MasterMind"),
    a(
      href := "http://bit.ly/1VnFT8U", 
      target := "_blank",
      cls := "text-xs sm:text-sm text-blue-500 dark:text-blue-400 hover:text-blue-600 dark:hover:text-blue-300 underline font-normal ml-1",
      "[wiki]"
    )
  )
  
  def gameBoxTitle = div(
    cls := "flex flex-col items-center justify-center p-3 sm:p-6 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-700 border-b border-gray-200 dark:border-gray-600",
    gameName, 
    gameBoxTitleButtons
  )
  
  def gameBoxTitleButtons = div(
    cls := "flex gap-2 sm:gap-3 mt-3 sm:mt-4",
    startNewGameBtn, 
    testMoveBtn, 
    showTargetBtn
  )

  def rows = {
    val gameRows = (0 until game.maxTurns)
      .map(Option.apply)
      .zipAll(game.steps.reverse.map(Option.apply), None, None)
      .map(p =>
        if (p._1.get == game.steps.length) activeRow(p._2)
        else if (p._1.get < game.steps.length) completedRow(p._2)
        else emptyRow(p._2)
      ).reverse
    
    div(cls := "space-y-2 sm:space-y-3", gameRows)
  }

  def activeRow(turn: Option[Turn]) = {
    val rowClasses = if (game.hasWon) {
      "flex items-center justify-between p-2 sm:p-4 bg-green-100 dark:bg-green-900/30 border-2 border-green-400 dark:border-green-600 rounded-xl shadow-lg"
    } else if (game.hasTurnsExhausted) {
      "flex items-center justify-between p-2 sm:p-4 bg-red-100 dark:bg-red-900/30 border-2 border-red-400 dark:border-red-600 rounded-xl shadow-lg opacity-60"
    } else {
      "flex items-center justify-between p-2 sm:p-4 bg-blue-100 dark:bg-blue-900/30 border-2 border-blue-400 dark:border-blue-600 rounded-xl shadow-lg animate-pulse-border"
    }
    
    div(
      cls := rowClasses,
      hintCell(turn), 
      testMoveBtn,
      div(
        cls := "flex items-center justify-center gap-1 sm:gap-2 p-2 sm:p-3 bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700",
        activeHoles.map(x => x._2)
      )
    )
  }

  def completedRow(turn: Option[Turn]) = div(
    cls := "flex items-center justify-between p-2 sm:p-4 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl shadow-sm",
    hintCell(turn), 
    guessCell(turn)
  )

  def emptyRow(turn: Option[Turn]) = div(
    cls := "flex items-center justify-between p-2 sm:p-4 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl shadow-sm opacity-50",
    hintCell(turn), 
    guessCell(turn)
  )

  def guesses(guess: Option[Guess]) =
    if (guess.isEmpty) game.target.map(x => hole(None))
    else game.target.map(Option.apply)
      .zipAll(guess.get.colors.map(Option.apply), None, None)
      .map(p => hole(p._2))

  def hints(hint: Option[Hint]) =
    if (hint.isEmpty) game.target.map(x => None).map(hole(_))
    else game.target.map(Option.apply)
      .zipAll(hintColors(hint.get), None, None)
      .map(p => hole(p._2))

  def hintColors(hint: Hint): List[Option[Color]] =
    hint.aligned.map(x => Some(App.setup.alignedHintColor)) union hint.notAligned.map(x => Some(App.setup.notAlignedHintColor))

}

