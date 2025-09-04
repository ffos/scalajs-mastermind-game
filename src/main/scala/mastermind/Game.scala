package mastermind

import scala.util.Random
import java.util.Date
import java.util.UUID

/** color representation **/
case class Color(val name: String, val rgb: (Int, Int, Int)) {
  def rgbToCssString = s"rgb(${rgb._1}, ${rgb._2}, ${rgb._3})"
}

/** the hint to be shown after a move based on evaluation **/
case class Hint(val aligned: List[Color], val notAligned: List[Color])

/** representation of the player's guess/color-choices **/
case class Guess(val colors: List[Color]) {
  def matches(target: List[Color]): Boolean =
    if (target.length != colors.length) false
    else target.zip(colors).dropWhile(pair => pair._1 == pair._2).isEmpty

  def getHint(target: List[Color]): Hint = {
    val aligned = target.zip(colors).filter(pair => pair._1 == pair._2).map(pair => pair._2)
    val notAligned = target.diff(aligned).intersect(colors)
    Hint(aligned, notAligned)
  }
}

/** representation of a played move - guess and the resulting hint **/
case class Turn(val guess: Guess, val hint: Hint)

/** The game **/
case class Game(val target: List[Color], val maxTurns: Int, val steps: List[Turn]) {
  def hasTurnsExhausted = steps.length == maxTurns
  def hasWon = if (steps.isEmpty) false else steps.head.guess.matches(target)
  def guess(choices: Guess): Either[String, Game] =
    if (hasWon) Left("Game already won")
    else if (hasTurnsExhausted) Left("No more turns left")
    else Right(Game(target, maxTurns, Turn(choices, choices.getHint(target)) :: steps))
}

class Setup {
  /** palette of non-hint colors with modern, vibrant colors **/
  val palette: List[Color] =
    List(
      Color("Crimson", (220, 38, 127)),
      Color("Electric Blue", (59, 130, 246)),
      Color("Emerald", (34, 197, 94)),
      Color("Amber", (245, 158, 11)),
      Color("Purple", (147, 51, 234)),
      Color("Orange", (249, 115, 22))
    )
  val alignedHintColor = Color("Correct", (15, 23, 42))      // Dark slate for correct position
  val notAlignedHintColor = Color("Present", (148, 163, 184)) // Light slate for wrong position

  def newGame: Game = {
    val rand = new Random(new Date().getTime)
    val colors = rand.shuffle(palette).take(4)
    Game(colors, 6, List())
  }
}
