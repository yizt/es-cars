$(function () {
    var userID = localStorage.getItem('userID');
    var $username = $('#username');
    var $password = $('#password');
    var $repassword = $('#repassword');
    var $name = $('#name');
    var $tel = $('#tel');
    if (GLOBAL_DEBUG) {
        var data = GLOBAL_JSON.user.data.user;
        $username.text(data.username);
        $name.val(data.name);
        $tel.val(data.phoneNumber);
    } else {
        _ajax({
            url: GLOBAL_AJAX_URL.user_info,
            data: {
                id: userID
            },
            success: function (res) {
                if (res.status) {
                    var data = res.data.user;
                    $username.text(data.username);
                    $name.val(data.name);
                    $tel.val(data.phoneNumber);
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
    $repassword.on('focus', function () {
        $repassword.siblings('i').addClass('hide');
    });
    $name.on('focus', function () {
        $name.siblings('i').addClass('hide');
    });
    $tel.on('focus', function () {
        $tel.siblings('i').addClass('hide');
    })
    $('#submit_btn').on('click', function () {
        var username = $.trim($username.text());
        var password = $.trim($password.val());
        var repassword = $.trim($repassword.val());
        var name = $.trim($name.val());
        var tel = $.trim($tel.val());
        if (password != repassword) {
            $repassword.siblings('i').removeClass('hide').text('两次输入的密码不一致');
            return false;
        }
        if (!name) {
            $name.siblings('i').removeClass('hide').text('姓名不能为空');
            return false;
        } else if (name.length <= 1) {
            $name.siblings('i').removeClass('hide').text('姓名至少为两个字');
            return false;
        }
        if (!tel) {
            $tel.siblings('i').removeClass('hide').text('手机号码不能为空');
            return false;
        } else if (!(/^1[3|4|5|7|8]\d{9}$/.test(tel))) {
            $tel.siblings('i').removeClass('hide').text('手机号码格式不正确');
            return false;
        }
        if (GLOBAL_DEBUG) {
            window.location.reload(true);            
        } else {
            _ajax({
                url: GLOBAL_AJAX_URL.user_info_edit,
                data: {
                    id: userID,
                    username: username,
                    password: repassword,
                    name: name,
                    phoneNumber: tel
                },
                success: function (res) {
                    if (res.status) {
                        window.location.reload(true);
                        if (res.data.flag) {
                            top.dhtmlx.message.position = "bottom";
                            top.dhtmlx.message({
                                type: "success",
                                text: "成功修改个人信息！"
                            })
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
    });
})