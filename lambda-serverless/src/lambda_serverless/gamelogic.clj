(ns lambda-serverless.gamelogic)

; get initial board
(defn get_initial_board []
  [[nil,nil,nil], [nil,nil,nil], [nil,nil,nil]])

(defn get_player_sign []
  (rand-nth ["X" "O"]))

; place player move (borad, player, place) => board
(defn move
  [board, player, place]
  (assoc board (place 0) (assoc (board (place 0)) (place 1) player)))

; how won board => x|o|nil

(defn check_winner_cord
  [board, p0, p1, p2]
  (if (and (not (nil? ((board (p0 0)) (p0 1))))
           (= ((board (p0 0)) (p0 1)) ((board (p1 0)) (p1 1)))
           (= ((board (p0 0)) (p0 1)) ((board (p2 0)) (p2 1))))
    ((board (p0 0)) (p0 1)) nil))

(defn check_winner
  [board]
  (or
   (check_winner_cord board, [0 0], [0 1], [0 2])
   (check_winner_cord board, [1 0], [1 1], [1 2])
   (check_winner_cord board, [2 0], [2 1], [2 2])
   (check_winner_cord board, [0 0], [1 0], [2 0])
   (check_winner_cord board, [0 1], [1 1], [2 1])
   (check_winner_cord board, [0 2], [1 2], [2 2])
   (check_winner_cord board, [0 0], [1 1], [2 2])
   (check_winner_cord board, [0 2], [1 1], [2 0])))

; is board full

(defn is_board_full
  [board]
  (not (some nil? (apply concat board))))

(defn is_game_ended [board]
  (println "is_game_ended" board)
  (or (is_board_full board)
      (not (= nil (check_winner board)))))

(defn get_winner [board]
  (or (check_winner board)
      (if (is_board_full board) "TIE" nil)))

; play random move turn (board, player) => place
(defn int_to_cord
  [x]
  [(int (/ x 3)) (mod x 3)])

(defn is_cord_empty
  [board, place]
  (nil? ((board (place 0)) (place 1))))

(defn get_all_posible_moves
  [board]
  (filter (partial is_cord_empty board) (map int_to_cord (range 9))))

(defn get_random_move
  [board]
  (rand-nth (get_all_posible_moves board)))

; how's turn it is
(defn get_current_player
  [board]
  (println "get_current_player" board)
  (if (=
       (get (frequencies (apply concat board)) "X" 0)
       (get (frequencies (apply concat board)) "O" 0))
    "X" "O"))
