$(function getCodeByCountry() {
    let select = $('#select-country');
    select.change(function () {
        let countryId = select.val();
        $.ajax({
            url: window.location.href,
            type: "GET",
            dataType: 'html',
            data: {countryId},
            success: function(data) {
                let country = $(data).find('#selectedCountry').text();
                let code = $(data).find('#code').text();
                $('#selectedCountry').html(country);
                $('#code').html(code);
            }
        })
    })
});

$(function getCountry() {
    let select = $('#select-country');
    select.change(function () {
        let countryId = select.val();
        $.ajax({
            url: window.location.href,
            type: "GET",
            dataType: 'string',
            data: {countryId}
        })
    })
});