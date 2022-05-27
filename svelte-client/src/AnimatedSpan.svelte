<script>
	export let value;
    export let enterAtStep;
    export let step;
</script>
{#if step > enterAtStep}
    <span> {value} </span>
{:else}
    <span>&nbsp;</span>
{/if}
