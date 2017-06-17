window.onload = function () {
    document.getElementById("cont_text").innerHTML = decodeURI(getUrlParam('title'));
    document.getElementById("btn_sure").onclick = function () {
        top.closePopCallBack();
    }
    document.getElementById("btn_cancel").onclick = function () {
        top.closePop();
    }
}
//获取浏览器参数方法
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}