function run(input, params) {

    var docs = Document.Query(null, {"query" : "select * from Document"});
    
    return docs;    
}
var docs = run()
Console.log("Yo");
Assert.assertTrue(docs.size() > 0, "MyAssert");
docs;
