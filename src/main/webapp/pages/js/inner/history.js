var myGrid;
$(function () {
    myGrid = new dhtmlXGridObject('gridbox');
    myGrid.setHeader("序号,任务类型,任务运行ID,任务名称,用户,开始时间,结束时间,运行时间,状态,操作")
    myGrid.setColumnIds(",type,runid,name,user,begingtime,endtime,duration,status,runid");
    myGrid.setColTypes("cntr,ro,link,ro,ro,ro,ro,ro,ro,button");
    myGrid.setInitWidths("50,100,100,*,70,155,155,80,80,180");
    myGrid.setColAlign("center,center,center,center,center,center,center,center,center,center");
    myGrid.enableTooltips("false,true,true,true,true,true,true,true,true,false");
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
        var data = GLOBAL_JSON.task_history.data.data;
        myGrid.parse(data, function () {
            myGrid.forEachRow(function (id) {
                var status_cell = myGrid.cellById(id, 8),
                    status_value = status_cell.getValue();
                if (status_value == '1') {
                    status_cell.setValue('<span style="color:#54c3a5">成功</span>');
                } else {
                    status_cell.setValue('<span style="color:#ff576f">失败</span>');
                }
                
                var taskid = status_cell.cell.parentNode._attrs.runid,
                    typeid = status_cell.cell.parentNode._attrs.type;
                if (typeid == '1') {
                    myGrid.cellById(id, 1).setValue('作业');
                    myGrid.cellById(id, 9).setValue({ type: typeid, rowid: taskid });
                    //根据id链接跳转
                    myGrid.cellById(id, 2).setValue(taskid + '^history_chart.html?id=' + taskid + '^_self');
                } else {
                    myGrid.cellById(id, 1).setValue('转换');
                    myGrid.cellById(id, 9).setValue({ type: typeid, rowid: taskid });
                    //根据id链接跳转
                    myGrid.cellById(id, 2).setValue(taskid + '^../task/history_chart.html?id=' + taskid + '^_self');
                }
            });            
        }, "js");
    } else {
        ajaxDatatable()
    }
    //按钮事件
    $('#btn_search').on('click', function () {
        var data = {};
            data.name = $('#taskName').val(),
            data.user = $('#taskUser').val(),
            data.type = $('#taskType').val();
        //ajax加载数据，函数
        window.location.reload(true);
        ajaxDatatable(data)
    });
})
    //加载数据
    function ajaxDatatable(param) {
        var param = param || '';
        _ajax({
            url: GLOBAL_AJAX_URL.task_history,
            data: {
                name: param.name,
                user: param.user,
                type: param.type
            },
            success: function (res) {
                if (res.status) {
                    myGrid.parse(res.data, function () {
                        var status_cell = myGrid.cellById(id, 8),
                        status_value = status_cell.getValue();
                        if (status_value == '1') {
                            status_cell.setValue('<span style="color:#54c3a5">成功</span>');
                        } else {
                            status_cell.setValue('<span style="color:#ff576f">失败</span>');
                        }

                        var taskid = status_cell.cell.parentNode._attrs.runid,
                            typeid = status_cell.cell.parentNode._attrs.type;
                        if (typeid == '1') {
                            myGrid.cellById(id, 1).setValue('作业');
                            myGrid.cellById(id, 9).setValue({ type: typeid, rowid: taskid });
                            //根据id链接跳转
                            myGrid.cellById(id, 2).setValue(taskid + '^history_chart.html?id=' + taskid + '^_self');
                        } else {
                            myGrid.cellById(id, 1).setValue('转换');
                            myGrid.cellById(id, 9).setValue({ type: typeid, rowid: taskid });
                            //根据id链接跳转
                            myGrid.cellById(id, 2).setValue(taskid + '^../task/history_chart.html?id=' + taskid + '^_self');
                        }
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
    //停止任务
    function stopWork(id) {
        top.creatPop({
            caption: '提示消息',
            width: 360,
            height: 234,
            title: '您确定要停止该任务吗？',
            url: 'html/common/delpop.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.task_stop,
                    data: { id: id },
                    success: function (res) {
                        if (res.status) {
                            window.location.reload(true);
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
        }
    }
    //重新执行
    function restart(id) {
        top.creatPop({
            caption: '提示消息',
            width: 360,
            height: 234,
            title: '您确定要重新执行该任务吗？',
            url: 'html/common/delpop.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.task_restart,
                    data: { id: id },
                    success: function (res) {
                        if (res.status) {
                            window.location.reload(true);
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
        }
    }
    eXcell_button.prototype = new eXcell;
    function eXcell_button(cell) {
        if (cell) {
            this.cell = cell;
            this.grid = this.cell.parentNode.grid;
        }
        this.edit = function () { }
        this.isDisabled = function () { return true; }
        this.setValue = function (val) {
            var data = val;
            if (data.type == '1') {
                this.setCValue('<div class="row-event narrow-width">' + '<span onclick="window.location.href=\'history_chart.html?id=' + data.rowid + '\';" title="任务详情">任务详情</span>' + '<span onclick="stopWork(' + data.rowid + ');" title="停止">停止</span><span onclick="restart(' + data.rowid + ');" title="重新执行">重新执行</span></div>', val);
            } else {
                this.setCValue('<div class="row-event narrow-width">' + '<span onclick="window.location.href=\'../task/history_chart.html?id=' + data.rowid + '\';" title="任务详情">任务详情</span>' + '<span onclick="stopWork(' + data.rowid + ');" title="停止">停止</span><span onclick="restart(' + data.rowid + ');" title="重新执行">重新执行</span></div>', val);
            }
        }
    }