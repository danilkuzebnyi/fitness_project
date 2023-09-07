$(function initRatings() {
    let rating = $('.rating');
    let ratingValue = $('.rating-value');
    let ratingActive = $('.rating-active');
    let ratingItems = $('.rating-item');
    let ratingCount = $('.rating-count');
    let currentRating;
    let initialRating;
    let count;

    init();
    function init() {
        count = parseInt(ratingCount.text());
        initialRating = parseToFloat(ratingValue.text());
    }

    setRatingActiveWidth();
    function setRatingActiveWidth(value) {
        value = value > 0 ? value : ratingValue.text();
        const ratingActiveWidth = parseToFloat(value) * 20;
        ratingActive.css('width', `${ratingActiveWidth}%`);
    }

    setRating();
    function setRating() {
        for (let i = 0; i < ratingItems.length; i++) {
            const ratingItem = $(ratingItems[i]);
            ratingItem.hover(() => setRatingActiveWidth(ratingItem.val()), () => setRatingActiveWidth());
            ratingValue.text(initialRating.toFixed(1))

            ratingItem.click(function () {
                currentRating = parseInt(ratingItem.val());
                updateRating(function(response, newRating) {
                    rating.addClass('rating-sending');
                    setRatingActiveWidth(ratingItem.val());
                    ratingValue.text(newRating.toFixed(1));
                    ratingCount.text(count + 1);
                });
            })
        }
    }

    function parseToFloat(value) {
        return parseFloat(value.replace(',', '.'));
    }

    function updateRating(callback) {
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            url: window.location.href,
            type: "POST",
            dataType: 'text',
            data: {currentRating},
            success: function(response) {
                let newRating = (initialRating * count + currentRating) / (count + 1);
                callback(response, newRating);
            },
            error: function(jqXHR) {
                let errorMessage = jqXHR.responseText;
                $('#rating-error-message').text(errorMessage);
            }
        });
    }
})