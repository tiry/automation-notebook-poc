@Operation(id = "Scripting.Listener")
function run(input, params) {

    Console.log("from js listener");
    Console.asJson(input);
    Console.asJson(params);

    Debug.dumpCtx();
}