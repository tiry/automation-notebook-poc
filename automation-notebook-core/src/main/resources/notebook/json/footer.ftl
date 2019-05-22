 <#setting number_format="computer">
 , "exec_time" : ${t}
 , "asserts": [ 
 <#list asserts as assert>
  { "success" : "${assert.value()}", "message" : "${assert.message?json_string}" }<#if assert?has_next>,</#if>
</#list> ]
 , "logs" : [  
<#list logs as log>
  { "level" : "${log.level}", "message" : "${log.message?json_string}" }<#if log?has_next>,</#if>    
</#list> ]
