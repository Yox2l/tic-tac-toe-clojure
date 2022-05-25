import fetch from "node-fetch";

const URL = "https://89tg3i7ftd.execute-api.eu-west-1.amazonaws.com/default/tic-tac-toe-clojure"

async function genericApiCall(data) {
    const response = await fetch(`${URL}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    return await response.json();
}

async function startNewGame() {
    return genericApiCall({action: "start_new_game"})
    // const response = await fetch(`${URL}`, {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify({action: "start_new_game"})
    // })
    // return await response.json();
    // => {"game_id": "1234567890"}
}

async function getGameUpdate(game_id) {
    return genericApiCall({action: "get_game_update", game_id})
    const response = await fetch(`${URL}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({action: "get_game_update", game_id})
    })
    return await response.json()
    // => {
    //     "board": [["X",null,null],["O",null,null],["X",null,null]],
    //     "player": "X"|"O", 
    //     "turn": "X"|"O",
    //     "winner": "X"|"O"|"TIE",
    // }
}

async function move(game_id, x, y) {
    return genericApiCall({action: "move", x, y, game_id})
    const response = await fetch(`${URL}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({action: "move", x, y, game_id})
    })
    return await response.json()
    // => {"status": "OK"|"ERROR", "error": "ERROR_MSG"}
}

// console.log(await startNewGame())
// console.log(await move("074bdbfe-08b9-466a-98ed-f8ba678cb61a", 1, 1))
// console.log(await getGameUpdate("074bdbfe-08b9-466a-98ed-f8ba678cb61a"))
// "074bdbfe-08b9-466a-98ed-f8ba678cb61a"
// "074bdbfe-08b9-466a-98ed-f8ba678cb61a"
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
            // return
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