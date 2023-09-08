$(document).ready(function() {
    const token = localStorage.getItem("Authorization")
    console.log("token", token)
    $("#nav-items").empty()

    if (token !== null) {
        $('#logout').show();
        $('#mypage').show();
        $('#signup').hide();
        $('#login').hide();
    } else {
        $('#login').show();
        $('#signup').show();
        $('#logout').hide();
        $('#mypage').hide();

    }
});

function signup() {
    let email = $('#signupemail').val();
    let name = $('#signupname').val();
    let password = $('#signuppassword').val();

    console.log(email)
    console.log(name)
    console.log(password)

    $.ajax({
        type: "POST",
        url: `/api/members/signup`,
        contentType: "application/json",
        data: JSON.stringify({memberemail: email, memberpassword: password, membername:name}),
    })
        .done(function (res, status, xhr) {

            alert("Signup Success");

            window.location.href = '/openrun/main'
        })
        .fail(function (jqXHR, textStatus) {
            alert("Signup Fail");
            window.location.href = '/openrun/main'
        });
}


function onLogin() {
    let loginemail = $('#loginemail').val();
    let loginpassword = $('#loginpassword').val();

    $.ajax({
        type: "POST",
        url: `/api/members/login`,
        contentType: "application/json",
        data: JSON.stringify({memberemail: loginemail, memberpassword: loginpassword}),
    })
        .done(function (res, status, xhr) {

            const token = xhr.getResponseHeader('Authorization');
            alert("Login Success");

            localStorage.setItem("Authorization", token)

            window.location.href = '/openrun/main'
        })
        .fail(function (jqXHR, textStatus) {
            alert("Login Fail");
            window.location.href = '/openrun/main'
        });
}
function logout() {
    localStorage.removeItem('Authorization')
    window.location.href = '/openrun/main';
}