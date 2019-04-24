function run(input, params) {

    var domain = Repository.GetDocument(null, {
        "value" : "/default-domain"
    });
    
    Console.log("retrieved doc " + domain.id);
    return domain;    
}
run()