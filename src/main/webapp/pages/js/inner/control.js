var myGrid;
$(function () {
    myGrid = new dhtmlXGridObject('gridbox');
    myGrid.setHeader("序号,任务名称,任务调度名称,用户,首次调度时间,下次调度时间,重复时间间隔,操作")
    myGrid.setColumnIds(",name,controlname,user,begingtime,endtime,duration,id");
    myGrid.setColTypes("cntr,ro,ro,ro,ro,ro,ro,button");
    myGrid.setInitWidths("50,*,130,70,155,155,120,180");
    myGrid.setColAlign("center,center,center,center,center,center,center,center");
    myGrid.enableTooltips("false,true,true,true,true,true,true,false");
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
        var data = GLOBAL_JSON.task_control.data.data;
        myGrid.parse(data, "js");
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.task_control,
            data:{},
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
    //按钮事件
    $('#btn_search').on('click', function () {
        var data = {};
            data.name = $('#taskName').val();
        //ajax加载数据，函数
        window.location.reload(true);
    });

    //添加调度
    $('.btn_add').on('click', function () {
        top.creatPop({
            caption: '添加调度',
            width: 630,
            height: 480,
            url: 'html/common/control_add.html'
        });
        top.myPop.callback = function (param) {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.user_add,
                    data: param,
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
    });
})
    
    //修改调度
    function controlEdit(rid) {
        top.creatPop({
            caption: '修改调度',
            width: 630,
            height: 480,
            url: 'html/common/control_edit.html'
        });
        top.myPop.passdata = {
            "id": rid,
            "name": myGrid.cellById(rid, 2).getValue(),
            "starttime": myGrid.cellById(rid, 4).getValue()
        };
        top.myPop.callback = function (param) {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.control_edit,
                    data: param,
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
    };
    //调度删除
    function controlDel(id) {
        top.creatPop({
            caption: '删除调度',
            width: 360,
            height: 234,
            title: '您确定要删除该调度吗？删除后将无法找回',
            url: 'html/common/delpop.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.control_del,
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
    //手动执行
    function controlHand(id) {
        top.creatPop({
            caption: '手动执行调度',
            width: 630,
            height: 480,
            url: 'html/common/control_hand.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.control_hand,
                    data: {
                        id: user_id
                    },
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
            var row_id = val;     // 获取行id
            this.setCValue('<div class="row-event narrow-width">' + '<span onclick="controlEdit(' + row_id + ');" title="修改">修改</span>' + '<span onclick="controlDel(' + row_id + ');" title="删除">删除</span><span onclick="controlHand(' + row_id + ');" title="手动执行">手动执行</span></div>', val);
        }
    }