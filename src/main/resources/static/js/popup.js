$(function defineMethodsToClosePopup() {
    let popup = $('.popup');
    const escapeKeyCode = 27;

    $('.popup-close').click(function () {
        closePopup(popup);
    })

    $(document).keydown(function (e) {
        if (e.keyCode === escapeKeyCode) {
            closePopup(popup);
        }
    })

    function closePopup(popup) {
        popup.css('visibility', 'hidden');
        if (popup.is($('#error-popup'))) {
            $('#popup-message').text("");
        }
    }
})

$(function showSupportPopup() {
    let popup = $('#support-popup');
    let supportButton = $('#support-button');
    supportButton.click(function () {
        popup.css('visibility', 'visible');
    })
})
