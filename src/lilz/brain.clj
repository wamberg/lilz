(ns lilz.brain
  (:require [lilz.actuator] [clojure.contrib.logging :as log][clojure.contrib.str-utils]))
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
(defn can-move-to [^net.percederberg.tetris.Figure figure x y orientation]
  " Made net.percederberg.tetris.Figure.canMoveTo accessible.
   Note sending a Class[] for parameterTypes on getDeclaredMethod was killing me.
   The resulting solution seems more hackish than normal."
  (let [can-move-to-method (first (filter (fn [x] (= "canMoveTo" (.getName x))) (.getDeclaredMethods net.percederberg.tetris.Figure)))]
    (log/debug (str "can-move-to: " (clojure.contrib.str-utils/str-join " " [can-move-to-method figure x y orientation])))
    (.setAccessible can-move-to-method true)
    (.invoke can-move-to-method figure (object-array [x y orientation]))
  )
)
(defn get-relative-x [^net.percederberg.tetris.Figure figure square orientation]
  " Made net.percederberg.tetris.Figure.getRelativeX accessible.
   Note sending a Class[] for parameterTypes on getDeclaredMethod was killing me.
   The resulting solution seems more hackish than normal."
  (let [get-relative-x-method (first (filter (fn [x] (= "getRelativeX" (.getName x))) (.getDeclaredMethods net.percederberg.tetris.Figure)))]
    (log/debug (str "get-relative-x: " (clojure.contrib.str-utils/str-join " " [square orientation])))
    (.setAccessible get-relative-x-method true)
    (.invoke get-relative-x-method figure (object-array [square orientation]))
  )
)
(defn get-relative-y [^net.percederberg.tetris.Figure figure square orientation]
  " Made net.percederberg.tetris.Figure.getRelativeY accessible.
   Note sending a Class[] for parameterTypes on getDeclaredMethod was killing me.
   The resulting solution seems more hackish than normal."
  (let [get-relative-y-method (first (filter (fn [x] (= "getRelativeY" (.getName x))) (.getDeclaredMethods net.percederberg.tetris.Figure)))]
    (log/debug (str "get-relative-y: " (clojure.contrib.str-utils/str-join " " [square orientation])))
    (.setAccessible get-relative-y-method true)
    (.invoke get-relative-y-method figure (object-array [square orientation]))
  )
)
(defn figure-current-orientation [^net.percederberg.tetris.Figure figure]
  (let [orientation-field (.getDeclaredField net.percederberg.tetris.Figure "orientation")]
    (.setAccessible orientation-field true)
    (.get orientation-field figure)
  )
)
(defn figure-x-pos [^net.percederberg.tetris.Figure figure]
  (let [x-pos-field (.getDeclaredField net.percederberg.tetris.Figure "xPos")]
    (.setAccessible x-pos-field true)
    (.get x-pos-field figure)
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
    (vec(for [y matrix] 
      (vec (for [x y] (if (not (nil? x)) true nil)))
    ))
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
  (log/debug (str "score-row: " (clojure.contrib.str-utils/str-join " " [row row-completion-modifier max-width])))
  (if (not (is-row-empty? row))
    (if (is-row-full? row)
      ; a full line is worth:
      ; (board-width * 2) * (y-coordinate + 1)
      ; the y-coordinate multiplier encourages filling in lower lines
      (* (+ row-completion-modifier 1) (* max-width 2))
      ; each filled square is worth its row-completion-modifier value
      (reduce + (for [x row :when (not (nil? x))] row-completion-modifier))
    )
    0 ; no points if row is empty
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
(defn print-board [board]
  (doseq [board-row board]
    (println board-row)
  )
)
(defn create-board-with-placed-figure [board figure-coords]
  ;(log/debug (str "create-board-with-placed-figure: " figure-coords))
  (log/debug (str "board before placement: " (print-board board)))
  (vec (for [y (range (count board))] ; create board with placed figure
    (vec(for [x (range (count (first board)))]
      (if (nil? (some #{[x y]} figure-coords))
        (get (get board y) x)
        true
      )
    ))
  ))
)
(defn place-figure-on-board [board ^net.percederberg.tetris.Figure figure x orientation debug?]
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
  (log/debug (str "place-figure-on-board: " (clojure.contrib.str-utils/str-join " " [figure x orientation])))
  (def first-y (first (for [y (range (- (count board) 1) -1 -1) :when (can-move-to figure x y orientation)] y)))
  
  (if-not (nil? first-y)
    ; place figure on board
    (let [figure-coords ; create coordinates of placed figures
            (for [relative-coord (map list ; create relative coordinates for figure
                (for [relative-x (range 0 4)] (get-relative-x figure relative-x orientation))
                (for [relative-y (range 0 4)] (get-relative-y figure relative-y orientation)))]
              (map + [x first-y] relative-coord)
            )]
      ; create board with placed figure and score resulting board
      (log/debug (str "figure-coords: " (print-board figure-coords)))
      (def move-candidate-board (create-board-with-placed-figure board figure-coords))
      (log/debug (str "move-candidate-board: " (clojure.contrib.str-utils/str-join " " [x orientation])))
      (if debug?(log/info (str "board with figure: " (print-board move-candidate-board))))

      (score-board move-candidate-board)
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
  (log/debug (str "determine-best-move-for-figure: " figure))
  (reduce compare-scores
    (for [x (range 0 (count (first board))) orientation (range 0 (max-orientation figure))] ; from 0 to width of board
      (list (place-figure-on-board board figure x orientation false) x orientation)
    )
  )
)

; play loop
(defn start [^net.percederberg.tetris.Game core-game]
  (let [game core-game
        robot (new java.awt.Robot)
        move-delay 100]
    (lilz.actuator/init-game robot) ; start the game
    (while true ; this is the outer game loop to restart processing after pauses
      (while (not= (game-paused? game) true)
        (def board (create-board-representation (current-board game))) ; imagine board
        ; analyze potential moves
        (while (nil? (current-figure game)) ; loop until a figure is attached to the board
          (.delay robot 50)
        )
        (def figure (current-figure game))
        (def best-move (determine-best-move-for-figure board figure))
        ; make a move
        ;(lilz.actuator/test-move robot)
        (let [move (rest best-move) x (first move) orientation (second move)]
          ;(place-figure-on-board board figure x orientation true)
          ; change the figure orientation until we reach optimal
          (while (not= (figure-current-orientation figure) orientation)
            (lilz.actuator/rotate-clockwise robot)
            (.delay robot move-delay)
          )
          ; move the figure left and right until optimal x position is reached
          (while (not= (figure-x-pos figure) x)
            ;(log/info (str "figure-x-pos: " (figure-x-pos figure) " optimal-x: " x))
            (if (> (figure-x-pos figure) x)
              (lilz.actuator/move-left robot)
              (lilz.actuator/move-right robot)
            )
            (.delay robot move-delay)
          )
          (lilz.actuator/move-down robot)
          (.delay robot 700)
        )
      )
    )
  )
)
