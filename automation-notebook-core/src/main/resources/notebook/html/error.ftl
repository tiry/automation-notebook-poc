<span style="color:red"> Unable to execute Automation Script:</span>

<pre>  
${e.getClass().getSimpleName()} : ${e.getMessage()}

 caused by  ${e.getCause().getClass().getSimpleName()}:  ${e.getCause().getMessage()}    


 <#list e.getStackTrace() as trace>
    ${trace.toString()}
 </#list>
 
</pre>

