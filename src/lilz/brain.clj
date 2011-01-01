(ns lilz.brain
  (:require [lilz.actuator] [clojure.contrib.logging :as log]))
(import '(net.percederberg.tetris Game)
        '(java.awt Robot))
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

(defn start [^net.percederberg.tetris.Game core-game]
  (let [game core-game
        robot (new java.awt.Robot)]
    (lilz.actuator/init-game robot) ; start the game
    (while true ; this is the outer game loop to restart processing after pauses
      (while (not= (game-paused? game) true)
        ; analyze potential moves
        ; make a move
        (lilz.actuator/test-move robot)
      )
    )
  )
)
