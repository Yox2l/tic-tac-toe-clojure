(ns lambda-serverless.game-runner
    (:require [lambda-serverless.dynamo-db :as db]
              [lambda-serverless.gamelogic :as gamelogic]))

(defn start_new_game []
    (let [game_id (.toString (java.util.UUID/randomUUID))]
        (db/update-game game_id 
                        {:board (gamelogic/get_initial_board) 
                         :player (gamelogic/get_player_sign)
                         :ttl (/ (+ (System/currentTimeMillis) (* 1000 60 30)) 1000.0)
                         :next-cpu-turn (+ (System/currentTimeMillis) 250 (rand-int 1500))})
        {:game_id game_id}))

(defn get_game_update_2 [request]
    (println "get_game_update request" request)
    (let [game_id (get request "game_id")
          game-data (db/get-game game_id)]
        (println "get_game_update db/get-game" game-data)
        {:status "OK"}))

(defn get_game_update [request]
    (println "get_game_update request" request)
    (let [game_id (get request "game_id")
            game-data (db/get-game game_id)
            {:keys [board player next-cpu-turn]} game-data
            turn (gamelogic/get_current_player board)
            winner (gamelogic/get_winner board)]
        (if (and (nil? winner) (not= turn player) (< next-cpu-turn (System/currentTimeMillis)))
            (db/update-game game_id (assoc game-data :board 
                (gamelogic/move board (gamelogic/get_current_player board) (gamelogic/get_random_move board)))))
        {:board board
            :player player
            :turn turn
            :winner winner}))

(defn move [request]
    (let [game_id (get request "game_id")
          x (get request "x")
          y (get request "y")
          game-data (db/get-game game_id)
          {:keys [board player]} game-data]
          (if (not= (gamelogic/get_current_player board) player)
            {:status "ERROR" :error "NOT YOUR TURN"}
            (if (gamelogic/is_game_ended board)
                {:status "ERROR" :error "GAME ENDED"}
                (if (not (gamelogic/is_cord_empty board [x y]))
                    {:status "ERROR" :error "SQUARE NOT EMPTY"}
                    (do 
                        (db/update-game game_id 
                            (assoc (assoc game-data 
                                :board (gamelogic/move board player [x y])) 
                                :next-cpu-turn (+ (System/currentTimeMillis) 250 (rand-int 1500)))) 
                        {:status "OK"}))))))

