(ns lilz.core
  (:gen-class)
  (:require [lilz.bot]))
(defn -main [& args]
  (import '(net.percederberg.tetris Game)
          '(java.awt Frame)
          '(java.awt.event WindowAdapter WindowEvent))
  (def frame (new java.awt.Frame "Tetris"))
  (def game (new net.percederberg.tetris.Game))

  (doto frame
    (.add (.getComponent game))
    (.pack)
    (.addWindowListener (proxy [java.awt.event.WindowAdapter] []
                          (windowClosing [_] (System/exit 0))))
    (.show)
  )
  (lilz.bot/init-game lilz.bot/robot)
  (lilz.bot/play-loop lilz.bot/robot)
)
