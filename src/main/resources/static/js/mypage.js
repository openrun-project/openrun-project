let token = localStorage.getItem("Authorization");

var currentPageWish = 0;
var totalPagesWish = 0;

var currentPageOrder = 0;
var totalPagesOrder = 0;

// 페이지 로딩 시 초기 데이터 호출 및 초기 페이징 설정
fetchWishProducts(currentPageWish);
fetchMyOrders(currentPageOrder);

$('#wishpagination').on('click', 'a', function(e) {
    e.preventDefault();
    var page = parseInt($(this).text().replace("[", "").replace("]","")) - 1;
    fetchWishProducts(page);
});

$('#orderpagination').on('click', 'a', function(e) {
    e.preventDefault();
    var page = parseInt($(this).text().replace("[", "").replace("]","")) - 1;
    fetchMyOrders(page);
});

function fetchWishProducts(page) {
    let size = 8;
    $.ajax({
        type: "GET",
        url: `/api/products/wish?page=${page}&size=${size}`,
        contentType: "application/json",
        headers: {
            "Authorization": token
        }
    })
        .done(function (json) {
            totalPagesWish = json.totalPages;
            currentPageWish = page;

            let products = json.content;

            $("#myWish-row").empty();

            products.forEach((data) => {
                displayWishProduct(data);
            });

            updateWishPagination();
        })
        .fail(function (jqXHR, textStatus) {
            if (jqXHR.status === 400) {
            } else {
                alert("Error fetching orders");
                window.location.href = '/openrun/main';
            }
        });
}

function fetchMyOrders(page) {
    let index = page * 10 + 1; // Calculate index based on page
    let size = 10;
    $.ajax({
        type: "GET",
        url: `/api/orders?page=${page}&size=${size}`,
        contentType: "application/json",
        headers: {
            "Authorization": token
        }
    })
        .done(function (json) {
            totalPagesOrder = json.totalPages;
            currentPageOrder = page;

            let orders = json.content;

            $("#purchase-list").empty();

            orders.forEach((order) => {
                displayMyOrders(order, index);
                index++;
            });

            updateOrderPagination();
        })
        .fail(function (jqXHR, textStatus) {
            if (jqXHR.status === 400) {
                // Handle no orders logic
            } else {
                alert("Error fetching orders");
                window.location.href = '/openrun/main';
            }
        });
}

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

function displayWishProduct(data){
    let productId = data['id'];
    let productName = data['productName'];
    let productImage = data['productImage'];
    let price = data['price'];
    let mallName = data['mallName'];

    let html = `<div class="col-md-3">
                    <div class="card" >
                        <img src="${productImage}" class="card-img-top" alt="...">
                        <div class="card-body">
                            <h5 class="card-title" style="cursor: pointer;" onclick="window.location.href='/openrun/detail/${productId}'">${productName}</h5>
                            <p class="card-text">${mallName}</p>
                            <p class="card-text">${price}</p>                                            
                            <a href="/openrun/detail/${productId}" class="btn btn-primary">Buy Now</a> 
                        </div>
                    </div>
                </div>`;




    $("#myWish-row").append(html);
}

function displayMyOrders(order,index){
    let orderId = order['id'];
    let productName = order['productName'];
    let price = order['price'];
    let count = order['count'];

    let total = price * count; // 총 가격 계산

    let row = `<tr id = "myorder">
                    <td>${index++}</td>
                    <td>${productName}</td>
                    <td>${price}</td>
                    <td>${count}</td>
                    <td>${total}</td>
                    <td><button class="btn btn-danger" onclick="cancelOrder(${orderId})">구매 취소</button></td>
               </tr>`
    $('#purchase-list').append(row);
}





function updateWishPagination() {

    let paginationWish = $('#wishpagination');
    paginationWish.empty();

    /*현재 페이지 번호를 기준으로 페이징의 시작 페이지를 계산합니다. 예를 들어, 현재 페이지가 15라면 startPage는 10이 됩니다.*/
    let startPage = Math.floor(currentPageWish / 10) * 10;
    /*페이징의 끝 페이지를 계산합니다. startPage에서 9를 더한 값과 totalPages - 1 중 작은 값을 선택합니다. 이렇게 하면 최대 10개의 페이지 버튼이 표시됩니다.*/
    let endPage = Math.min(startPage + 9, totalPagesWish - 1);

    /* << */
    if (startPage > 0) {
        paginationWish.append('<a href="#" onclick="fetchWishProducts(' + (startPage - 1) + ')">&laquo;</a>');
    }

    for (let i = startPage; i <= endPage; i++) {
        if (i === currentPageWish) {
            paginationWish.append('<span class="current-page">[' + (i + 1) + ']</span>');
        } else {
            paginationWish.append('<a href="#">' +
                '[' + (i + 1) + ']' +
                '</a>');
        }
    }

    /* >> */
    if (endPage < totalPagesWish - 1) {
        paginationWish.append('<a href="#" onclick="fetchWishProducts(' + (endPage + 1) + ')">&raquo;</a>');
    }
}
console.log("paginationWish " + paginationWish)
function updateOrderPagination() {
    let paginationOrder = $('#orderpagination');
    paginationOrder.empty();
    console.log("currentPageOrder  ", currentPageOrder)
    /*현재 페이지 번호를 기준으로 페이징의 시작 페이지를 계산합니다. 예를 들어, 현재 페이지가 15라면 startPage는 10이 됩니다.*/
    let startPage = Math.floor(currentPageOrder / 10) * 10;
    /*페이징의 끝 페이지를 계산합니다. startPage에서 9를 더한 값과 totalPages - 1 중 작은 값을 선택합니다. 이렇게 하면 최대 10개의 페이지 버튼이 표시됩니다.*/
    let endPage = Math.min(startPage + 9, totalPagesOrder - 1);

    /* << */
    if (startPage > 0) {
        paginationOrder.append('<a href="#" onclick="fetchMyOrders(' + (startPage - 1) + ')">&laquo;</a>');
    }

    for (let i = startPage; i <= endPage; i++) {
        if (i === currentPageOrder) {
            paginationOrder.append('<span class="current-page">[' + (i + 1) + ']</span>');
        } else {
            paginationOrder.append('<a href="#">' +
                '[' + (i + 1) + ']' +
                '</a>');
        }
    }

    /* >> */
    if (endPage < totalPagesOrder - 1) {
        paginationOrder.append('<a href="#" onclick="fetchMyOrders(' + (endPage + 1) + ')">&raquo;</a>');
    }
}