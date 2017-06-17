$(function () {
    var $type     = $('#type');
    var $version  = $('#version');
    var $name     = $('#name');
    var $starttime= $('#starttime');
    var $interval = $('#interval');
    var $interval_title = $('#interval_title');
    
    $('.btn_sure').click(function () {
        var type    =  $type.val(),
            version =  $version.val(),
            name    = $.trim($name.val()),
            starttime = $.trim($starttime.val()),
            interval  = $.trim($interval.val()),
            interval_title = $interval_title.val();
        if (!type) {
            $type.parent().siblings('i').removeClass('hide').text('任务调度名称不能为空');
            return false;
        }
        if (!version) {
            $version.parent().siblings('i').removeClass('hide').text('开始时间不能为空');
            return false;
        }
        if (!name) {
            $name.parent().siblings('i').removeClass('hide').text('任务调度名称不能为空');
            return false;
        }
        if (!starttime) {
            $starttime.parent().siblings('i').removeClass('hide').text('开始时间不能为空');
            return false;
        }
        if (!interval) {
            $interval_title.parent().siblings('i').removeClass('hide').text('时间间隔不能为空');
            return false;
        }
        top.closePopCallBack({
            type:type,
            version:version,
            name :name,
            starttime:starttime,
            interval:interval,
            interval_title:interval_title
        });
    });
    //隐藏提示
    $('#type, #version, #name, #starttime, #interval').on('focus', function () {
        $(this).parent().siblings('i').addClass('hide');
    });

    //tab切换
    $('.vice_nav>.vice_name').click(function () {
        $('.vice_nav>.vice_name').removeClass('active');
        $(this).addClass('active');

        var showid = $(this).attr('showid');

        $('.pop_group #' + showid).show().siblings('.pop_group .pop_tab').hide();
    });

    $('.btn_cancel').click(function () {
        top.closePop();
    });
})
    //新增
    function addData() {
        $('#param_box').append('<div class="pop_add"><span>参数名：</span> <input type="text" class="w120 paramName" /> <span>参数值：</span> <input type="text" class="w120 paramValue"/> <i onclick="removeData(this)" class="remove">×</i></div>')
    }
    //删除
    function removeData(obj) {
        $(obj).parent('.pop_add').remove();
    }
    //数据拼接
    function paramData() {
        var putdata = [], dataList = $('#param_box').children('.pop_add');
        for (var i = 0; i < dataList.length; i++) {
            var pname  = $.trim($(dataList[i]).children('input.paramName').val());
            var pvalue = $.trim($(dataList[i]).children('input.paramValue').val());
            if (pname != '' && pvalue != '') {
                putdata.push({ paramName: pname, paramValue: pvalue });
            }
        }
        return JSON.stringify(putdata);
    }