<#include '../structure.ftlh'>
<#include '../header.ftlh'>
<div class="container-fluid">
    <p> <span class="text-primary text-uppercase">First name: </span>${currentUser.firstName}</p>
    <p> <span class="text-primary text-uppercase">Last name: </span>${currentUser.lastName}</p>
    <p> <span class="text-primary text-uppercase">Username: </span>${currentUser.username}</p>
    <p> <span class="text-primary text-uppercase">Country: </span>${currentUser.country}</p>
    <p> <span class="text-primary text-uppercase">Phone: </span>${currentUser.telephoneNumber}</p>
    <p> <span class="text-primary text-uppercase">Price per training: </span>${currentTrainer.price}</p>
</div>
<div class="container-fluid">
    <p class="text-primary text-uppercase">Working days:
        <a href="/profile/export/excel">
            <i class="fas fa-file-excel fa-2x btn btn-primary ms-4"></i>
        </a>
    </p>
    <#list workingTimes as workingTime>
        <div class="row">
            <div class="col-md-1 col-sm-4 col-xs-6">${workingTime.dayOfWeek}:</div>
            <div class="col-md-1 col-sm-4 col-xs-3">${workingTime.hoursFrom?string("HH:mm")}</div>
            <div class="col-md-1 col-sm-4 col-xs-3">${workingTime.hoursTo?string("HH:mm")}</div>
        </div>
    </#list>
</div>
<a class="btn btn-outline-light mt-3">
    <form action="/profile/trainer/edit" method="get">
        <input class="btn btn-primary" type="submit" value="Edit my profile"/>
    </form>
</a>
<a class="btn btn-outline-light mt-3">
    <form action="/logout" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input class="btn btn-danger" type="submit" value="Log-out"/>
    </form>
</a>