function run(input, params) {

    var docs = Document.Query(null, {"query" : "select * from Document"});
    
    return docs;    
}
run()