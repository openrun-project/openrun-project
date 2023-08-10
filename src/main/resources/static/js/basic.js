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


function logout() {
    localStorage.removeItem('Authorization')
    window.location.href = '/openrun/main';
}