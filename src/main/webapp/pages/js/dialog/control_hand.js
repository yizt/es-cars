$(function () {
    var passdata = top.myPop.passdata;
    var $type     = $('#type');
    var $version  = $('#version');
    var $name     = $('#name');
    var $starttime= $('#starttime');
    var $interval = $('#interval');
    var $interval_title = $('#interval_title');
    if (GLOBAL_DEBUG) {
        //赋值
        var paramdata = [
            {
                paramName: 'inputFileName',
                paramValue: 'data_20170204.txt'
            },
            {
                paramName: 'outputFtpIp',
                paramValue: '192.168.0.90'
            },
            {
                paramName: 'outputFtpPort',
                paramValue: '21'
            },
            {
                paramName: 'outputFtpDir',
                paramValue: '/data/output/'
            }
        ];
        //参数数据
        for (var i = 0; i < paramdata.length; i++) {
            $('#param_box').append('<div class="pop_add"><span>参数名：</span> <input value="' + paramdata[i].paramName + '" type="text" class="w120 paramName input_disable" disabled /> <span>参数值：</span> <input type="text" value="' + paramdata[i].paramValue + '" class="w120 paramValue"/> <i onclick="removeData(this)" class="remove">×</i></div>')
        }
        $('#param_box>div:first-child').append('<a onclick="addData()" class="add">新增</a>').find('i').remove();
    } else {
        //详情数据读取
        _ajax({
            url: GLOBAL_AJAX_URL.category_detail,
            data: { id: passdata.id },
            success: function (res) {
                if (res.status) {
                    //参数数据
                    for (var i = 0; i < res.data.length; i++) {
                        $('#param_box').append('<div class="pop_add"><span>参数名：</span> <input value="' + res.data[i].paramName + '" type="text" class="w120 paramName input_disable" disabled /> <span>参数值：</span> <input type="text" value="' + res.data[i].paramValue + '" class="w120 paramValue"/> <i onclick="removeData(this)" class="remove">×</i></div>')
                    }
                    $('#param_box>div:first-child').append('<a onclick="addData()" class="add">新增</a>').find('i').remove();
                    
                } else {
                    top.dhtmlx.alert({
                        text: res.data.msg,
                        title: '提示信息',
                        ok: '确定'
                    });
                }
            }
        });

    }
    $('.btn_sure').click(function () {
        //var type    =  $type.val();
        //if (!type) {
        //    $type.parent().siblings('i').removeClass('hide').text('任务调度名称不能为空');
        //    return false;
        //}
        top.closePopCallBack({
            type:type,
            version:version,
            name :name,
            starttime:starttime,
            interval:interval,
            interval_title:interval_title
        });
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