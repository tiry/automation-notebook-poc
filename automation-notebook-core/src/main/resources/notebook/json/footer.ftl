 , "exec_time" : ${t}
 , "asserts": [ 
 <#list asserts as assert>
  { "success" : "${assert.isSuccess()}", "message" : "${assert.message}" }<#if assert?has_next>,</#if>
</#list> ]
 , "logs" : [  
<#list logs as log>
  { "level" : "${log.level}", "message" : "${log.message}" }<#if log?has_next>,</#if>    
</#list> ]
