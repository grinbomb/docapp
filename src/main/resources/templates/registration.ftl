<#import "parts/common.ftl" as c>
<#import "parts/loginregistermacro.ftl" as l>

<@c.page>

<h3>Add new user</h3>

<#if message??>
<div class="alert alert-warning mt-2" role="alert">
 ${message}
</div>
</#if>

<@l.login path = "/registration" act="Create" />

</@c.page>