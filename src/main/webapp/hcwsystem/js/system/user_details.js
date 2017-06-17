$(function () {
  var passdata = top.myPop.passdata;
  var param = {};
  var $name = $('#name');//用户姓名
  var $email = $('#email');//邮箱
  var $group = $('#group');//所属组织
  var $roles = $('#roles');//用户角色
  var $available = $('#available');//用户状态
  //获取所有组织
  _ajax({
    url: GLOBAL_AJAX_URL.getGroups,
    async: false,
    success: function (res) {
      for (var i = 0, len = res.data; i < len; i++) {
        $group.append('<option value="' + res.data[i].id + '">' + res.data[i].name + '</option>');
      }
    }
  });
  //获取所有角色
  _ajax({
    url: GLOBAL_AJAX_URL.getRoles,
    async: false,
    success: function (res) {
      for (var i = 0, len = res.data; i < len; i++) {
        $roles.append('<input class="check-box" _name="' + res.data[i].name + '" id="role_' + i + '" value="' + res.data[i].code + '" type="checkbox"/><label for= "role_' + i + '" > '+res.data.name+' < / label > ');
      }
    }
  });
  var $roleinputs=$roles.find('.check-box');
  $name.val(passdata.name);
  $email.val(passdata.email);
  $group.val(passdata.groups.id);
  $available.val(passdata.available);
  for(var i=0,len=passdata.roles.length;i<len;i++){
    $roleinputs.each(function(index,item){
      if($(item).val()==passdata.roles[i].code){
        $(item).prop('checked',true);
      }
    });
  }
  $('#btn_save').click(function () {
    param.id=passdata.id;
    param.email=$.trim($email.val());
    param.name=$.trim($name.val());
    param.available=$available.val();
    param.groupsId=$group.val();
    param.roleCodes=[];
    param.roleCodeName=[];
    $roleinputs.each(function(index,item){
      if($(item).prop('checked')){
        param.roleCodes.push($(item).val());
        param.roleCodeName.push($(item).attr('_name'));
      }
    });
    top.closePopCallBack(param);
  });
  $('#btn_cancel').click(function () {
    top.closePop();
  });
})