$(function changePassword() {
    let divPassword = $('#div-password');
    let changePasswordButton = $('#change-password');
    let inputPassword = $('#password');
    changePasswordButton.click(function () {
        divPassword.removeAttr('hidden');
        inputPassword.removeAttr('disabled');
        changePasswordButton.attr('hidden', true);
    })
});