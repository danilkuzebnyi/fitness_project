<#include '../structure.ftlh'>
<#include '../header.ftlh'>

<h1 class="text-center">Trainers</h1>
<div class="container">
    <div class="row">
        <div class="col">
            <#list specializations as specialization>
                <p class="badge bg-primary text-wrap fs-6" style="width: 150px">
                    <a class="btn" href="/trainers?specialization=${specialization.name}">
                        ${specialization.name}
                    </a>
                </p>
                <br>
            </#list>
        </div>
        <div class="col">
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a class="btn" href="/trainers?specialization=${specialization}&sorting=experience">
                    <i class="fa-solid fa-angles-down"></i> experience
                </a>
            </p>
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a class="btn" href="/trainers?specialization=${specialization}&sorting=smallPrice">
                    <i class="fa-solid fa-angles-up"></i> price
                </a>
            </p>
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a class="btn" href="/trainers?specialization=${specialization}&sorting=bigPrice">
                    <i class="fa-solid fa-angles-down"></i> price
                </a>
            </p>
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a class="btn" href="/trainers?specialization=${specialization}&sorting=rating">
                    <i class="fa-solid fa-angles-down"></i> rating
                </a>
            </p>
        </div>
        <div class="col-9">
            <#list trainers as trainer>
                <div id="link_wrapper" class="row" onclick="location.href='/trainers/${trainer.id}';" style="cursor:pointer;">
                    <div class="col-4">
                        <img class="img-fluid" style="height: 200px" src="${trainer.image}" alt="${trainer.fullName}">
                    </div>
                    <div class="col-5">
                        <a class="fw-bolder fs-3" href="/trainers/${trainer.id}"> ${trainer.fullName} </a>
                        <p class="fw-bold fs-5"> Specializations:
                        <ul>
                            <#list trainer.specializations as specialization>
                                <li> ${specialization.name} </li>
                            </#list>
                        </ul>
                        </p>
                    </div>
                    <div class="col-3">
                        <#if trainer.price??>
                            <h3 class="text-center mt-3"> ${trainer.price} ₴ </h3>
                        <#else>
                            <p></p>
                        </#if>>
                        <p class="btn btn-danger mt-4"> Book a workout </p>
                    </div>
                </div> <br>
            </#list>
        </div>
    </div>
</div>