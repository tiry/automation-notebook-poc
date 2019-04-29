Documents: 

<table>
<tr>
    <th> UID </th>
    <th> Title </th>
    <th> Type </th>
    <th> Path </th>
</tr>

<#list docs as doc>
<tr>
    <td> ${doc.id} </td>
    <td> ${doc.dublincore.title} </td>
    <td> ${doc.type} </td>
    <td> ${doc.path} </td>
</tr>

</#list>

</table>

<#include "/notebook/html/footer.ftl">
