(ns lilz.brain
  (:require [lilz.actuator] [clojure.contrib.logging :as log]))
(import '(net.percederberg.tetris Game SquareBoard Figure)
        '(java.awt Robot))

; game functions
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
(defn current-figure [^net.percederberg.tetris.Game game]
  (let [figure (.getDeclaredField net.percederberg.tetris.Game "figure")]
    (.setAccessible figure true)
    (.get figure game))
)
(defn max-orientation [^net.percederberg.tetris.Figure figure]
  "Access protected maxOrientation field."
  (let [max-rotations-field (.getDeclaredField net.percederberg.tetris.Figure "maxOrientation")]
    (.setAccessible max-rotations-field true)
    (.get max-rotations-field figure)
  )
)

; game board functions
(defn current-board [^net.percederberg.tetris.Game game]
  (let [board-field (.getDeclaredField net.percederberg.tetris.Game "board")]
    (.setAccessible board-field true)
    (.get board-field game)
  )
)
(defn board-height [^net.percederberg.tetris.SquareBoard board]
  (.getBoardHeight board)
)
(defn board-width [^net.percederberg.tetris.SquareBoard board]
  (.getBoardWidth board)
)
(defn board-matrix [^net.percederberg.tetris.SquareBoard board]
  " Access the protected board matrix which shows the state of the figures on
    the game board."
  (let [matrix-field (.getDeclaredField net.percederberg.tetris.SquareBoard "matrix")]
    (.setAccessible matrix-field true)
    (.get matrix-field board)
  )
)
(defn create-board-representation [^net.percederberg.tetris.SquareBoard board]
  " Create a simple representation of the figures laid out on 'board'."
  (let [matrix (board-matrix board)]
    (for [y matrix] 
      (for [x y] (if (not (nil? x)) true nil))
    )
  )
)
(defn is-row-empty? [row]
  " If 'row' list contains all false values, return true"
  (every? (fn [x] (not x)) row)
)
(defn is-row-full? [row]
  " If 'row' list contains all true values, return true"
  (every? (fn [x] x) row)
)
(defn score-row [row row-completion-modifier max-width]
  " Add points for each true value in 'row'.  If row is complete, multiply
    by 'row-completion-modifier'.  Return the point tally for the row."
  (if (not (is-row-empty? row))
    (if (is-row-full? row)
      ; a full line is worth:
      ; (board-width * 2) * (y-coordinate + 1)
      ; the y-coordinate multiplier encourages filling in lower lines
      (* (+ row-completion-modifier 1) (* max-width 2))
      ; each filled square is worth its row-completion-modifier value
      (reduce + (for [x row :when (not (nil? x))] row-completion-modifier))
    )
  )
)
(defn score-board [board]
  "Return a point value for a 'board'"
  (reduce +
    (for [y (range 0 (count board))
          :let [row (get board y)
                width (count row)]]
      (score-row row y width)
    )
  )
)
(defn place-figure-on-board [board ^net.percederberg.tetris.Figure figure x orientation]
  " Place a 'figure' on the 'board' at a given 'x' coordinate.  Return the
    board's score with the placed figure.
      board - our representation of the board with current pieces
      figure - representation of the piece we're placing
      x - 0-based position where we want to lay the figure
      orientation - numerical orientation given to Figures in net.percederberg.tetris.Figure"
  ; starting with y at the bottom of the board (max-height) try to place
  ; the figure on every (x, y) going up the board.  On the first (x, y) where
  ; the figure fits, create a simple board representation with the figure
  ; place at (x, y)
  (def first-y (first (for [y (range (- (count board) -1 -1)) :when (.canMoveTo figure x y orientation)] y)))
  
  (if-not (nil? first-y)
    ; place figure on board
    (let [figure-coord ; create coordinates of placed figures
            (for [relative-coords (map list ; create relative coordinates for figure
                (for [relative-x (range 0 4)] (.getRelativeX figure relative-x orientation))
                (for [relative-y (range 0 4)] (.getRelativeY figure relative-y orientation)))]
              (+ '(x first-y) relative-coords)
            )]
      ;create board with placed figure
      ; TODO: loop through existing board copying it except make figure-coord true
      ; score resulting board
    )
    0 ; return zero if figure can't fit on board for this 'x'
  )
)
(defn compare-scores [one two]
  "The score is kept in the first position of the lists 'one' and 'two'."
  (if (> (first one) (first two)) one two)
)
(defn determine-best-move-for-figure [board ^net.percederberg.tetris.Figure figure]
  " Tally point totals for all possible moves of a 'figure' on our 'board'.
    Return a list: (score x-coordinate orientation)
      board - our representation of the board with current pieces
      figure - representation of the piece we're placing"
  (reduce compare-scores
    (for [x (range 0 (count (first board))) orientation (range 0 (max-orientation figure))] ; from 0 to width of board
      (list (place-figure-on-board board figure x orientation) x orientation)
    )
  )
)

; play loop
(defn start [^net.percederberg.tetris.Game core-game]
  (let [game core-game
        robot (new java.awt.Robot)]
    (lilz.actuator/init-game robot) ; start the game
    (while true ; this is the outer game loop to restart processing after pauses
      (while (not= (game-paused? game) true)
        (def board (create-board-representation (current-board game))) ; imagine board
        ; analyze potential moves
        (def best-move (determine-best-move-for-figure board (current-figure game)))
        ; make a move
        (lilz.actuator/test-move robot)
      )
    )
  )
)
