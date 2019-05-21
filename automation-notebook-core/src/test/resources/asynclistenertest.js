
Console.log("starting listener testing");

simulator.registerListener('documentCreated','Scripting.Listener',false);

var root = Repository.GetDocument(null, {
        "value" : "/"
    });

var newDoc = Document.Create(root, {
        "type" : "File",
        "name" : "newDoc",
        "properties" : {
            "dc:title" : "whathever"
        }
    });

Console.log("created test Document " + newDoc.id);

simulator.waitForAsync();

Console.log("end of listener testing");