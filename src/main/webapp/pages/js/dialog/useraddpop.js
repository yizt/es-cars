$(function () {
    var $username = $('#username');
    var $password = $('#password');
    var $repassword = $('#repassword');
    var $name = $('#name');
    var $tel = $('#tel');
    var $role = $('#role');
    //隐藏提示
    $('#username, #password, #repassword, #name, #tel').on('focus', function () {
        $(this).parent().siblings('i').addClass('hide');
    });
    $('.btn_sure').click(function () {
        var username   = $.trim($username.val()),
            password   = $.trim($password.val()),
            repassword = $.trim($repassword.val()),
            name       = $.trim($name.val()),
            tel        = $.trim($tel.val()),
            role       = $.trim($role.val());
        if (!username) {
            $username.parent().siblings('i').removeClass('hide').text('用户名不能为空');
            return false;
        }
        if (password.length <= 0) {
            $password.parent().siblings('i').removeClass('hide').text('密码不能为空');
            return false;
        }
        if (password != repassword) {
            $repassword.parent().siblings('i').removeClass('hide').text('两次输入的密码不一致');
            return false;
        }
        if (!name) {
            $name.parent().siblings('i').removeClass('hide').text('用户名不能为空');
            return false;
        } else if (name.length < 2) {
            $name.parent().siblings('i').removeClass('hide').text('用户名不能少于两个字');
            return false;
        }
        if (!tel) {
            $tel.parent().siblings('i').removeClass('hide').text('手机号码不能为空');
            return false;
        } else if (!(/^1[3|4|5|7|8]\d{9}$/.test(tel))) {
            $tel.parent().siblings('i').removeClass('hide').text('手机号码格式不正确');
            return false;
        }
        top.closePopCallBack({
            username: username,
            password: password,
            name: name,
            phoneNumber: tel,
            role: role
        });
    });
    $('.btn_cancel').click(function () {
        top.closePop();
    });
})