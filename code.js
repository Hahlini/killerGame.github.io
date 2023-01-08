
function myFunction(){
    const codes = ["9146e7", "cf4ac3", "f4bf19"]
    let code = document.getElementById('input-code').value
    if(codes.includes(code)){
        window.location.href = "folder/" + code + ".html"
    }
    return = false
};
