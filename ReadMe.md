### About

The goal of this repository is to build a POC of what a Jupyther Notebook for Automation could be.

### Why

Jupyter Notebook are a nice way to test and build a ML Pipeline:

 - prepare the data
 - build the model
 - test the model

In a sense the goal is to try a similar approach with an Automation Scripting:

 - prepare the data that is needed in the repository so that the Operation can run
 - code the operation using scripting
 - test the operation

### POC Architecture:

The current POC uses a [wrapper around the default python Kernel](https://jupyter-client.readthedocs.io/en/stable/wrapperkernels.html) rather than building a complete custom kernel.
The idea is that the kernel just acts as a proxy to the Nuxeo Server.

The Nuxeo Server is exposing a custom operation that takes care of:

 - compile and execute the code
 - register the operation is neeed
 - return an HTML rendered result

In order to leverage Notebook interface, all Nuxeo output are for now done using HTML that is server side rendered by Nuxeo using freemarker.



    NoteBook ==> ZeroMQ ==> nuxeokernel ==> nuxeo python client ==> AutomationKernelExecutor

### What does it look like?

See [this sample](samples/TestingAutomation.ipynb).


### Open questions

 - Implement the `ContentsManager` interface on Nuxeo?
    - see [pgcontents](https://github.com/quantopian/pgcontents) 
 - State Management: keep transient context between calls
 - Leverage existing client side extensions
 	- https://jupyter-contrib-nbextensions.readthedocs.io/en/latest/
 - 