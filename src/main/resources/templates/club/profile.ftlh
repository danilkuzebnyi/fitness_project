<#include '../structure.ftlh'>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-5 mt-4">
            <p> <span class="text-primary text-uppercase">First name: </span>${currentUser.firstName}</p>
            <p> <span class="text-primary text-uppercase">Last name: </span>${currentUser.lastName}</p>
            <p> <span class="text-primary text-uppercase">Username: </span>${currentUser.username}</p>
            <p> <span class="text-primary text-uppercase">Country: </span>${currentUser.country}</p>
            <p> <span class="text-primary text-uppercase">Phone: </span>${currentUser.telephoneNumber}</p>
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
        </div>

        <div class="col-7 mt-4">
            <h6 class="text-info text-center"> My Trainers
                <a href="/profile/export/pdf">
                    <i class="fas fa-file-pdf fa-2x btn btn-primary ms-4"></i>
                </a>
            </h6>
            <div class="mt-4">
                <#list clubTrainers as trainer>
                    <span id="link_wrapper" onclick="location.href='profile/trainer/${trainer.id}/edit';" style="cursor:pointer;">
                        <img class="img-fluid ms-4 mt-4" style="height: 100px" src="${trainer.image}" alt="${trainer.fullName}">
                    </span>
                </#list>
            </div>
        </div>
    </div>
</div>

<#include '../popupImports.ftlh'>