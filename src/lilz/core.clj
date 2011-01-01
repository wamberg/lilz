(set! *warn-on-reflection* true)
(ns lilz.core
  (:gen-class)
  (:require [lilz.brain] [clojure.contrib.logging :as log]))
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

  ; the tetris program is running, we have to start our bot to begin playing
  (lilz.brain/start game)
)
