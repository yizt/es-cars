$(function () {
    var taskId = getUrlParam('id');

    if (GLOBAL_DEBUG) {
        
        createLog(GLOBAL_JSON.task_log.data);
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.task_log,
            data: {
                id: id
            },
            success: function (res) {
                if (res.status) {

                    createLog(res.data);
                }
            }
        });
        setInterval(function () {
            _ajax({
                url: GLOBAL_AJAX_URL.task_log,
                data: {
                    id: id
                },
                success: function (res) {
                    if (res.status) {
                        $('#log_list').empty();
                        createLog(res.data);
                    }
                }
            });
        }, 6000)
    }
    function createLog(data) {
        var $ul = $('#log_list');
        for (var i = 0, len = data.length; i < len; i++) {
            var item = data[i];
            $ul.prepend('<li><b>' + (i + 1) + '</b><strong>' + item.installTime + '</strong>&nbsp;-&nbsp;' + item.title + '&nbsp;-&nbsp;' + item.content + '</li>');
        }
    }
    //页面跳转
    $('.vice_nav>.vice_name').on('click', function () {
        var url = $(this).attr('hrefurl');
        window.location.href = url + '.html?id=' + taskId;
    });
    //$('#close_win').on('click', function () {
    //    window.location.href = 'running.html';
    //})
    //获取浏览器参数方法
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }
})