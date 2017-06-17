var myGrid;
$(function () {
    var taskIds = getUrlParam('id');
    myGrid = new dhtmlXGridObject('gridbox');
    myGrid.setHeader("序号,步骤名称,复制的记录行数,读,写,输入,输出,更新,拒绝,错误,时间,速度（条记录/秒）")
    myGrid.setColumnIds(",stepname,line_number,read,write,entry,export,update,refuse,error,time,speed");
    myGrid.setColTypes("cntr,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro");
    myGrid.setInitWidths("50,*,130,80,80,80,80,80,80,80,80,150");
    myGrid.setColAlign("center,center,center,center,center,center,center,center,center,center,center,center");
    myGrid.enableTooltips("false,true,true,true,true,true,true,true,true,true,true,true");
    myGrid.enableResizing("false");
    myGrid.enableKeyboardSupport(false);
    myGrid.enableAutoHeight(true);
    myGrid.enableAutoWidth(true);
    myGrid.enableRowsHover(true, 'grid_hover');
    myGrid.init();

    //禁止选中
    myGrid.attachEvent("onBeforeSelect", function (new_row, old_row, new_col_index) {
        return false;
    });
    if (GLOBAL_DEBUG) {
        var data = GLOBAL_JSON.task_situation.data.data;
        myGrid.parse(data, "js");
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.task_situation,
            success: function (res) {
                if (res.status) {
                    myGrid.parse(res.data, "js");
                } else {
                    top.dhtmlx.alert({
                        text: res.messages,
                        title: '提示信息',
                        ok: '确定'
                    });
                }
            }
        });
    }
    //页面跳转
    $('.vice_nav>.vice_name').on('click', function () {
        var url = $(this).attr('hrefurl');
        window.location.href = url + '.html?id=' + taskIds;
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
