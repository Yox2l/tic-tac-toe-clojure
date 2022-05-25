(ns lambda-serverless.core
  (:require [uswitch.lambada.core :refer [deflambdafn]]
            [clojure.data.json :as json]
            [taoensso.faraday :as far]
            [clojure.java.io :as io]
            [lambda-serverless.game-runner :as game-runner]))

(defn handle-event
  [event]
  (println "Got the following event: " (pr-str event))
  (let [body (json/read-str (get event "body"))
        action (get body "action")]
    (if (= action "start_new_game")
      (game-runner/start_new_game)
      (if (= action "move")
        (game-runner/move body)
        (if (= action "get_game_update")
          (game-runner/get_game_update body)
          {:status "ERROR NO SUCH ACTION"}))
    )))
  ;; {:status "OK"})

(deflambdafn lambda-serverless.core.handler
  [in out ctx]
  (let [event (json/read (io/reader in))
        res (handle-event event)]
    (with-open [w (io/writer out)]
      (json/write res w))))
