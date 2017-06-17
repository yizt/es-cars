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
        var typedata = ["importDataByDay", "importDataByDay", "javascript"];
        var version = ["v 0.1", "v 0.2", "v 0.3"];
        selectionData(typedata, "#type");
        selectionData(version, "#version");
        $type.val(passdata.type);
        $version.val(passdata.version);

        $name.val(passdata.name);
        $starttime.val(passdata.starttime);
        //任意参数默认
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
            $('#param_box').append('<div class="pop_add"><span>参数名：</span> <input value="' + paramdata[i].paramName + '" type="text" class="w120 paramName" /> <span>参数值：</span> <input type="text" value="' + paramdata[i].paramValue + '" class="w120 paramValue"/> <i onclick="removeData(this)" class="remove">×</i></div>')
        }
        $('#param_box>div:first-child').append('<a onclick="addData()" class="add">新增</a>').find('i').remove();

        //select循环读取数据[本地数据使用]
        function selectionData(data, byId) {
            for (var i = 0; i < data.length; i++) {
                $(byId).append("<option value=" + data[i] + ">" + data[i] + "</option>");
            }
        }
    } else {
        //select初始数据:任务类型
        _ajax({
            url: GLOBAL_AJAX_URL.category_business,
            success: function (res) {
                if (res.status) {
                    for (var i = 0, len = res.data.apptypes.length; i < len; i++) {
                        $type.append("<option value=" + res.data.apptypes[i].id + ">" + res.data.apptypes[i].manName + "</option>");
                    }
                } else {
                    top.dhtmlx.alert({
                        text: res.data.msg,
                        title: '提示信息',
                        ok: '确定'
                    });
                }
            }
        });
        //select任务版本
        _ajax({
            url: GLOBAL_AJAX_URL.category_system,
            success: function (res) {
                if (res.status) {
                    for (var i = 0, len = res.data.os.length; i < len; i++) {
                        $version.append("<option value=" + res.data.os[i].id + ">" + res.data.os[i].name + "</option>");
                    }
                } else {
                    top.dhtmlx.alert({
                        text: res.data.msg,
                        title: '提示信息',
                        ok: '确定'
                    });
                }
            }
        });
        //详情数据读取
        _ajax({
            url: GLOBAL_AJAX_URL.category_detail,
            data: { id: passdata.id },
            success: function (res) {
                if (res.status) {
                    $name.val(passdata.name);
                    $starttime.val(passdata.starttime);
                    //参数数据
                    for (var i = 0; i < res.data.length; i++) {
                        $('#param_box').append('<div class="pop_add"><span>参数名：</span> <input value="' + res.data[i].paramName + '" type="text" class="w120 paramName " /> <span>参数值：</span> <input type="text" value="' + res.data[i].paramValue + '" class="w120 paramValue"/> <i onclick="removeData(this)" class="remove">×</i></div>')
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