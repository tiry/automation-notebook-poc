@Operation(id = "Scripting.Listener")
function run(input, params) {

    Console.log("from js listener");
    Console.logAsJson(input);
    Console.logAsJson(params);

    Debug.dumpCtx();
}