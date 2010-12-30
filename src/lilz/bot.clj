; This code controls simple output of the robot such as keypresses
(ns lilz.bot
  (:require [clojure.contrib.logging :as log]))
(import '(java.awt Robot)
        '(java.awt.event KeyEvent InputEvent))

(def robot (new java.awt.Robot))

(defn init-game [bot]
  (doto bot
    (.mouseMove 267 273) ; TODO: error prone for other screen resolutions
    (.mousePress java.awt.event.InputEvent/BUTTON1_MASK)
    (.mouseRelease java.awt.event.InputEvent/BUTTON1_MASK))
  (log/info "Game initialized"))

(defn move-left [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_LEFT)
  (.keyRelease bot java.awt.event.KeyEvent/VK_LEFT)
  (log/info "Key Press: LEFT"))
(defn move-right [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_RIGHT)
  (.keyRelease bot java.awt.event.KeyEvent/VK_RIGHT)
  (log/info "Key Press: RIGHT"))
(defn move-down [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_DOWN)
  (.keyRelease bot java.awt.event.KeyEvent/VK_DOWN)
  (log/info "Key Press: DOWN"))
(defn rotate-clockwise [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_SHIFT)
  (.keyPress bot java.awt.event.KeyEvent/VK_UP)
  (.keyRelease bot java.awt.event.KeyEvent/VK_UP)
  (.keyRelease bot java.awt.event.KeyEvent/VK_SHIFT)
  (log/info "Key Press: Clockwise"))
(defn rotate-counter-clockwise [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_UP)
  (.keyRelease bot java.awt.event.KeyEvent/VK_UP)
  (log/info "Key Press: Counter-Clockwise"))

(defn play-loop [bot]
  (while true
    ; test our agent
    (doseq [i (range 0 (rand-nth (range 0 5)))]
      (move-left bot))
    (doseq [i (range 0 (rand-nth (range 0 2)))]
      (rotate-clockwise bot))
    (doseq [i (range 0 (rand-nth (range 0 2)))]
      (rotate-counter-clockwise bot))
    (doseq [i (range 0 (rand-nth (range 0 5)))]
      (move-left bot))
    (move-down bot)
  )
)
