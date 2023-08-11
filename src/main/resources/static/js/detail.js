let productId = window.location.pathname.split('/')[3];
let status = "";
let token = localStorage.getItem("Authorization");
let isWish = false;
let wishCount = 0;
$(document).ready(function () {

    $.ajax({
        type: "GET",
        url: `/api/products/${productId}`,
        contentType: "application/json",
    }).done(function (json) {
        console.log(json);
        let productId = json['id'];
        let productName = json['productName'];
        let productImage = json['productImage'];
        let productCategory = json['category'];
        let price = json['price'];
        let mallName = json['mallName'];
        wishCount = json['wishCount'];
        status = json['status'];

        // 카운트다운 타이머 설정
        let eventDate = new Date(json['eventStartTime']);
        let endTime = new Date(eventDate);
        endTime.setDate(endTime.getDate() + 1);  // 다음날
        endTime.setHours(0, 0, 0, 0);


        $("#product-container").empty();
        let html = `<div class="row" id="product-row">
                                <div class="col-md-6">
                                    <img src="${productImage}" alt="Product Name" class="img-fluid">
                                </div>
                                <div class="col-md-6">
                                    <h1>${productName}</h1>
                                    <p class="text-muted">${productCategory}</p>
                                    <h2>판매 가격 : ${price} ₩</h2>
                                    <h3>판매처 : ${mallName}</h3>
                                    <div class="mb-3">
                                        <label for="quantity" class="form-label">구매 수량 : </label>
                                        <input type="number" class="form-control" id="quantity" value="1" min="1">
                                    </div>
                                    <button class="btn btn-primary btn-lg" onclick="order()">구매</button>
                                    
                                    <button class="btn btn-primary btn-lg" id="wish-btn" onclick="onWish()">찜 ${wishCount}</button>
                                </div>
                            </div>
                            <div class="row mt-5">
                                <div class="col">
                                    <h3>판매 기간</h3>
                                    <p id="countdown-timer"></p>
                                </div>
                            </div>`
        $("#product-container").append(html);

        updateCountdown(eventDate, endTime, status);

        setInterval(function () {
            updateCountdown(eventDate, endTime, status);
        }, 1000);

    }).fail(function (jqXHR, textStatus) {
        alert("상품 조회 실패!");
        window.location.href = '/openrun/main'
    });

    // 로그인 상태일 때만 찜 조회
    if (token != null) { // 로그인 상태
        $.ajax({
            type: "GET",
            url: `/api/products/${productId}/wish/user`,
            contentType: "application/json",
            headers: {
                'Authorization': token
            },
        }).done(function (json) {
            console.log(json);
            isWish = json['isWish'];
            if (isWish) {
                $("#wish-btn").text("찜 취소" + wishCount);
            } else {
                $("#wish-btn").text("찜 하기" + wishCount);
            }
        }).fail(function (jqXHR, textStatus) {
        });
    }
});

function updateCountdown(eventDate, endTime, status) {
    let now = new Date();
    let timeDiff = endTime - now;
    let hours = Math.floor(timeDiff / (1000 * 60 * 60));
    let minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));
    let seconds = Math.floor((timeDiff % (1000 * 60)) / 1000);

    if (timeDiff <= 0 || status === "CLOSE") {
        clearInterval(updateCountdown);
        document.getElementById("countdown-timer").textContent = "판매 기간이 종료되었습니다!";
    } else if (status === "WAITING") {
        document.getElementById("countdown-timer").textContent = `판매 기간이 아닙니다! 판매날짜는 : ${eventDate} 입니다!`;
    } else {
        document.getElementById("countdown-timer").textContent = `남은 시간: ${hours}시간 ${minutes}분 ${seconds}초`;
    }
}

function order() {
    if (localStorage.getItem("Authorization") === null) {
        alert("로그인이 필요합니다!");
        return;
    }
    if ($("#quantity").val() < 1) {
        alert("구매 수량은 1개 이상이어야 합니다.");
        return;
    }
    if (!confirm("구매하시겠습니까?")) {
        return;
    }
    if (status === "WAITING" || status === "CLOSE") {
        alert("판매 기간이 아닙니다!");
        return;
    }
    $.ajax({
        type: "POST",
        url: `/api/orders/${productId}`,
        contentType: "application/json",
        headers: {
            'Authorization': token
        },
        data: JSON.stringify({count: $("#quantity").val()}),
    }).done(function (json) {
        alert("구매 성공");
        window.location.reload();
    }).fail(function (jqXHR, textStatus) {
        alert("구매 실패");
        window.location.reload();
    });
}

function onWish() {
    if (localStorage.getItem("Authorization") === null) {
        alert("로그인이 필요합니다!");
        return;
    }
    if (isWish) {
        if (!confirm("찜을 취소하시겠습니까?")) {
            return;
        }
        $.ajax({
            type: "DELETE",
            url: `/api/products/${productId}/wish`,
            contentType: "application/json",
            headers: {
                'Authorization': token
            },
        }).done(function (json) {
            alert("찜 취소 성공");
            isWish = false
            wishCount = wishCount - 1
            $("#wish-btn").text("찜 하기 " + wishCount);
        }).fail(function (jqXHR, textStatus) {
            alert("찜 취소 실패");
        });
    } else {
        if (!confirm("찜 하시겠습니까?")) {
            return;
        }
        $.ajax({
            type: "POST",
            url: `/api/products/${productId}/wish`,
            contentType: "application/json",
            headers: {
                'Authorization': token
            },
        }).done(function (json) {
            alert("찜 성공");
            isWish = true
            wishCount = wishCount + 1
            $("#wish-btn").text("찜 취소 " + wishCount);
        }).fail(function (jqXHR, textStatus) {
            alert("찜 실패");
        });
    }
}