<#include '../structure.ftlh'>

<form action="/login" method="post" class="col-4 p-4 p-md-5 border rounded bg-light">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div class="form-floating mb-3">
        <input type="email" class="form-control" name="username" id="username" placeholder="Username" required />
        <label for="username">Username</label>
    </div>
    <div class="form-floating mb-3">
        <input type="password" class="form-control" name="password" id="password" placeholder="Password" required />
        <label for="password">Password</label>
    </div>
    <div class="checkbox mb-3">
        <label for="remember-me">Remember me</label>
        <input type="checkbox" name="remember-me" id="remember-me"/>
    </div>
    <button class="w-100 btn btn-lg btn-primary" type="submit">Sign in</button>
</form>

<#include '../popupImports.ftlh'>