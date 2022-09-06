<#include '../structure.ftlh'>
<#include '../header.ftlh'>
<#import "/spring.ftl" as spring />

<div class="container ms-3 mt-3">
    <form action="/profile/trainer/${trainer.id}/edit" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="input-group mb-3">
            <input type="text" name="firstName" id="firstName" value="${trainer.firstName!}" class="form-control" placeholder="First name" aria-label="First name" required />
        </div>
        <div class="input-group mb-3">
            <input type="text"  name="lastName" id="lastName" value="${trainer.lastName!}" class="form-control" placeholder="Last Name" aria-label="Last name" required />
        </div>
        <div class="input-group mb-3">
            <#include '../country.ftlh'>
            <span class="input-group-text" id="code">${Session.code!}</span>
            <input type="text"  name="telephoneNumber" id="telephoneNumber" value="${trainer.telephoneNumber!}" class="form-control ${(telephoneNumberError??)?string('is-invalid', '')}" placeholder="Phone" aria-label="Phone" />
        </div>
        <div class="container">
            <div class="row justify-content-md-end">
                <div class="col-4">
                    <@spring.bind 'trainer.telephoneNumber'/>
                    <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
                </div>
            </div>
        </div> <br>
        <div>
            <@spring.bind 'trainer.username'/>
            <div class="input-group mb-3">
                <span class="input-group-text" id="basic-addon1">@</span>
                <input type="email"  name="username" id="username" value="${trainer.username!}" class="form-control ${(usernameError??)?string('is-invalid', '')}" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
            </div>
            <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
        </div> <br>
        <p id="change-password" class="btn btn-link">Change password</p>
        <div id="div-password" hidden>
            <input type="password" name="password" id="password" class="form-control ${(passwordError??)?string('is-invalid', '')}" placeholder="Password" aria-label="Password" disabled />
        </div>
        <@spring.bind 'trainer.password'/>
        <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
        <br> <br>
        <div class="input-group mb-3">
            <input type="number" name="price" id="price" value="${trainer.price!}" class="form-control" placeholder="Price per training" aria-label="Price per training"/>
        </div>

        <div id="workingHours">
            <p>Working days: </p>
            <select class="btn btn-secondary dropdown-toggle" name="dayOfWeek" id="select-day">
                <#list Session.dayOfWeeks as dayOfWeek>
                    <#if dayOfWeek == Session.selectedDayOfWeek>
                        <option value="${dayOfWeek}" selected>${dayOfWeek}</option>
                    <#else>
                        <option value="${dayOfWeek}">${dayOfWeek}</option>
                    </#if>
                </#list>
            </select>
            <label for="hoursFrom">From: </label>
            <#if Session.workingTimeOfTrainerByDayOfWeek.hoursFrom??>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursFrom" id="hoursFrom" value="${Session.workingTimeOfTrainerByDayOfWeek.hoursFrom?string("HH:mm")}"/>
            <#else>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursFrom" id="hoursFrom"/>
            </#if>
            <label for="hoursTo">To: </label>
            <#if Session.workingTimeOfTrainerByDayOfWeek.hoursFrom??>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursTo" id="hoursTo" value="${Session.workingTimeOfTrainerByDayOfWeek.hoursTo?string("HH:mm")}"/>
            <#else>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursTo" id="hoursTo"/>
            </#if>
            <i id="delete-button" class="fs-3 ms-2 fa-solid fa-circle-minus"></i>
            <br>
            <#if message??>
                <#include '../popup.ftlh'>
            </#if>
            <button class="btn btn-outline-success btn-lg mt-3" type="submit">Change data</button>
        </div>
    </form>
</div>
<script src="/static/js/jquery-3.6.0.js"></script>
<script src="/static/js/country.js"></script>
<script src="/static/js/profileWorkingHours.js"></script>
<script src="/static/js/changePassword.js"></script>
<script src="/static/js/popup.js"></script>
