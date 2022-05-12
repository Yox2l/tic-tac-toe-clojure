; https://tutswiki.com/rest-api-in-clojure/
; lein ring server
; https://github.com/ring-clojure/ring-json
(use 'clojure.java.io)

(ns tic-tac-toe-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [tic-tac-toe-server.gamelogic :as gamelogic]
            [tic-tac-toe-server.in-memory-db :as db]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.util.response :refer [response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/start_new_game" 
    [] 
    (let [game_id (.toString (java.util.UUID/randomUUID))]
      (db/create-game game_id 
                      {:board (gamelogic/get_initial_board) 
                       :player (gamelogic/get_player_sign)
                       :start-time (System/currentTimeMillis)})
      (response {:game_id game_id})))
  (GET "/get_game_update/:game_id"
    [game_id]
    (let [{:keys [board player]}  (db/get-game-data game_id)] 
      (response {:board board
                 :player player
                 :turn (gamelogic/get_current_player board)
                 :winner (gamelogic/get_winner board)})))

  (POST "/move" {:keys [params]}
    (let [{:keys [x y game_id]} params]
      (let [{:keys [board player]}  (db/get-game-data game_id)]
        (prn x y)
        (if (not= (gamelogic/get_current_player board) player)
          (response {:status "ERROR" :error "NOT YOUR TURN"})
          (if (gamelogic/is_game_ended board)
            (response {:status "ERROR" :error "GAME ENDED"})
            (if (not (gamelogic/is_cord_empty board [x y]))
              (response {:status "ERROR" :error "SQUARE NOT EMPTY"})
              (do 
                (db/update-game-data-field game_id :board (gamelogic/move board player [x y])) 
                (response {:status "OK"}))))))))

  (route/not-found "Not Found"))

;; (def app
;;   (wrap-json-body (wrap-json-response (wrap-defaults app-routes api-defaults))))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-params)
      (wrap-json-response)))

(alter-var-root #'*out* (constantly *out*))

(def TTL (* 5 60 1000)) ; 5 minutes ttl

(defn play-single-game
  [game_id {:keys [board player start-time]}]
  (print game_id)
  (if (< (+ start-time TTL) (System/currentTimeMillis))
    (do (prn "delete game" game_id)
        (db/delete-game game_id))
    (if (and (not= (gamelogic/get_current_player board) player) (not (gamelogic/is_game_ended board)))
      (db/update-game-data-field game_id :board (gamelogic/move board (gamelogic/get_current_player board) (gamelogic/get_random_move board)))
      nil)))

(defn cpu_play_loop []
  ;; move to db and send function that get full game-state
  (db/call-func-on-every-game play-single-game)
  (Thread/sleep (+ 250 (rand-int 1500)))
  (future (cpu_play_loop)))

(future (cpu_play_loop))

