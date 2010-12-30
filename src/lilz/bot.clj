; This code controls simple output of the robot such as keypresses
(ns lilz.bot)
(import '(java.awt Robot)
        '(java.awt.event KeyEvent InputEvent))
(def robot (new java.awt.Robot))

(defn init-game [bot]
  (doto bot
    (.mouseMove 267 273) ; TODO: error prone for other screen resolutions
    (.mousePress java.awt.event.InputEvent/BUTTON1_MASK)
    (.mouseRelease java.awt.event.InputEvent/BUTTON1_MASK)))

(defn move-left [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_LEFT))
(defn move-right [bot]
  (.keyPress bot java.awt.event.KeyEvent/VK_RIGHT))

(defn play-loop [bot]
  (while true
    ; test our agent
    (doseq [i (range 0 100)]
      (move-left bot)
      (.delay bot 1000))
    (doseq [i (range 0 100)]
      (move-right bot)
      (.delay bot 1000))
  )
)
