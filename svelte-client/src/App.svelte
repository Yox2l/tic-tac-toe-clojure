<script>
	import Cell from './Cell.svelte';
	import AnimatedSpan from './AnimatedSpan.svelte';
	let game_id;
	let game_data = {}
	let error;
	let show_intro = true;
	let step = 0;
	let optimsticMove = null;
	let timer = null;
	let gameLoading = false;
	// tic-tac-toe-api.route42.co.il
	const URL = "https://9kyk6iii2a.execute-api.eu-west-1.amazonaws.com/tic-tac-toe-clojure"
	const ANIM_TIME = 60
	const MAX_ANIM_STEP = 125
	async function sleep(seconds) {
		return new Promise((resolve) => setTimeout(resolve, seconds))
	}
	async function genericApiCall(data) {
		const response = await fetch(URL, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(data)
		})
		return await response.json();
	}
	async function startMewGame() {
		game_data = {};
		show_intro = true;
		gameLoading = true
		// await sleep(500)
		const data = await genericApiCall({action: "start_new_game"})
		show_intro = false;
		gameLoading = false
		game_id = data.game_id
	}
	async function updateGameBoard() {
		if(!game_id) return;
		game_data = await genericApiCall({action: "get_game_update", game_id})
		performOptimsticMove()
		if (game_data.winner) {
			game_id = null;
		}
	}
	function performOptimsticMove() {
		if(!optimsticMove) return;
		const {x, y} = optimsticMove
		game_data.board[x][y] = game_data.player
	}
	async function move(x,y) {
		optimsticMove = {x, y}
		performOptimsticMove()
		const data = await genericApiCall({action: "move", x, y, game_id})
		optimsticMove = null
		error = data.error
		if(error) {
			// we want the user to only see the errors for 1.5 seconds
			clearTimeout(timer)
			timer = setTimeout(() => {
				error = null
			}, 1500)
		}
	}
	function nextStep() {
		step = (step + 1) % MAX_ANIM_STEP
		show_intro && setTimeout(nextStep, ANIM_TIME)
	}
	setTimeout(nextStep, ANIM_TIME)
	setInterval(updateGameBoard, 750)
	genericApiCall({action: "warm up!"})
</script>

<main>
	{#if !game_id}
		<div class="start-new-game">
			{#if show_intro}
				<div class="intro">
					<h3>Route42 Present:</h3>
					<h2> TIC-TAC-TOE</h2>
					<div class="ascii_board">
						<p>&nbsp;|&nbsp;|<AnimatedSpan value="X" step={step} enterAtStep={60} /></p>
						<p>-----</p>
						<p>&nbsp;|<AnimatedSpan value="X" step={step} enterAtStep={15} />|&nbsp;</p>
						<p>-----</p>
						<p><AnimatedSpan value="X" step={step} enterAtStep={90} />|<AnimatedSpan value="O" step={step} enterAtStep={72} />|<AnimatedSpan value="O" step={step} enterAtStep={28} /> </p>
					</div>
					<p> A <span style="color: blue">MAN</span> AGAINST </p>
					<p> A <span style="color: red">MACHINE</span> ADVENTURE </p>
				</div>
			{/if}
			{#if !gameLoading}
				<h1 on:click={startMewGame}> Start New Game </h1>
			{:else}
				<h1 on:click={startMewGame} style="color: yellow"> Loading... </h1>
			{/if}
		</div>
	{/if}
	{#if game_data.board}
		<h2 style="margin-top: 32px; color:cyan"> TIC-TAC-TOE </h2>
		<div class="board">
			{#each game_data.board as row, i}
				<div class="row">
					{#each row as cell, j}
						<Cell value={cell} player={game_data.player} cb={() => move(i, j)}/>
					{/each}
				</div>
			{/each}
		</div>
		<p style="margin-top: 32px"> 
			You play:<span style="color: blue">{game_data.player}</span> 
			turn:<span style="color: {game_data.turn === game_data.player ? "blue": "red"}">{game_data.turn}</span>
		</p>
	{/if}
	{#if error}
		<h4> {error} </h4>
	{/if}
	{#if game_data.winner}
	<h2> The winner is:<strong style="color: {game_data.winner === game_data.player ? "blue": "red"}">{game_data.winner}</strong> </h2>
	{/if}
	<p class="blog">©Yotam Lichter 2022 <a href="https://route42.co.il/clojure-part-1-logic" target="_blank">route42</a></p>
</main>

<style>
	.row {
		display: flex;
	}
	main {
		text-align: center;
		margin: 0 auto;
		margin-top: 120px;
		width: 480px;
		height: 640px;
		border: 1px solid black;
		display: flex;
		align-items: center;
		flex-direction: column;
		color: white;
		background-color: black;
		position: relative;
	}
	.blog {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 16px;
		color: black;
		position: absolute;
		bottom: -44px;
		left: 0px;
	}

	.start-new-game {
		width: 480px;
		height: 640px;
		position: absolute;
		top: 0px;
		left: 0px;
	}

	.intro {
		width: 480px;
		height: 480px;
		position: absolute;
		top: 0px;
		left: 0px;
	}

	.intro h3 {
		color: magenta;
		margin-top: 64px;
	}

	.intro h2 {
		margin-top: 64px;
		color: yellow;
	}
	.ascii_board {
		margin: 80px;
		color: cyan;		
	}

	.ascii_board p {
		font-size: 32px;
		margin-top: -44px;
	}

	@keyframes flickerAnimation {
		0%   { opacity:0; }
		49.9%  { opacity:0; }
		50%  { opacity:1; }
		100% { opacity:1; }
	}

	.start-new-game h1 {
		cursor: pointer;
		margin-top: 560px;
		font-size: 24px;
		color: magenta;
		animation: flickerAnimation 1.5s infinite;
	}

	.board {
		margin-top: 32px;
	}

	@keyframes errorFlickerAnimation {
		0%   { opacity:0; }
		20%  { opacity:1; }
		80%  { opacity:1; }
		100% { opacity:0; }
	}

	h4 {
		color: red;
		animation: errorFlickerAnimation 1.5s infinite;
		font-size: 24px;
		padding: 20px;
	}
</style>