<#include '../structure.ftlh'>
<#include '../header.ftlh'>
<#import "/spring.ftl" as spring />

<div class="container mt-4 ms-4">
    <fieldset>
        <h2 class="text-info">Sign up</h2>
        <form action="/signup" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="input-group mb-3 mt-4">
                <input type="text" name="firstName" id="firstName" class="form-control" placeholder="First name" aria-label="First name" required autofocus />
            </div>
            <div class="input-group mb-3">
                <input type="text" name="lastName" id="lastName" class="form-control" placeholder="Last Name" aria-label="Last name" />
            </div>
            <div class="input-group" id="country-phone">
                <#include '../country.ftlh'>
                <span class="input-group-text" id="code">${Session.code!}</span>
                <input type="tel" name="telephoneNumber" id="telephoneNumber" class="form-control" placeholder="Phone" aria-label="Phone" aria-describedby="code" />
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
                <div class="input-group">
                    <span class="input-group-text" id="basic-addon1">@</span>
                    <input type="email" name="username" id="username" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1" />
                </div>
                <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
            </div> <br>
            <div>
                <@spring.bind 'user.password'/>
                <input type="password" name="password" id="password" class="form-control" placeholder="Password" aria-label="Password"/>
                <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
            </div> <br>
            <button class="btn btn-outline-success btn-lg" id="submit-button" style="width: 40%;" type="submit">Sign up</button>
        </form>
    </fieldset>
</div>
<script src="/static/js/jquery-3.6.0.js"></script>
<script src="/static/js/country.js"></script>