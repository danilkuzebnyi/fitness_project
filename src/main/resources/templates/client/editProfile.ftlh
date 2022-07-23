<#include '../structure.ftlh'>
<#include '../header.ftlh'>

<div class="container mt-4 ms-4">
    <fieldset>
        <form action="/profile/user/edit" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="input-group mb-3">
                <input type="text" name="firstName" id="firstName" value="${currentUser.firstName!}" class="form-control" placeholder="First name" aria-label="First name" required autofocus/>
            </div>
            <div class="input-group mb-3">
                <input type="text"  name="lastName" id="lastName" value="${currentUser.lastName!}" class="form-control" placeholder="Last Name" aria-label="Last name "/>
            </div>
            <div class="input-group mb-3">
                <input type="text"  name="telephoneNumber" id="telephoneNumber" value="${currentUser.telephoneNumber!}" class="form-control" placeholder="Phone" aria-label="Phone" />
            </div>
            <div class="input-group mb-3">
                <span class="input-group-text" id="basic-addon1">@</span>
                <input type="email"  name="username" id="username" value="${currentUser.username!}" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
            </div>
            <div>
                <input type="password" name="password" id="password" class="form-control" placeholder="Password" aria-label="Password"/>
            </div> <br>
            <button class="btn btn-outline-success btn-lg" type="submit">Change data</button>
        </form>
    </fieldset>
</div>