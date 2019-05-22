
Console.log("starting listener testing");

Harness.registerListener('documentCreated','Scripting.Listener',false);

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

Harness.waitForAsync();

Console.log("end of listener testing");