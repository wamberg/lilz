; This code controls simple output of the robot such as keypresses
(ns lilz.actuator
  (:require [clojure.contrib.logging :as log]))
(import '(java.awt Robot)
        '(java.awt.event KeyEvent InputEvent))

(defn init-game [^java.awt.Robot bot]
  (doto bot
    (.mouseMove 267 273) ; TODO: error prone for other screen resolutions
    (.mousePress java.awt.event.InputEvent/BUTTON1_MASK)
    (.mouseRelease java.awt.event.InputEvent/BUTTON1_MASK)
    (.delay 1000))
  (log/info "Game initialized"))

(defn move-left [^java.awt.Robot bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_LEFT)
  (.keyRelease bot java.awt.event.KeyEvent/VK_LEFT)
  (log/debug "Key Press: LEFT"))
(defn move-right [^java.awt.Robot bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_RIGHT)
  (.keyRelease bot java.awt.event.KeyEvent/VK_RIGHT)
  (log/debug "Key Press: RIGHT"))
(defn move-down [^java.awt.Robot bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_DOWN)
  (.keyRelease bot java.awt.event.KeyEvent/VK_DOWN)
  (log/debug "Key Press: DOWN"))
(defn rotate-clockwise [^java.awt.Robot bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_SHIFT)
  (.keyPress bot java.awt.event.KeyEvent/VK_UP)
  (.keyRelease bot java.awt.event.KeyEvent/VK_UP)
  (.keyRelease bot java.awt.event.KeyEvent/VK_SHIFT)
  (log/debug "Key Press: Clockwise"))
(defn rotate-counter-clockwise [^java.awt.Robot bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_UP)
  (.keyRelease bot java.awt.event.KeyEvent/VK_UP)
  (log/debug "Key Press: Counter-Clockwise"))

(defn test-move [^java.awt.Robot bot]
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
  (.delay bot 1000)
)
