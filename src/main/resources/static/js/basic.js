$(document).ready(function() {
    const token = localStorage.getItem("Authorization")
    console.log("token", token)
    $("#nav-items").empty()

    if (token !== null) {
        $('#logout').show();
        $('#signup').hide();
        $('#login').hide();
    } else {
        $('#login').show();
        $('#signup').show();
        $('#logout').hide();
    }
});

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

            console.log(token);

            localStorage.setItem("Authorization", token)

            // $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            //     jqXHR.setRequestHeader('Authorization', token);
            // });
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