var myGrid;
$(function () {
    myGrid = new dhtmlXGridObject('gridbox');
    myGrid.setHeader("序号,用户名,姓名,手机号,角色,状态,操作")
    myGrid.setColumnIds("ordernum,username,name,phoneNumber,role,status,status");
    myGrid.setColTypes("cntr,ro,ro,ro,ro,ro,button");
    myGrid.setInitWidths("50,180,130,150,140,*,200");
    myGrid.setColAlign("center,center,center,center,center,center,center");
    myGrid.enableTooltips("false,true,true,true,true,true,false");
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
        myGrid.parse(GLOBAL_JSON.user_list.data.users, function () {
            myGrid.forEachRow(function (id) {
                var status_cell = myGrid.cellById(id, 5);
                var status_value = status_cell.getValue();
                if (status_value == 'Enable') {
                    status_cell.setValue('<span style="color:#54c3a5">启用</span>');
                } else {
                    status_cell.setValue('<span style="color:#ff576f">禁用</span>');
                }
                var role_cell = myGrid.cellById(id, 4);
                var role_value = role_cell.getValue();
                if (role_value == 'Administrator') {
                    role_cell.setValue('管理员');
                } else if (role_value == 'User') {
                    role_cell.setValue('普通用户');
                }
            });
        }, "js");
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.user_list,
            success: function (res) {
                if (res.status) {
                    myGrid.parse(res.data, function () {
                        myGrid.forEachRow(function (id) {
                            var status_cell = myGrid.cellById(id, 5);
                            var status_value = status_cell.getValue();
                            if (status_value == 'Enable') {
                                status_cell.setValue('<span style="color:#54c3a5">启用</span>');
                            } else {
                                status_cell.setValue('<span style="color:#ff576f">禁用</span>');
                            }
                            var role_cell = myGrid.cellById(id, 4);
                            var role_value = role_cell.getValue();
                            if (role_value == 'Administrator') {
                                role_cell.setValue('管理员');
                            } else if (role_value == 'User') {
                                role_cell.setValue('普通用户');
                            }
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
    //添加用户
    $('.btn_add').on('click',  function () {
        top.creatPop({
            caption: '添加用户',
            width: 680,
            height: 510,
            url: 'html/common/useraddpop.html'
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
    //用户编辑
    function userEditPop(rid) {
        top.creatPop({
            caption: '修改用户',
            width: 670,
            height: 510,
            url: 'html/common/usereditpop.html'
        });
        top.myPop.passdata = {
            "id": rid,
            "username": myGrid.cellById(rid, 1).getValue(),
            "name": myGrid.cellById(rid, 2).getValue(),
            "phoneNumber": myGrid.cellById(rid, 3).getValue(),
            "role": myGrid.cellById(rid, 4).getValue()
        };
        top.myPop.callback = function (param) {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.user_edit,
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
    //用户删除
    function userDel(id) {
        top.creatPop({
            caption: '删除用户',
            width: 360,
            height: 234,
            title: '您确定要删除这些用户吗？删除后将无法找回',
            url: 'html/common/delpop.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.user_del,
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
    //更改用户状态
    function changeState(user_id) {
        top.creatPop({
            caption: '更改用户状态',
            width: 360,
            height: 234,
            title: '您确定要更改此用户状态',
            url: 'html/common/delpop.html'
        });
        top.myPop.callback = function () {
            if (GLOBAL_DEBUG) {
                window.location.reload(true);
            } else {
                _ajax({
                    url: GLOBAL_AJAX_URL.user_status_edit,
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
            var row_id = this.cell.parentNode.idd;     // 获取行id
            if (val == 'Enable') {
                this.setCValue('<div class="row-event">' + '<span onclick="userEditPop(' + row_id + ');" title="修改">修改</span>' + '<span onclick="userDel(' + row_id + ');" title="删除">删除</span><span onclick="changeState(' + row_id + ');" title="禁用">禁用</span></div>', val);
            } else {
                this.setCValue('<div class="row-event">' + '<span onclick="userEditPop(' + row_id + ');" title="修改">修改</span>' + '<span onclick="userDel(' + row_id + ');" title="删除">删除</span><span onclick="changeState(' + row_id + ');" title="启用">启用</span></div>', val);
            }
        }
    }