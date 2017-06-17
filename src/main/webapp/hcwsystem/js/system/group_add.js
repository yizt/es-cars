$(function () {
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
  $('#btn_save').click(function () {
    param.code=$.trim($group_code.val());
    param.name=$.trim($name.val());
    param.available=$available.val();
    param.description=$.trim($group_des.val());
    param.pGroups=$pGroup.val();
    top.closePopCallBack(param);
  });
  $('#btn_cancel').click(function () {
    top.closePop();
  });
})