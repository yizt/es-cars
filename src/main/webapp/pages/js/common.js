//封装ajax请求
function _ajax(opt) {
    $.ajax({
        url: opt.url,
        type: 'POST',
        dataType: 'json',
        cache: false,
        data: opt.data,
        success: opt.success
    });
}
//生成分页代码
//$dom:分页节点
//totals：总记录数
//page_size：一页数量
//current_page：当前页数
function generatePagination($dom, totals, page_size, current_page) {
    $dom.find('.sum span').text(totals);//设置总记录数
    var total_page = Math.ceil(parseInt(totals) / parseInt(page_size));//总页数
    if (total_page <= 1) {
        $dom.find('.pagination').addClass('hide');
        return false;
    }
    $dom.find('.pagination').removeClass('hide');
    $dom.find('input').val(current_page);
    $dom.find('.creatli').remove();
    var $first = $dom.find('.first');
    var $prev = $dom.find('.prev');
    var $next = $dom.find('.next');
    var $last = $dom.find('.last');
    $first.removeClass('disabled');
    $prev.removeClass('disabled');
    $next.removeClass('disabled');
    $last.removeClass('disabled');
    if (total_page <= 5) {
        for (var i = 1; i <= total_page; i++) {
            addliPagination($next, i, current_page);
        }
    } else {
        if (current_page <= 3) {
            for (var i = 1; i <= 5; i++) {
                addliPagination($next, i, current_page);
            }
        } else if (current_page >= (total_page - 2)) {
            for (var i = (total_page - 4) ; i <= total_page; i++) {
                addliPagination($next, i, current_page);
            }
        } else {
            for (var i = (current_page - 2) ; i <= (current_page + 2) ; i++) {
                addliPagination($next, i, current_page);
            }
        }
    }
    $prev.attr('pagenum', current_page - 1);
    $next.attr('pagenum', current_page + 1);
    $last.attr('pagenum', total_page);
    if (current_page == 1) {
        $first.addClass('disabled');
        $prev.addClass('disabled');
    }
    if (current_page == total_page) {
        $last.addClass('disabled');
        $next.addClass('disabled');
    }
};
//分页生成li方法
function addliPagination($dom, index, current_page) {
    var $li = $('<li>');
    $li.addClass('creatli').attr('pagenum', index);
    if (current_page == index) {
        $li.addClass('active');
    }
    var $a = $('<a>');
    $a.text(index);
    $li.append($a);
    $dom.before($li);
}
//分页控件各事件注册
function page_event_register(reloadGridData) {
    //分页点击事件
    $('.m-pagenav').on('click', '.pagination li:not(.disabled)', function () {
        var current_page = $(this).attr('pagenum');
        var pagesize = $('.m-pagenav .sum select').val();
        reloadGridData(current_page, pagesize);
    });
    //分页按钮确定点击事件
    $('.m-pagenav').on('click', '.inputbox a', function () {
        var pagesize = $('.m-pagenav .sum select').val();
        var current_page = parseInt($.trim($('.m-pagenav .inputbox input').val()));
        var total_page = parseInt($.trim($('.m-pagenav .pagination .last').attr('pagenum')));
        if (!current_page || current_page == 0) {
            current_page = 1;
        }
        if (current_page > total_page) {
            current_page = total_page;
        }
        reloadGridData(current_page, pagesize);
    });
    //分页输入框回车事件
    $('.m-pagenav').on('keyup', '.inputbox input', function (event) {
        if (event.keyCode == '13') {
            $('.m-pagenav .inputbox a').trigger('click');
        }
    });
    //每页展示多少条改变事件
    $('.m-pagenav .sum select').change(function () {
        var pagesize = $(this).val();
        reloadGridData(1, pagesize);
    });
}
