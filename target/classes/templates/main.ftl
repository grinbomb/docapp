<#import "parts/common.ftl" as c>

<@c.page>
<form method="post" enctype="multipart/form-data">
	<div class="form-row">
    	<div class="form-group col-md-6">
      		<label for="inputDocName">Document's name</label>
      		<input type="text" name="name" autocomplete="off" class="form-control ${(nameError??)?string('is-invalid', '')}" value="<#if document??>${document.name}</#if>" id="inputDocName">
      		<#if nameError??>
	   			<div class="invalid-feedback">
 					 ${nameError}
				</div>
			</#if>
    	</div>
    	<div class="form-group col-md-6">
    	
    		<label for="inputFileType">File type</label>
	    	<select name="fileType" class="form-control ${(fileTypeError??)?string('is-invalid', '')}" value="<#if document??>${document.fileType}</#if>" id="inputFileType">
		    	<option hidden=""></option>
		      	<option value="pdf" ${setPDF}>pdf</option>
		      	<option value="docx" ${setDOC}>docx</option>
		      	<option value="txt" ${setTXT}>txt</option>
		    </select>
    		
      	<#if fileTypeError??>
	   		<div class="invalid-feedback">
 				 ${fileTypeError}
			</div>
		</#if>
		</div>
  </div>
    <div class="form-row">
    	<div class="form-group col-md-12">
			<label for="inputDocDescription">Document description</label>
      		<textarea name="body" autocomplete="off" class="form-control ${(bodyError??)?string('is-invalid', '')}" id="inputDocDescription"><#if document??>${document.body}</#if></textarea>
      		<#if bodyError??>
	   			<div class="invalid-feedback">
 					 ${bodyError}
				</div>
			</#if>
		</div>
  	</div>

  	<button type="submit" class="btn btn-danger">Confirm</button>
  	
  	<input type="hidden" name="_csrf" value="${_csrf.token}" />
	</form>
	
	<#if errorChangeFile??>
		<div class="alert alert-danger mt-2" role="alert">
		 ${errorChangeFile}
		</div>
	</#if>
	
	<#if messageDoc??>
		<div class="alert alert-success mt-2" role="alert">
		 ${messageDoc}
		</div>
	</#if>
	
	
	<form method="get" enctype="multipart/form-data" action="/">
		<div class="form-row mt-3">
			<label for="filtertag">Document name</label>
    		<div class="form-group col-md-3">
    			<input type="text" name="filtertag" class="form-control" autocomplete="off" id="filtertag" value="<#if filtertag??>${filtertag}</#if>">
    		</div>
    		<div class="form-group col-md-2">
    			<button type="submit" class="btn btn-primary">Find</button>
    		</div>
    	</div>
	</form>
	<#if docs??>
	<form method="post" enctype="multipart/form-data" action="/share">
	<h2>My documents</h2>
	<div class="form-row">
    	<div class="form-group col-md-8">
	
	<table class="table my-2">
	  <thead>
	    <tr>
	      <th scope="col" class="text-center">Name</th>
	      <th scope="col" class="text-center">Date</th>
	      <th scope="col" class="text-center">Read only</th>
	      <th scope="col" class="text-center">Select</th>
	      <th scope="col" class="text-center">Delete</th>
	      <th scope="col" class="text-center">Download</th>
	    </tr>
	  </thead>
	  <tbody>
	  <#list docs as doc>
	    <tr>
	      <td class="text-center">${doc.name+doc.fileType}</td>
	      <td class="text-center">${doc.date}</td>
	      <td class="text-center"><input type="checkbox" class="form-check-input" id="checkdoc" value="${doc.id}" name="readOnly"></td>
	      <td class="text-center"><input type="checkbox" class="form-check-input" id="checkdoc" value="${doc.id}" name="checkeddocs"></td>
	      <td class="text-center"><a href="/deletefile/${doc.id}"><img src="https://i.ibb.co/jMMQbJd/delete-remove-trash-trash-bin-trash-can-icon-1320073117929397588-24.png" alt="delete-remove-trash-trash-bin-trash-can-icon-1320073117929397588-24" border="0" width="20px" height="20px"></a></td>
	      <td class="text-center"><a href="/openfile/${doc.id}"><img src="https://i.ibb.co/ZHJ1NMr/download.png" alt="download" border="0" width="20px" height="20px"></a></td>
	    </tr>
	    </#list>
	  </tbody>
	</table>
	</div>
	<div class="form-group col-md-4">
	<#if users??>
	<table class="table table-dark my-2">
	  <thead>
	    <tr>
	      <th scope="col" class="text-center">User</th>
	      <th scope="col" class="text-center">Send</th>
	    </tr>
	  </thead>
	  <tbody>
	  <#list users as user>
	    <tr>
	      <td class="text-center">${user.username}</td>
	      <td class="text-center"><input type="checkbox" class="form-check-input" id="checkusr" value="${user.id}" name="checkedusers"></td>
	    </tr>
	    </#list>
	  </tbody>
	</table>
	</#if>
	</div>
	</div>
	<button type="submit" class="btn btn-primary">Share</button>
  	<input type="hidden" name="_csrf" value="${_csrf.token}"/>
	</form>
	</#if>
	<#if sharedMessage??>
		<div class="alert alert-info mt-2" role="alert">
			${sharedMessage}
		</div>
	</#if>
	<#if shareddocs??>
	<h2 class="mt-4">Documents sent to me</h2>
	<table class="table my-2">
	  <thead>
	    <tr>
	      <th scope="col" class="text-center">Name</th>
	      <th scope="col" class="text-center">Date</th>
	      <th scope="col" class="text-center">Author</th>
	      <th scope="col" class="text-center">Change</th>
	      <th scope="col" class="text-center">Download</th>
	    </tr>
	  </thead>
	  <tbody>
	  <#list shareddocs as shdoc>
	    <tr>
	      <td class="text-center">${shdoc.document.name+shdoc.document.fileType}</td>
	      <td class="text-center">${shdoc.document.date}</td>
	      <td class="text-center">${shdoc.document.author.username}</td>
	      <td class="text-center"><#if !shdoc.readOnly><a href="/changefile/${shdoc.document.id}"><img src="https://i.ibb.co/ns8wcXg/PNGIX-com-comments-icon-png-6437957.png" alt="PNGIX-com-comments-icon-png-6437957" border="0"  width="20px" height="20px"></a></#if></td>
	      <td class="text-center"><a href="/openfile/${shdoc.document.id}"><img src="https://i.ibb.co/ZHJ1NMr/download.png" alt="download" border="0" width="20px" height="20px"></a></td>
	    </tr>
	    </#list>
	  </tbody>
	</table>
	</#if>
</@c.page>