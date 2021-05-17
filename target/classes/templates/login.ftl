<#import "parts/common.ftl" as c>
<#import "parts/loginregistermacro.ftl" as l>

<@c.page>
<h3>Login page</h3>

<#if message??>
	<div class="alert alert-${messageType} mt-2" role="alert">
		 ${message}
	</div>
</#if>

<#if Session??&&Session.SPRING_SECURITY_LAST_EXCEPTION??>
    <div class="alert alert-danger" role="alert">
        ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
    </div>
</#if>

<@l.login path="/login" act="Sign In"/>

</@c.page>