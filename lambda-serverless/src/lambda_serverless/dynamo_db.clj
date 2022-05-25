(ns lambda-serverless.dynamo-db
    (:require [taoensso.faraday :as far]))

(def client-opts
    {:endpoint "http://dynamodb.eu-west-1.amazonaws.com"})

(defn update-game
    [game-id game-data]
    (far/put-item client-opts :tic-tac-toe {:id game-id :data game-data}))

(defn get-game
    [game-id]
    (println "get-game game-id" game-id)
    (get (far/get-item client-opts :tic-tac-toe {:id game-id}) :data))
