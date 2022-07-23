$(function getWorkingHours() {
    let select = $('#select-day');
    select.change(function () {
        let dayOfWeek = select.val();
        $.ajax({
            url: window.location.href,
            type: "GET",
            dataType: 'html',
            data: {dayOfWeek},
            success: function(data) {
                $('body').html(data);
            }
        });
    })
})