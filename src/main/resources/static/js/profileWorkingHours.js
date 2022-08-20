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
                let hoursFrom = $(data).find('#hoursFrom').val();
                let hoursTo = $(data).find('#hoursTo').val();
                $('#hoursFrom').val(hoursFrom);
                $('#hoursTo').val(hoursTo);
            }
        })
    })
});

$(function deleteWorkingHours() {
    $('#delete-button').click(function () {
        $('#hoursFrom').val("");
        $('#hoursTo').val("");
    })
})