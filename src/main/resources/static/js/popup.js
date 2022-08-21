$(function defineMethodsToClosePopup() {
    let popup = $('.popup');
    const escapeKeyCode = 27;

    $('.popup-close').click(function () {
        closePopup(popup);
    })

    $('.popup-outer').click(function () {
        closePopup(popup);
    })

    $(document).keydown(function (e) {
        if (e.keyCode === escapeKeyCode) {
            closePopup(popup);
        }
    })

    function closePopup(popup) {
        popup.hide();
    }
})
