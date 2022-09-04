<#include '../structure.ftlh'>
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<div class="container mt-3">
    <div class="row">
        <div class="col-3 offset-1">
            <img class="img-fluid" style="height: 200px" src="../${trainer.image}" alt="${trainer.fullName}">
        </div>
        <div class="col-5">
            <p class="fw-bolder fs-1 text-primary"> ${trainer.fullName} </p>
            <lh class="fw-bold fs-4"> Specializations: </lh>
            <ul>
                <#list trainer.specializations as specialization>
                    <li class="fst-italic fs-5"> ${specialization.name} </li>
                </#list>
            </ul>
            <#if trainer.description??>
                <p class="fw-light"> ${trainer.description} </p>
            </#if>
            <div class="mt-5">
                <form action="/trainers/${id}" method="get">
                    <input class="btn btn-outline-info btn-lg" type="date" name="date" id="date" value="${date!}"/>
                    <input class="btn btn-outline-info btn-lg" type="submit" value="View working hours" id="viewWorkingHours"/><br>
                </form>
                <div id="working-hours-div">
                    <#if date?has_content && workingHours?has_content>
                        <p>Select hours:
                        <form action="/trainers/${id}/success" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <select class="btn btn-info dropdown-toggle" style="width: 170px;">
                                <#list workingHours as time>
                                    <option value="${time}">${time}</option>
                                </#list>
                            </select>
                            </p>
                            <button class="btn btn-outline-success btn-lg" type="submit">Book a workout</button>
                        </form>
                    <#elseif date?has_content && !workingHours?has_content>
                        <p class="text-danger">${trainer.firstName} has a day off. Please select another day.</p>
                    </#if>
                </div>
            </div>
        </div>
        <div class="col-2 offset-1">
            <#if trainer.price??>
                <h3 class="text-center"> ${trainer.price} ₴ </h3>
            <#else>
                <p></p>
            </#if>
            <#include 'rating.ftlh'>
        </div>
    </div>
</div>

<#include '../popupImports.ftlh'>
