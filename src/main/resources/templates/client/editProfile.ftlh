<#include '../structure.ftlh'>
<#import "/spring.ftl" as spring />

<div class="container mt-4 ms-4">
    <fieldset>
        <form action="/profile/user/edit" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="input-group mb-3">
                <input type="text" name="firstName" id="firstName" value="${user.firstName!}" class="form-control" placeholder="First name" aria-label="First name" required autofocus/>
            </div>
            <div class="input-group mb-3">
                <input type="text"  name="lastName" id="lastName" value="${user.lastName!}" class="form-control" placeholder="Last Name" aria-label="Last name "/>
            </div>
            <div class="input-group">
                <#include '../country.ftlh'>
                <span class="input-group-text" id="code">${Session.code!}</span>
                <input type="tel"  name="telephoneNumber" id="telephoneNumber" value="${user.telephoneNumber!}" class="form-control ${(telephoneNumberError??)?string('is-invalid', '')}" placeholder="Phone" aria-label="Phone" aria-describedby="code" />
            </div>
            <div class="container">
                <div class="row justify-content-md-end">
                    <div class="col-4">
                        <@spring.bind 'user.telephoneNumber'/>
                        <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
                    </div>
                </div>
            </div> <br>
            <div>
                <@spring.bind 'user.username'/>
                <div class="input-group mb-3">
                    <span class="input-group-text" id="basic-addon1">@</span>
                    <input type="email"  name="username" id="username" value="${user.username!}" class="form-control ${(usernameError??)?string('is-invalid', '')}" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
                </div>
                <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
            </div> <br>
            <p id="change-password" class="btn btn-link">Change password</p>
            <div id="div-password" hidden>
                <input type="password" name="password" id="password" class="form-control ${(passwordError??)?string('is-invalid', '')}" placeholder="Password" aria-label="Password" disabled />
            </div>
            <@spring.bind 'user.password'/>
            <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
            <br> <br>
            <button class="btn btn-outline-success btn-lg" type="submit">Change data</button>
        </form>
    </fieldset>
</div>

<#include '../popupImports.ftlh'>
<script src="/static/js/country.js"></script>
<script src="/static/js/changePassword.js"></script>