let productId = window.location.pathname.split('/')[3];
let status = "";
$(document).ready(function () {
    console.log("productId", productId);
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
            let wishCount = json['wishCount'];
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
                                    
                                    <button class="btn btn-primary btn-lg">찜 ${wishCount}</button>
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

        setInterval(function() {
            updateCountdown(eventDate, endTime, status);
        }, 1000);

        }).fail(function (jqXHR, textStatus) {
            alert("상품 조회 실패!");
            window.location.href = '/openrun/main'
        });
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
    }else if(status === "WAITING") {
        document.getElementById("countdown-timer").textContent = `판매 기간이 아닙니다! 판매날짜는 : ${eventDate} 입니다!`;
    }else {
        document.getElementById("countdown-timer").textContent = `남은 시간: ${hours}시간 ${minutes}분 ${seconds}초`;
    }
}

function order(){
    // if ($("#quantity").val() < 1) {
    //     alert("구매 수량은 1개 이상이어야 합니다.");
    //     return;
    // }
    // if (!confirm("구매하시겠습니까?")) {
    //     return;
    // }
    // if(localStorage.getItem("Authorization") === null){
    //     alert("로그인이 필요합니다!");
    //     return;
    // }
    // if(status === "WAITING" || status === "CLOSE"){
    //     alert("판매 기간이 아닙니다!");
    //     return;
    // }
    $.ajax({
        type: "POST",
        url: `/api/orders/${productId}`,
        contentType: "application/json",
        data: JSON.stringify({count: $("#quantity").val()}),
    }).done(function (json) {
        console.log(json);
        alert("구매 성공");
    }).fail(function (jqXHR, textStatus) {
        console.log(jqXHR)
        console.log(textStatus)
        alert("구매 실패");
        // window.location.reload();
    });
}