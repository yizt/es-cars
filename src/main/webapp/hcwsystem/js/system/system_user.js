$(function () {
    var $table = $('#table_userlist');//表格
    var $username = $('#username');//用户名
    var $available = $('#available');//状态
    var params = null;//参数
    if (GLOBAL_DEBUG) {
        //本地
        generatePagination($('.m-pagenav'), 78, 10, 1);
    } else {
        table_data_load({
            pageSize: 10,
            pageIndex: 1,
            sort: 'ASC',
            sortFields: [
                "id",
                "createdTime"
            ],
            userName: '',
            available: ''
        });
    }
    function reloadGridData(current_page, pagesize) {
        var userName = '';
        var available = '';
        if (params) {
            userName = params.userName;
            available = params.available;
        }
        table_data_load({
            pageSize: pagesize,
            pageIndex: current_page,
            sort: 'ASC',
            sortFields: [
                "id",
                "createdTime"
            ],
            userName: userName,
            available: available
        });
    }

    //分页注册事件
    page_event_register(reloadGridData);
    //全选事件
    $('#check_all').click(function () {
        if ($(this).prop('checked')) {
            $table.find('.btn-check').prop('checked', true);
        } else {
            $table.find('.btn-check').prop('checked', false);
        }
    });
    //查询按钮事件
    $('#btn-query').click(function () {
        var userName = $.trim($username.val());//用户名
        var available = $available.val();//状态
        if (GLOBAL_DEBUG) {
            window.location.reload(true);
        } else {
            table_data_load({
                pageSize: 10,
                pageIndex: 1,
                sort: 'ASC',
                sortFields: [
                    "id",
                    "createdTime"
                ],
                userName: userName,
                available: available
            });
        }
    });
    //新增按钮事件
    $('.btn-add').click(function () {
        top.creatPop({
            caption: '用户新增',
            width: 400,
            height: 370,
            url: 'html/system/pophtml/userAdd.html'
        });
        top.myPop.callback = function (param) {
            _ajax({
                url: GLOBAL_AJAX_URL.usersAdd,
                data: param,
                success: function (res) {
                    if (res.status) {
                        window.location.reload(true);
                        top.myPop.close();
                    } else {
                        top.myPop.close();
                        top.dhtmlx.alert({
                            text: res.messages,
                            title: '提示信息',
                            ok: '确定'
                        });
                    }
                }
            });
        }
    });
    //多删除按钮事件
    $('.btn-dels').click(function () {
        var ids = [];
        var trs = [];
        var $btn_checks = $table.find('.btn-check');
        $btn_checks.each(function (index, item) {
            if ($(item).prop('checked')) {
                var $tr = $(item).parents('tr');
                ids.push($tr.data('content').id);
                trs.push($tr);
            }
        })
        if (ids.length <= 0) {
            top.dhtmlx.alert({
                text: '请选择需要删除的用户',
                title: '提示信息',
                ok: '确定'
            });
            return;
        }
        top.dhtmlx.confirm({
            title: "提示信息",
            text: "确定要删除吗？",
            ok: '确定',
            cancel: '取消',
            callback: function (isSure) {
                if (isSure) {
                    _ajax({
                        url: GLOBAL_AJAX_URL.usersDel + '?ids=' + ids.join(','),
                        success: function (res) {
                            if (res.status) {
                                for (var i = 0; i < trs.length; i++) {
                                    trs[i].remove();
                                }
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
            }
        });
    });
    //单删除按钮事件
    $table.on('click', '.btn-del', function () {
        var $this = $(this);
        var $tr = $this.parents('tr');
        top.dhtmlx.confirm({
            title: "提示信息",
            text: "确定要删除吗？",
            ok: '确定',
            cancel: '取消',
            callback: function (isSure) {
                if (isSure) {
                    _ajax({
                        url: GLOBAL_AJAX_URL.usersDel + '?ids=' + $tr.data('content').id,
                        success: function (res) {
                            if (res.status) {
                                $tr.remove();
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
            }
        });
    });
    //详情编辑事件
    $table.on('click', '.btn-details', function () {
        var $this = $(this);
        var $tr = $this.parents('tr');
        var data = $tr.data('content');
        top.creatPop({
            caption: '用户详情编辑',
            width: 400,
            height: 370,
            url: 'html/system/pophtml/userDetails.html'
        });
        top.myPop.passdata = data;
        top.myPop.callback = function (param) {
            _ajax({
                url: GLOBAL_AJAX_URL.usersUpdate,
                data: param,
                success: function (res) {
                    if (res.status) {
                        $tr.find('.data-name').text(param.name);
                        $tr.find('.data-role').text(param.roleCodeName.join(','));
                        $tr.find('.data-email').text(param.email);
                        if (param.available == '0') {
                            $tr.find('.data-available').text('禁用');
                        } else {
                            $tr.find('.data-available').text('启用');
                        }
                        top.myPop.close();
                    } else {
                        top.myPop.close();
                        top.dhtmlx.alert({
                            text: res.messages,
                            title: '提示信息',
                            ok: '确定'
                        });
                    }
                }
            });
        }
    })
    //封装加载表格数据函数
    function table_data_load(param) {
        _ajax({
            url: GLOBAL_AJAX_URL.usersQuery,
            data: {
                pageSize: param.pagesize,
                pageIndex: param.current_page,
                sort: param.sort,
                sortFields: param.sortFields,
                userName: param.userName,
                available: param.available
            },
            success: function (res) {
                if (res.status) {
                    $table.find('.table-content').remove();
                    var items = res.data.content;
                    for (var i = 0, len = items.length; i < len; i++) {
                        var item = items[i];
                        var available_status = '启用';
                        var email = '暂无';
                        var role = [];
                        for (var j = 0, jlen = item.roles.length; j < jlen; j++) {
                            role.push(item.roles[j].name);
                        }
                        if (item.available == '1') {
                            available_status = '启用';
                        } else if (item.available == '3') {
                            available_status = '禁用';
                        } else {
                            available_status = '禁用';
                        }
                        if (item.email) {
                            email = item.email;
                        }
                        var $tr = $('<tr><td><input class="btn-check" type="checkbox"/></td><td class="data-name">' + item.userName + '</td><td class="data-role">' + role.join(',') + '</td><td class="data-email">' + email + '</td><td class="data-available">' + available_status + '</td><td><a class="btn-details">详情编辑</a><a class="btn-del">删除</a></td></tr>').data('content', item);
                        $table.append($tr);
                    }
                    //生成分页
                    generatePagination($('.m-pagenav'), res.data.total, res.data.pageNums, res.data.numberOfPages);
                    params = {
                        sort: param.sort,
                        sortFields: param.sortFields,
                        userName: param.userName,
                        available: param.available
                    }
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
})