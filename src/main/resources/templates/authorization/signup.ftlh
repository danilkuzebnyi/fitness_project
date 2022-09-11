<#include '../structure.ftlh'>
<#import "/spring.ftl" as spring />

<div class="container mt-4 ms-4">
    <fieldset>
        <h2 class="text-info">Sign up</h2>
        <form action="/signup" method="post" id="registration-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="input-group mb-3 mt-4">
                <input type="text" value="${user.firstName!}" name="firstName" id="firstName" class="form-control" placeholder="First name" aria-label="First name" required autofocus />
            </div>
            <div class="input-group mb-3">
                <input type="text" value="${user.lastName!}" name="lastName" id="lastName" class="form-control" placeholder="Last Name" aria-label="Last name" required />
            </div>
            <div class="input-group" id="country-phone">
                <#include '../country.ftlh'>
                <span class="input-group-text" id="code">${Session.code!}</span>
                <input type="tel" value="${user.telephoneNumber!}" name="telephoneNumber" id="telephoneNumber" class="form-control ${(telephoneNumberError??)?string('is-invalid', '')}" placeholder="Phone" aria-label="Phone" aria-describedby="code" />
            </div>
            <div class="container">
                <div class="row">
                    <div class="col-6">
                        <@spring.bind 'user.country'/>
                        <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
                    </div>
                    <div class="col-4 offset-2">
                        <@spring.bind 'user.telephoneNumber'/>
                        <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
                    </div>
                </div>
            </div> <br>
            <div>
                <@spring.bind 'user.username'/>
                <div class="input-group">
                    <span class="input-group-text" id="basic-addon1">@</span>
                    <input type="email" value="${user.username!}" name="username" id="username" class="form-control ${(usernameError??)?string('is-invalid', '')}" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1" />
                </div>
                <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
            </div> <br>
            <div>
                <@spring.bind 'user.password'/>
                <input type="password" value="${user.password!}" name="password" id="password" class="form-control ${(passwordError??)?string('is-invalid', '')}" placeholder="Password" aria-label="Password"/>
                <@spring.showErrors separator=", " classOrStyle="text-danger ms-4" />
            </div> <br>
            <#if recaptchaError??>
                <input type="password" name="confirmationPassword" id="confirmationPassword"
                       class="form-control ${(confirmationPasswordError??)?string('is-invalid', '')}" placeholder="Confirmation Password" />
                <br>
            </#if>
            <button class="g-recaptcha btn btn-outline-success btn-lg"
                    data-sitekey="6LdrquMhAAAAAGLK9T_yFDHeG3goYKEmhVUavLIC"
                    data-callback='onSubmit'
                    data-action='register' id="submit-button" style="width: 40%;" type="submit">Sign up</button>
        </form>
    </fieldset>
</div>

<#include '../popupImports.ftlh'>
<script src="/static/js/country.js"></script>
<script src="https://www.google.com/recaptcha/api.js?render=${Session.recaptchaSiteKey}"></script>
<script type="text/javascript">
    function onSubmit() {
        $("#registration-form").submit();
    }
</script>
