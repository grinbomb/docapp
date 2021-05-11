<#include "security.ftl">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark" >
	<#if !isAuthorized>
	    <a class="navbar-brand" href="/login">Login page</a>
	</#if>
    <#if isAuthorized>
		<a class="navbar-brand" href="/">Working page</a>
     </#if>
         <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent" >
        <ul class="navbar-nav mr-auto">
        </ul>
        <span class="navbar-text">
      		<ul class="navbar-nav mr-auto">
      			<#if isAuthorized>
	      			<li>
	   			 		<a class="nav-link">${name}</a>
	   			 	</li>
   			 	</#if>
   			 	<#if !isAuthorized>
		            <li class="nav-item">
		                <a class="nav-link" href="/login">Sign In</a>
		            </li>
		        	<li class="nav-item">
		                <a class="nav-link">/</a>
	            	</li>
		            <li class="nav-item">
		                <a class="nav-link" href="/registration">Registration</a>
	            	</li>
	            </#if>
	            <#if isAuthorized>
	   			 	<li class="nav-item">
					    <form action="/logout" method="post">
					        <input type="hidden" name="_csrf" value="${_csrf.token}" />
					        <button type="submit" class="btn btn-secondary m-1">Sign Out</button>
					    </form>
	   			 	</li>
   			 	</#if>
        	</ul>
    	</span>
    </div>
</nav>
