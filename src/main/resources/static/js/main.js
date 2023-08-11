$(document).ready(function () {

    let count = 10;
    $.ajax({
        type: "GET",
        url: `/api/products/wishcount/${count}`,
        contentType: "application/json",
    })
        .done(function (json) {

            json.forEach((data) => {
                let productId = data['id'];
                let productName = data['productName'];
                let productImage = data['productImage'];
                let productCategory = data['category'];
                let price = data['price'];
                let mallName = data['mallName'];

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


            });
        })
        .fail(function (jqXHR, textStatus) {
            alert("Login Fail");
            window.location.href = '/openrun/main'
        });
});



// function onLogin() {
//     let loginemail = $('#loginemail').val();
//     let loginpassword = $('#loginpassword').val();
//
//     $.ajax({
//         type: "POST",
//         url: `/api/members/login`,
//         contentType: "application/json",
//         data: JSON.stringify({memberemail: loginemail, memberpassword: loginpassword}),
//     })
//         .done(function (res, status, xhr) {
//
//             const token = xhr.getResponseHeader('Authorization');
//             alert("Login Success");
//
//             console.log(token);
//
//             localStorage.setItem("Authorization", token)
//
//             // $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
//             //     jqXHR.setRequestHeader('Authorization', token);
//             // });
//             window.location.href = '/openrun/main'
//         })
//         .fail(function (jqXHR, textStatus) {
//             alert("Login Fail");
//             window.location.href = '/openrun/main'
//         });
// }

function getAuthTokenFromCookie() {
    return document.cookie.split(';').find(
            cookie => cookie.trim().startsWith('Authorization='))?.split('=')[1]
        || null;
}


function saveReservation() {
    let token = getAuthTokenFromCookie(); // 쿠키에서 토큰 값 추출

    $.ajax({
        type: "POST",
        url: "/",
        dataType: "json",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token // Authorization 헤더에 토큰 값 추가
        },
        data: JSON.stringify({}),
        success: function () {
            alert("성공");

        },
        error: function () {
            alert("실패");
        }
    })
}