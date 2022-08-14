<#include '../structure.ftlh'>
<#include '../header.ftlh'>

<div class="container-fluid">
    <p> <span class="text-primary text-uppercase">First name: </span>${currentUser.firstName}</p>
    <p> <span class="text-primary text-uppercase">Last name: </span>${currentUser.lastName}</p>
    <p> <span class="text-primary text-uppercase">Username: </span>${currentUser.username}</p>
    <p> <span class="text-primary text-uppercase">Country: </span>${currentUser.country}</p>
    <p> <span class="text-primary text-uppercase">Phone: </span>${currentUser.telephoneNumber}</p>
</div>
<a class="btn btn-outline-light mt-3">
    <form action="/profile/user/edit" method="get">
        <input class="btn btn-primary" type="submit" value="Edit my profile"/>
    </form>
</a>
<a class="btn btn-outline-light mt-3">
    <form action="/logout" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input class="btn btn-danger" type="submit" value="Log-out"/>
    </form>
</a>