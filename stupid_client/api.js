import fetch from "node-fetch";

const URL = "http://127.0.0.1:3030"

async function startNewGame() {
    const response = await fetch(`${URL}/start_new_game`);
    return await response.json();
    // => {"game_id": "1234567890"}
}

async function getGameUpdate(game_id) {
    const response = await fetch(`${URL}/get_game_update/${game_id}`)
    return await response.json();
    // => {
    //     "board": [["X",null,null],["O",null,null],["X",null,null]],
    //     "player": "X"|"O", 
    //     "turn": "X"|"O",
    //     "winner": "X"|"O"|"TIE",
    // }
}

async function move(game_id, x, y) {
    const response = await fetch(`${URL}/move`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({x, y, game_id})
    })
    return await response.json()
    // => {"status": "OK"|"ERROR", "error": "ERROR_MSG"}
}

async function sleep(seconds) {
    return new Promise((resolve) => setTimeout(resolve, seconds))
}

async function play() {
    // STEP 1: start new game
    const {game_id} = await startNewGame()
    console.log(`[+] strating game_id: ${game_id}`)
    while (true) {
        // STEP 2: wait for client turn
        while (true) {
            const {board, player, turn, winner} = await getGameUpdate(game_id)
            console.log(`\n[+] ${player} ${turn} ${winner}`)
            // print board
            board.forEach(row => console.log(row))
            if(winner) {
                console.log(`[+] winner is: ${winner}`)
                return
            }
            if(player === turn) {
                break
            }
            await sleep(750)
        }
        // STEP 3: try to move all option until move succeed 
        for(let i=0;i<9;i++) {
            const {status, error} = await move(game_id, Math.floor(i/3), i%3)
            console.log(`[+] ${status} ${error}`)
            if (status === "OK") {
                break
            }
        }
    }
}

await play()