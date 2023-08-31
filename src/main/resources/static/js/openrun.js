var currentPage = 0;
var totalPages = 0;
let searchMode = false;


//페이지 접속시 자동실행
fetchProducts(0); // Fetch initial page on page load

//이게 페이징에서 클릭시
$('#pagination').on('click', 'a', function(e) {
    e.preventDefault(); // Prevent default link behavior

    let clickedText = $(this).text();
    let isNavigationArrow = clickedText === ">>" || clickedText === "<<";
    let targetFunction = searchMode ? searchProducts : fetchProducts;

    if (isNavigationArrow) {
        targetFunction(parseInt($(this).attr("aria-valuetext")) + 1);
    } else {
        let page = parseInt(clickedText.replace("[", "").replace("]", "")) - 1;
        targetFunction(page);
    }
});


function searchProducts(page) {
    let size = 16;
    let value = (page == null || page === "") ? 0 : page;
    searchMode = true;
    //검색어
    var keyword = $("#search-input").val();
    var category = $("#category").val();
    var sortBy = $("#array").val();
    var isAsc = $("#array option:selected").text().endsWith("△");
    var minPrice = $("#minPrice").val();
    var maxPrice = $("#maxPrice").val();

    if (keyword === '' && category === '' && sortBy === '' && minPrice === '' && maxPrice === '') {
        console.log("검색 조건을 입력해주세요.");
        return;
    }

    $.ajax({
        type: "GET",
        url: `/api/products/search`,
        data: {
            size: size,
            page: value,
            keyword: keyword,
            category: category,
            sortBy: sortBy,
            lprice: minPrice,
            gprice: maxPrice,
            isAsc: isAsc
        },
        dataType: "json",
        contentType: "json",
        success: function (data) {
            console.log(data)

            currentPage = data.number;
            totalPages = data.totalPages;
            console.log("data['content'].length()", data['content'].length)
            if (data['content'].length === 0) {
                var productList = $('#product-row');
                productList.empty();
                let html = `<div>
                                    <h1 style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);"> 검색 결과가 없습니다.</h1>
                                </div>`;
                productList.append(html);
                updatePagination();
                return;
            }
            displayData(data['content']);
            updatePagination();
        },
        error: function () {
            alert("실패");
        }
    });
}


function fetchProducts(page) {
    let size = 16;
    $.ajax({
        type: "GET",
        url: `/api/products/openrun?page=${page}&size=${size}`,
        // dataType: "json",
        /*data: JSON.stringify({ page: page, size: 10 }), // Adjust the page size as needed*/
        contentType: "json",
        success: function (data) {
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
                                    <div class="card" onclick="window.location.href='/openrun/detail/${productId}'">
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

    /* << */
    if (startPage > 0) {
        pagination.append('<a href="javascript:void(0);"><<</a>');
    }

    for (var i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            pagination.append('<span class="current-page">[' + (i + 1) + ']</span>');
        } else {
            pagination.append('<a href="javascript:void(0);">' +
                '[' + (i + 1) + ']' +
                '</a>');
        }
    }

    /* >> */
    if (endPage < totalPages - 1) {
        pagination.append('<a href="javascript:void(0);" aria-valuetext="' + endPage + '">>></a>');
    }
}

