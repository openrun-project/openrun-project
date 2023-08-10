$(document).ready(function () {

    let token = localStorage.getItem("Authorization");

    $.ajax({
        type: "GET",
        url: `/api/products/wish`,
        contentType: "application/json",
        headers: {
            "Authorization": token
        }
    })
        .done(function (json) {
            let products = json.content;

            products.forEach((data) => {
                let productId = data['id'];
                let productName = data['productName'];
                let productImage = data['productImage'];
                let price = data['price'];
                let mallName = data['mallName'];
                let count = data['count'];


                let html = `<div class="col-md-3">
                                    <div class="card">
                                        <img src="${productImage}" class="card-img-top" alt="...">
                                        <div class="card-body">
                                            <h5 class="card-title">${productName}</h5>
                                            <p class="card-text">${mallName}</p>
                                            <p class="card-text">${price}</p>                                            
                                            <a href="/openrun/detail/${productId}" class="btn btn-primary">Buy Now</a>
                                        </div>
                                    </div>
                                </div>`

                $("#myWish-row").append(html);


            });
        })
        .fail(function (jqXHR, textStatus) {
            alert("sads");
            window.location.href = '/openrun/main'
        });
});


$(document).ready(function () {
    let token = localStorage.getItem("Authorization");
    console.log("주문목록",token)

    let index = 0;
    $.ajax({
        type: "GET",
        url: `/api/orders`,
        contentType: "application/json",
        headers: {
            "Authorization": token
        }
    })
        .done(function (json) {
            let orders = json.content

            orders.forEach((order) => {

                let orderId = order['id'];
                let productName = order['productName'];
                let price = order['price'];
                let count = order['count'];

                let total = price * count; // 총 가격 계산

                let row = `<tr>
                        <td>${++index}</td>
                        <td>${productName}</td>
                        <td>${price}</td>
                        <td>${count}</td>
                        <td>${total}</td>
                        <td><button class="btn btn-danger" onclick="cancelOrder(${orderId})">구매 취소</button></td>
                       </tr>`
                $('#purchase-list').append(row);
            });
        })
        .fail(function (jqXHR, textStatus) {
            if (jqXHR.status === 400) {
                // 구매 내역이 없을 때의 처리 로직 (예: 아무것도 안 함)
            } else {
                alert("Error fetching orders");
                window.location.href = '/openrun/main';
            }
        });
});

function cancelOrder(orderId) {
    let token = localStorage.getItem("Authorization");

    $.ajax({
        type: "DELETE",
        url: `/api/orders/${orderId}`,
        headers: {
            "Authorization": token
        }
    })
        .done(function (response) {
            alert("구매 취소 성공");
            $(`#order-${orderId}`).remove(); // 해당 주문 행을 테이블에서 제거
            location.reload();
        })
        .fail(function (jqXHR, textStatus) {
            alert("구매 취소 실패");
        });
}