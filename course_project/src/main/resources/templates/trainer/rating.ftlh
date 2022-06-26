<link rel="stylesheet" href="/static/css/rating.css">
<div id="rating-div">
    <form class="form" action="/trainers/${id}" method="post">
        <div class="rating">
            <div class="rating-body">
                <div class="rating-active"></div>
                <div class="rating-items">
                    <input class="rating-item" type="radio" name="rating" value="1">
                    <input class="rating-item" type="radio" name="rating" value="2">
                    <input class="rating-item" type="radio" name="rating" value="3">
                    <input class="rating-item" type="radio" name="rating" value="4">
                    <input class="rating-item" type="radio" name="rating" value="5">
                </div>
            </div>
        </div>
    </form>
    <div class="rate-numbers">
        <div class="rating-value text-center mt-2">${rating.value}</div>
        <div class="reviews text-center">( <span class="rating-count">${rating.count}</span> reviews )</div>
    </div>
</div>
<script src="/static/js/jquery-3.6.0.js"></script>
<script src="/static/js/rating.js"></script>
