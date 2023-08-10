function fetchPage(pageNumber) {
    let size = 20

    $.ajax({
        type: "GET",
        url: `/api/products?page=${pageNumber}&size=${size}`,
        contentType: "json",
    })

        .done(function (json) {
            console.log(json)

            let number = json['number'] // 현재 페이지
            let first = json['first'] // 첫 페이지 인가?
            let last = json['last'] // 마지막 페이지 인가?
            let totalElement = json['totalElements'] // 총 요소 갯수
            let totalPages = json['totalPages'] // 총 페이지 갯수

            $("#product-list").empty()
            json['content'].forEach((data) => {
                updateContent(data);

            });
        })

        .fail(function (jqXHR, textStatus) {
            alert("전체 상품을 가지고 올 수 없습니다.");
            window.location.href = '/openrun/main'
        });
}

// 처음 호출
fetchPage(0);


function updateContent(data) {
    console.log("data : ", data)
    let productId = data['id'];
    let productName = data['productName'];
    let productCategory = data['category'];
    let price = data['price'];
    let mallName = data['mallName'];
    let html = `<tr>
                            <th scope="row">${productId}</th>
                            <td>${productCategory}</td>
                            <td>${mallName}</td>
                            <td>${productName}</td>
                            <td>${price}</td>
                        </tr>`

    $("#product-list").append(html)
}

function updatePagination(totalPages, currentPage) {
    let showPage = 10;
    let startPage = currentPage + 1; // 1
    let lastPage = startPage + 9; // 10
    for (let i = 1; i <= (currentPage/10 + 10); i++) {

    }
    $("#pagination").append()
}
