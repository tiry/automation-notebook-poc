@Operation(id = "Scripting.GetRoot")
function run(input, params) {

    var domain = Repository.GetDocument(null, {
        "value" : "/default-domain"
    });
    return domain;    
}