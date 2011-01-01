(ns lilz.brain
  (:require [lilz.actuator] [clojure.contrib.logging :as log]))
(import '(net.percederberg.tetris Game)
        '(java.awt Robot))
; game functions
(defn current-figure [^net.percederberg.tetris.Game game]
  (let [figure (.getDeclaredField net.percederberg.tetris.Game "figure")]
    (.setAccessible figure true)
    (.get figure game))
)
(defn current-thread [^net.percederberg.tetris.Game game]
  (let [thread-field (.getDeclaredField net.percederberg.tetris.Game "thread")]
    (.setAccessible thread-field true)
    (.get thread-field game)
  )
)
(defn game-paused? [^net.percederberg.tetris.Game game]
  (let [thread (current-thread game)
        is-paused-method (.getDeclaredMethod net.percederberg.tetris.Game$GameThread "isPaused" nil)]
    (.setAccessible is-paused-method true)
    (.invoke is-paused-method thread nil)
  )
)

; game board functions
(defn current-board [^net.percederberg.tetris.Game game]
  (let [board-field (.getDeclaredField net.percederberg.tetris.Game "board")]
    (.setAccessible board-field true)
    (.get board-field game)
  )
)
(defn create-board-representation [^net.percederberg.tetris.Game game]
  (let [board (current-board game)]
    ; cycle through rows
    ; cycle through columns
    ; build a two-dimensional list of true/false values if square is occupied
    (nil? nil)
  )
)
(defn is-row-empty? [row]
  "If 'row' list contains all false values, return true"
  (nil? nil)
)
(defn is-row-full? [row]
  "If 'row' list contains all true values, return true"
  (nil? nil)
)
(defn score-row [row row-completion-modifier]
  " Add points for each true value in 'row'.  If row is complete, multiply
    by 'row-completion-modifier'.  Return the point tally for the row."
  (nil? nil)
)
(defn score-figure [board figure x]
  " Place a 'figure' on the 'board' at a given 'x' coordinate
      board - our representation of the board with current pieces
      figure - representation of the piece we're placing
      x - 0-based position where we want to lay the figure"
  (nil? nil)
)
(defn determine-best-move-for-figure [board figure]
  " Tally point totals for all possible moves of a 'figure' on our 'board'.
    Return a list: (x-coordinate clockwise-rotations counter-clockwise-rotations)
      board - our representation of the board with current pieces
      figure - representation of the piece we're placing"
  (nil? nil)
)

; play loop
(defn start [^net.percederberg.tetris.Game core-game]
  (let [game core-game
        robot (new java.awt.Robot)]
    (lilz.actuator/init-game robot) ; start the game
    (while true ; this is the outer game loop to restart processing after pauses
      (while (not= (game-paused? game) true)
        (def board (create-board-representation game)) ; imagine board
        ; analyze potential moves
        ; make a move
        (lilz.actuator/test-move robot)
      )
    )
  )
)
