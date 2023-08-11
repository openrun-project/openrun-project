var currentPage = 0;
var totalPages = 0;

let token = localStorage.getItem("Authorization")

//이게 페이징에서 클릭시
$('#pagination').on('click', "a.next-page", function(e) {
    e.preventDefault(); // Prevent default link behavior
    var page = parseInt($(this).text().replace("[", "").replace("]","")) - 1; // 2 - 1
    fetchPage(page)
});

function fetchPage(pageNumber) {
    let size = 20

    $.ajax({
        type: "GET",
        url: `/api/products?page=${pageNumber}&size=${size}`,
        contentType: "application/json",
    })

        .done(function (json) {
            console.log(json)

            currentPage = pageNumber; // 현재 페이지 갱신
            totalPages = json.totalPages; // 전체 페이지 수 설정

            $("#product-list").empty()
            json['content'].forEach((data) => {
                updateContent(data);
            });
            updatePagination();
        })

        .fail(function (jqXHR, textStatus) {
            alert("전체 상품을 가지고 올 수 없습니다.");
            window.location.href = '/openrun/main'
        });
}

// 처음 호출
fetchPage(0);


function updateContent(data) {
    // console.log("data : ", data)
    let productId = data['id'];
    let productName = data['productName'];
    let productCategory = data['category'];
    let price = data['price'];
    let mallName = data['mallName'];
    let html = `<tr class="product-tr" style="cursor: pointer;" onclick="window.location.href='/openrun/detail/${productId}'">
                            <th scope="row">${productId}</th>
                            <td>${productCategory}</td>
                            <td>${mallName}</td>
                            <td>${productName}</td>
                            <td>${price}</td>
                        </tr>`

    $("#product-list").append(html)
}

function updatePagination() {
    var pagination = $('#pagination');

    pagination.empty();

    /*현재 페이지 번호를 기준으로 페이징의 시작 페이지를 계산합니다. 예를 들어, 현재 페이지가 15라면 startPage는 10이 됩니다.*/
    var startPage = Math.floor(currentPage / 10) * 10;
    /*페이징의 끝 페이지를 계산합니다. startPage에서 9를 더한 값과 totalPages - 1 중 작은 값을 선택합니다. 이렇게 하면 최대 10개의 페이지 버튼이 표시됩니다.*/
    var endPage = Math.min(startPage + 9, totalPages - 1);

    /* << */
    if (startPage > 0) {
        pagination.append('<a href="#" onclick="fetchPage(' + (startPage - 1) + ')">&laquo;</a>');
    }

    for (var i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            pagination.append('<span class="current-page">[' + (i + 1) + ']</span>');
        } else {
            pagination.append('<a class="next-page" href="#">' +
                '[' + (i + 1) + ']' +
                '</a>');
        }
    }

    /* >> */
    if (endPage < totalPages - 1) {
        pagination.append('<a href="#" onclick="fetchPage(' + (endPage + 1) + ')">&raquo;</a>');
    }
}