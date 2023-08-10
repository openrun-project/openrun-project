var currentPage = 0;
var totalPages = 0;

$(document).ready(function() {
    fetchProducts(0); // Fetch initial page on page load

    $('#pagination').on('click', 'a', function(e) {
        e.preventDefault(); // Prevent default link behavior
        var page = parseInt($(this).text()) - 1;
        fetchProducts(1);
    });
});


function fetchProducts(page) {
    $.ajax({
        type: "GET",
        url: '/api/products/openrun',
        dataType: "json",
        data: { page: page, size: 10 }, // Adjust the page size as needed
        success: function(data) {
            console.log(data)

            currentPage = data.number;
            totalPages = data.totalPages;
            displayData(data['content']);
            updatePagination();
        },
        error: function () {
            alert("실패");
        }
    });
}

function displayData(data) {

    var productList = $('#product-row');
    productList.empty();

    data.forEach(products => {
        let productId = products['id'];
        let productName = products['productName'];
        let productImage = products['productImage'];
        let productCategory = products['category'];
        let price = products['price'];
        let mallName = products['mallName'];

        let html = `<div class="col-md-3">
                                    <div class="card">
                                        <img src="${productImage}" class="card-img-top" alt="...">
                                        <div class="card-body">
                                            <h5 class="card-title">${productName}</h5>
                                            <p class="card-text">${mallName}</p>
                                            <p class="card-text">${price}</p>
                                            <p class="card-text">${productCategory}</p>
                                            <a href="/openrun/detail/${productId}" class="btn btn-primary">Buy Now</a>
                                        </div>
                                    </div>
                                </div>`
        $("#product-row").append(html);
    })
}

function updatePagination() {
    var pagination = $('#pagination');
    pagination.empty();

    /*현재 페이지 번호를 기준으로 페이징의 시작 페이지를 계산합니다. 예를 들어, 현재 페이지가 15라면 startPage는 10이 됩니다.*/
    var startPage = Math.floor(currentPage / 10) * 10;
    /*페이징의 끝 페이지를 계산합니다. startPage에서 9를 더한 값과 totalPages - 1 중 작은 값을 선택합니다. 이렇게 하면 최대 10개의 페이지 버튼이 표시됩니다.*/
    var endPage = Math.min(startPage + 9, totalPages - 1);

    if (startPage > 0) {
        pagination.append('<a href="#" onclick="fetchProducts(' + (startPage - 1) + ')">&laquo;</a>');
    }

    for (var i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            pagination.append('<span class="current-page">[' + (i + 1) + ']</span>');
        } else {
            pagination.append('<a href="#" onclick="fetchProducts(' + i + ')">[' + (i + 1) + ']</a>');
        }
    }

    if (endPage < totalPages - 1) {
        pagination.append('<a href="#" onclick="fetchProducts(' + (endPage + 1) + ')">&raquo;</a>');
    }
}

