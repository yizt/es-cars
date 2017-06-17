var myGrid;
$(function () {
    myGrid = new dhtmlXGridObject('gridbox');
    myGrid.setHeader("序号,任务名称,版本,任务调度数,任务描述,创建人,最后更新时间,操作")
    myGrid.setColumnIds(",name,version,dispatch,describe,user,lasttime,id");
    myGrid.setColTypes("cntr,ro,ro,ro,ro,ro,ro,button");
    myGrid.setInitWidths("50,*,100,120,180,100,170,100");
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
        var data = GLOBAL_JSON.task_manage.data.data;
        myGrid.parse(data, "js");
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.task_manage,
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
            data.name = $('#taskName').val(),
            data.user = $('#taskUser').val();
        //ajax加载数据，函数
        window.location.reload(true);
    });
})
    
    //删除任务
    function taskDel(id) {
        top.creatPop({
            caption: '删除用户',
            width: 360,
            height: 234,
            title: '您确定要删除该任务吗？删除后将无法找回',
            url: 'html/common/delpop.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.taskDel,
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
            var row_id = val;     // 获取行id
            this.setCValue('<div class="row-event">' + '<span onclick="taskDel(' + row_id + ');" title="删除">删除</span></div>', val);
        }
    }