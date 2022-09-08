<div id="support-popup" class="popup" style="visibility: hidden">
    <div class="popup-outer">
        <div class="popup-inner">
            <i class="fa-solid fa-xmark popup-close"></i>
            <form action="/support" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="form-floating mb-3">
                    <input type="text" class="form-control" name="telephoneNumber" id="telephoneNumber" placeholder="Phone.." value="+" required />
                    <label for="telephoneNumber">Phone</label>
                    <span class="text-secondary">Enter the phone starting with "+"</span>
                </div>
                <button id="request-call" class="w-100 btn btn-lg btn-primary" type="submit">Request a call</button>
            </form>
        </div>
    </div>
</div>