$(function () {
    var passdata = top.myPop.passdata;
    var param = {};
    var $name = $('#name');//组织名称
    var $pGroup = $('#pGroup');//上层组织
    var $group_code = $('#group');//组织代码
    var $group_des = $('#group_des');//组织描述
    var $available = $('#available');//组织状态
    //获取所有组织
    _ajax({
        url: GLOBAL_AJAX_URL.getGroups,
        async: false,
        success: function (res) {
            for (var i = 0, len = res.data; i < len; i++) {
                $pGroup.append('<option value="' + res.data[i].id + '">' + res.data[i].name + '</option>');
            }
        }
    });
    $name.val(passdata.name);
    $group_code.val(passdata.code);
    $pGroup.val(passdata.pGroups.id);
    $available.val(passdata.available);
    $group_des.val(passdata.description)
    $('#btn_save').click(function () {
        param.id = passdata.id;
        param.name = $.trim($name.val());
        param.available = $available.val();
        param.pGroups.id = $pGroup.val();
        param.description=$.trim($group_des.val());
        param.code=$.trim($group_code.val());
        top.closePopCallBack(param,$pGroup.find('option:selected').text());
    });
    $('#btn_cancel').click(function () {
        top.closePop();
    });
})