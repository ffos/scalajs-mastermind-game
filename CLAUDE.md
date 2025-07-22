# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Building and Running
- `sbt fastOptJS` - Compile to JavaScript for development
- `sbt fullOptJS` - Compile to optimized JavaScript for production
- `sbt ~fastOptJS` - Watch mode for continuous compilation during development
- Open `app/index-dev.html` in a browser to run the game after compilation

### Testing
- `sbt test` - Run all tests
- `sbt "testOnly mastermind.GameTest"` - Run specific test suite
- `sbt "testOnly mastermind.GameTest -- -z \"specific test name\""` - Run specific test

### Other Commands
- `sbt clean` - Clean build artifacts
- `sbt compile` - Compile without generating JavaScript

## Project Architecture

This is a Scala.js implementation of the Mastermind board game with a clean separation between game logic and UI.

### Core Components

**Game Logic (`src/main/scala/mastermind/Game.scala`)**
- `Game` - Immutable game state containing target colors, turn history, and max turns
- `Color` - Enumeration of available colors with RGB values
- `Guess` - Player's color choices for a turn
- `Hint` - Feedback showing aligned (black pegs) and not-aligned (white pegs) colors
- `Turn` - Combination of guess and resulting hint
- `Setup` - Configuration for initializing games with color palette

**UI Layer (`src/main/scala/mastermind/App.scala`)**
- `App` - Main entry point that manages game lifecycle and DOM initialization
- `Gui` - Renders the game board using Scalatags and handles user interactions
- Click-based color selection cycles through available colors
- Visual feedback with black pegs (correct position) and white pegs (correct color, wrong position)

### Key Design Patterns
- Immutable data structures throughout game logic
- Functional programming style with pure functions
- Event-driven UI updates
- Separation of concerns between domain logic and presentation

### Testing Approach
Tests use ScalaTest with FlatSpec style and JSDOM for DOM testing. Game logic has comprehensive unit test coverage in `GameTest.scala`.

### Styling
The project now uses Tailwind CSS (via CDN) for modern styling. The Tailwind version is available in `app/index-tailwind.html` while the original CSS version remains in `app/index-dev.html`.