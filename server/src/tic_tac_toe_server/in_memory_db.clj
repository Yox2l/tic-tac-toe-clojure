(ns tic-tac-toe-server.in-memory-db)

(def games-db (atom {}))

(defn create-game
  [game-id game-data]
  (swap! games-db assoc game-id game-data))

(defn get-game-data
  [game-id]
  (get @games-db game-id))

(defn delete-game
  [game-id]
  (swap! games-db dissoc game-id))

(defn update-game-data-field
  [game-id k v]
  (swap! games-db assoc game-id (update (get @games-db game-id) k (constantly v))))

(defn call-func-on-every-game
  [call-game-func]
  (doseq [[game-id game-data] @games-db] (call-game-func game-id game-data)))

; *** TEST ***
;; (create-game "ID_1" {:data 20 :time 0})
;; (create-game "ID_2" {:data 10 :time 20})
;; @games-db ; => {"ID_1" {:data 20, :time 0}, "ID_2" {:data 10, :time 20}}
;; (get-game-data "ID_1") ; => {:data 20, :time 0}
;; (update-game-data-field "ID_1" :data 100)
;; (get-game-data "ID_1") ; => {:data 100, :time 0}

;; (defn print-game-id-and-data 
;;   [game-id {:keys [data time]}]
;;   (print game-id data time ","))

;; (call-func-on-every-game print-game-id-and-data)
;; ;; => ID_1 100 0 ,ID_2 10 20 ,
;; (delete-game "ID_1") ; =>
;; (call-func-on-every-game print-game-id-and-data)
;; ; => ID_2 10 20 ,