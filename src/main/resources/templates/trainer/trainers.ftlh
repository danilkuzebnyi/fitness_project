<#include '../structure.ftlh'>

<h1 class="text-center">Trainers</h1>
<div class="container">
    <div class="row">
        <div class="col">
            <#list specializations as specialization>
                <p class="badge bg-primary text-wrap fs-6" style="width: 150px">
                    <a id="${specialization.name}" class="btn" href="/trainers?specialization=${specialization.name}">
                        ${specialization.name}
                    </a>
                </p>
                <br>
            </#list>
        </div>
        <div class="col">
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a id="sortByExperience" class="btn" href="/trainers?specialization=${specialization}&sorting=experience">
                    <i class="fa-solid fa-angles-down"></i> experience
                </a>
            </p>
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a id="sortByAscPrice" class="btn" href="/trainers?specialization=${specialization}&sorting=small_price">
                    <i class="fa-solid fa-angles-up"></i> price
                </a>
            </p>
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a id="sortByDescPrice" class="btn" href="/trainers?specialization=${specialization}&sorting=big_price">
                    <i class="fa-solid fa-angles-down"></i> price
                </a>
            </p>
            <p class="badge bg-info text-wrap" style="width: 120px">
                <a id="sortByRating" class="btn" href="/trainers?specialization=${specialization}&sorting=rating">
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
                        <ul id="trainerSpecializations">
                            <#list trainer.specializations as specialization>
                                <li id="trainerSpecialization"> ${specialization.name} </li>
                            </#list>
                        </ul>
                        </p>
                    </div>
                    <div class="col-3">
                        <#if trainer.price??>
                            <h3 id="trainerPrice" class="text-center mt-3"> ${trainer.price} ₴ </h3>
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

<#include '../popupImports.ftlh'>