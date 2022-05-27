<script>
	export let value, cb, player;
</script>

<div on:click={cb} style="color:{player === value ? 'blue' : 'red'}">
    {value || ""}
</div>

<style>
	div {
		width: 100px;
        height: 100px;
        border: 2px solid yellow;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        font-size: 48px;
        font-weight: 600;
	}
</style>