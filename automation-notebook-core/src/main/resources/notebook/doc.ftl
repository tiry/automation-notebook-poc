
Document: uuid ${doc.id}

<table>
<tr>
    <th> Title </th>
    <td> ${doc.dublincore.title} </td>
</tr>
<tr>
    <th> Type </th>
    <td> ${doc.type} </td>
</tr>

<tr>
    <th> Path </th>
    <td> ${doc.path} </td>
</tr>

</table>

<#include "/notebook/footer.ftl">