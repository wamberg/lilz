(set! *warn-on-reflection* true)
(ns lilz.core
  (:gen-class)
  (:require [lilz.bot] [clojure.contrib.logging :as log]))
(defn -main [& args]
  (import '(net.percederberg.tetris Game)
          '(java.awt Frame)
          '(java.awt.event WindowAdapter WindowEvent))
  (def frame (new java.awt.Frame "Tetris"))
  (def game (new net.percederberg.tetris.Game))

  (doto ^java.awt.Frame frame
    (.add (.getComponent ^net.percederberg.tetris.Game game))
    (.pack)
    (.addWindowListener (proxy [java.awt.event.WindowAdapter] []
                          (windowClosing [_] (System/exit 0))))
    (.show)
  )
  ; java reflection to allow us to start the game
;  (def handle-start (.getDeclaredMethod net.percederberg.tetris.Game "handleStart" nil))
;  (.setAccessible handle-start true)
;  (.invoke handle-start game nil)

  (lilz.bot/init-game lilz.bot/robot)
  (lilz.bot/play-loop lilz.bot/robot)
)
