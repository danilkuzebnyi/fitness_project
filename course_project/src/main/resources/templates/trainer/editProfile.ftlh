<#include '../structure.ftlh'>
<#include '../header.ftlh'>
<div class="container ms-3 mt-3">
    <form action="/profile/trainer/${trainer.id}/edit" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="input-group mb-3">
            <input type="text" name="firstName" id="firstName" value="${trainer.firstName}" class="form-control" placeholder="First name" aria-label="First name" required autofocus/>
        </div>
        <div class="input-group mb-3">
            <input type="text"  name="lastName" id="lastName" value="${trainer.lastName}" class="form-control" placeholder="Last Name" aria-label="Last name "/>
        </div>
        <div class="input-group mb-3">
            <input type="text"  name="telephoneNumber" id="telephoneNumber" value="${trainer.telephoneNumber}" class="form-control" placeholder="Phone" aria-label="Phone" />
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1">@</span>
            <input type="email"  name="username" id="username" value="${trainer.username}" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
        </div>
        <div>
            <input type="password" name="password" id="password" class="form-control" placeholder="Password" aria-label="Password"/>
        </div> <br>
        <div class="input-group mb-3">
            <input type="number" name="price" id="price" value="${trainer.price}" class="form-control" placeholder="Price per training" aria-label="Price per training"/>
        </div>

        <div id="workingHours">
            <p>Working days: </p>
            <select class="btn btn-secondary dropdown-toggle" name="dayOfWeek" id="select-day">
                <#list dayOfWeeks as dayOfWeek>
                    <#if dayOfWeek == selectedDayOfWeek>
                        <option value="${dayOfWeek}" selected>${dayOfWeek}</option>
                    <#else>
                        <option value="${dayOfWeek}">${dayOfWeek}</option>
                    </#if>
                </#list>
            </select>
            <label for="hoursFrom">From: </label>
            <#if workingTimeOfTrainerByDayOfWeek.hoursFrom??>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursFrom" id="hoursFrom" value="${workingTimeOfTrainerByDayOfWeek.hoursFrom?string("HH:mm")}"/>
            <#else>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursFrom" id="hoursFrom"/>
            </#if>
            <label for="hoursTo">To: </label>
            <#if workingTimeOfTrainerByDayOfWeek.hoursFrom??>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursTo" id="hoursTo" value="${workingTimeOfTrainerByDayOfWeek.hoursTo?string("HH:mm")}"/>
            <#else>
                <input class="btn btn-outline-info btn-lg" type="time" name="hoursTo" id="hoursTo"/>
            </#if>
            <br> <button class="btn btn-outline-success btn-lg mt-3" type="submit">Change data</button>
        </div>
    </form>
</div>
<script src="/static/js/jquery-3.6.0.js"></script>
<script src="/static/js/profileWorkingHours.js"></script>
