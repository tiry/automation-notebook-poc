
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

simulator.waitForAsync();