@Operation(id = "Scripting.GetRoot")
function run(input, params) {

    var root = Repository.GetDocument(null, {
        "value" : "/"
    });
    return root;    
}