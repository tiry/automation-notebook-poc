<BR/>

<I>Execution time = ${t} ms</I>

<BR/>

<#list asserts as assert>
 <#if assert.isSuccess()>  
    <div style='background-color:#99FF99; margin:1px;font-weight:bold;'> PASS: ${assert.message} </div>
 <#else>
    <div style='background-color:#FF4444; margin:1px;font-weight:bold;'> FAIL: ${assert.message}</div>
 </#if>
</#list>

<#list logs as log>
    <#if log.level == "ERR">
      <div style="color:red"> 
    <#elseif log.level == "WARN">
      <div style="color:orange">
    <#elseif log.level == "INFO">
      <div style="color:black">
    <#elseif log.level == "TRC" >
      <div style="color:#999999"> 
    </#if>
    
      ${log.ts} - [${log.thread}] : ${log.message}
    </div>
 </#list>
