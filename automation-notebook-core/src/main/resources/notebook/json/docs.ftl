{
  "type" : "documents",
  "uids" : [ 
<#list docs as doc>
  "${doc.id}"<#if doc?has_next>,</#if>
</#list>
]
<#include "/notebook/json/footer.ftl">
}
