var myGrid;
$(function () {
    var taskId = getUrlParam('id');
    
    myGrid = new dhtmlXGridObject('gridbox');
    myGrid.setHeader("序号,步骤名称,步骤类型,进度,开始时间,结束时间,运行时间")
    myGrid.setColumnIds(",stepname,steptype,progress,begingtime,endtime,duration");
    myGrid.setColTypes("cntr,link,ro,ro,ro,ro,ro");
    myGrid.setInitWidths("50,*,100,100,200,200,120");
    myGrid.setColAlign("center,center,center,center,center,center,center");
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
        var data = GLOBAL_JSON.task_step.data.data;
        myGrid.parse(data, function () {
            myGrid.forEachRow(function (id) {
                //根据id链接跳转
                myGrid.cellById(id, 1).setValue(myGrid.cellById(id, 1).cell.parentNode._attrs.stepname + '^history_chart.html?id=' + taskId + '^_self');
                myGrid.cellById(id, 3).setValue('<div class="progress"><span style="width:' + myGrid.cellById(id, 1).cell.parentNode._attrs.progress + '%;">'+myGrid.cellById(id, 1).cell.parentNode._attrs.progress + '%</span></div>');
                
            });
        }, "js");
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.task_step.data,
            success: function (res) {
                if (res.status) {
                    myGrid.parse(res.data, function () {
                        myGrid.forEachRow(function (id) {
                            //根据id链接跳转
                            myGrid.cellById(id, 1).setValue(myGrid.cellById(id, 1).cell.parentNode._attrs.stepname + '^history_chart.html?id=' + taskId + '^_self');
                            myGrid.cellById(id, 3).setValue('<div class="progress"><span style="width:' + myGrid.cellById(id, 1).cell.parentNode._attrs.progress + '%;">' + myGrid.cellById(id, 1).cell.parentNode._attrs.progress + '%</span></div>');

                        });
                    }, "js");
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
});