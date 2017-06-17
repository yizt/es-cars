var GLOBAL_DEBUG = true;//是否本地调试，true本地调试,false生产环境
var GLOBAL_IMGS_URL = '/images/';//图片路径
//项目跳转地址管理
var GLOBAL_ADDR_URL = {
    index:'/index.html'//登录成功后，跳转的后台首页
}
//后台ajax请求地址
var GLOBAL_AJAX_URL = {
    login: '/metallurgy/login/isMatching',//登录
    task_manage: '',//任务管理
    task_control:'',//任务调度
    task_running: '',//任务历史列表
    task_history: '',//任务历史列表
    task_stop: '',//任务历史停止操作
    task_restart: '',//任务历史重新执行
    task_chart: '',//任务图表
    task_step: '',//任务步骤
    task_situation: '',//执行情况
    task_log:'',//任务日志
	user_list: '',//用户管理-用户列表
	user_add: '',//用户管理-用户添加
	users_del: '',//用户管理-用户批量删除
	user_del: '',//用户管理-单个用户删除
	user_edit: '',//用户管理-用户编辑
	user_status_edit: '',//用户管理-用户状态更改
	user_info: '',//个人信息管理-获取个人详细信息
	user_info_edit: '',//个人信息管理-修改个人信息
};
//模拟的JSON数据
var GLOBAL_JSON = {
    //登录
    login: {
        username: 'admin',
        password: 'admin'
    },
    //任务管理
    task_manage: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": {
            "msg": "",
            "data": [
                {
                    "id":"1001",
                    "name": "importDataByDay",
                    "version": "v 0.1",
                    "dispatch": "2",
                    "describe": "每天导入数据",
                    "user": "admin",
                    "lasttime": "2016-12-20 00:05:00"
                },
                {
                    "id": "1002",
                    "name": "importDataByDay",
                    "version": "v 0.2",
                    "dispatch": "1",
                    "describe": "每天导入数据",
                    "user": "admin",
                    "lasttime": "2016-12-20 00:05:00"
                },
                {
                    "id": "1003",
                    "name": "importDataByB2",
                    "version": "v 0.1",
                    "dispatch": "1",
                    "describe": "每隔半小时导入数据",
                    "user": "User",
                    "lasttime": "2016-12-20 00:05:00"
                }
            ]
        }
    },
    //任务调度
    task_control: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": {
            "msg": "",
            "data": [
                {
                    "id":"1001",
                    "name": "importDataByDay",
                    "controlname": "每天定时导入",
                    "user": "admin",
                    "begingtime": "2016-12-20 00:00:00",
                    "endtime": "2016-12-21 00:00:00",
                    "duration": "1天"
                },
                {
                    "id": "1002",
                    "name": "importDataByB2",
                    "controlname": "每半小时定时导入",
                    "user": "admin",
                    "begingtime": "2016-12-21 00:00:00",
                    "endtime": "2016-12-20 00:30:00",
                    "duration": "30分钟"
                },
                {
                    "id": "1003",
                    "name": "importFileToDB",
                    "controlname": "每12小时定时导入",
                    "user": "User",
                    "begingtime": "2016-12-22 12:20:10",
                    "endtime": "2016-12-22 00:20:10",
                    "duration": "12h"
                }
            ]
        }
    },
    //运行中任务
    task_running: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": {
            "msg": "",
            "data": [
                {
                    "type": "1",
                    "runid": "5678",
                    "name": "importDataByDay",
                    "user": "admin",
                    "begingtime": "2016-12-20 00:00:00",
                    "endtime": "-",
                    "duration": "5m"
                },
                {
                    "type": "1",
                    "runid": "5679",
                    "name": "importDataByB2",
                    "user": "admin",
                    "begingtime": "2016-12-21 00:00:00",
                    "endtime": "-",
                    "duration": "30s"
                },
                {
                    "type": "2",
                    "runid": "5680",
                    "name": "importFileToDB",
                    "user": "User",
                    "begingtime": "2016-12-22 12:20:10",
                    "endtime": "-",
                    "duration": "1m20s"
                }
            ]
        }
    },
    //任务历史
    task_history: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": {
            "msg": "",
            "data": [
                {
                    "type": "1",
                    "runid": "5678",
                    "name": "importDataByDay",
                    "user": "admin",
                    "begingtime": "2016-12-20 00:00:00",
                    "endtime": "2016-12-20 00:05:00",
                    "duration": "5m",
                    "status": "1"
                },
                {
                    "type": "1",
                    "runid": "5679",
                    "name": "importDataByB2",
                    "user": "admin",
                    "begingtime": "2016-12-21 00:00:00",
                    "endtime": "2016-12-21 00:00:30",
                    "duration": "30s",
                    "status": "1"
                },
                {
                    "type": "2",
                    "runid": "5680",
                    "name": "importFileToDB",
                    "user": "User",
                    "begingtime": "2016-12-22 12:20:10",
                    "endtime": "2016-12-22 12:21:30",
                    "duration": "1m20s",
                    "status": "2"
                }
            ]
        }
    },
    //任务图表折线图
    task_chart: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "messages": "查询成功",
        "data": {
            "xaxis": ['10%', '20%', '30%', '40%', '50%', '60%', '70%', '80%', '90%', '100%'],
            "yaxis": ['12', '22', '28', '32', '36', '37', '40', '48', '55', '78']
        }
    },
    //步骤信息
    task_step: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": {
            "msg": "",
            "data": [
                {
                    "stepname": "copySftpToHdfs",
                    "steptype": "script",
                    "progress": "100",
                    "begingtime": "2016-12-20 00:00:00",
                    "endtime": "2016-12-20 00:05:00",
                    "duration": "5m"
                },
                {
                    "stepname": "compareImportPersonnel",
                    "steptype": "script",
                    "progress": "80",
                    "begingtime": "2016-12-21 00:00:00",
                    "endtime": "2016-12-21 00:00:30",
                    "duration": "30s"
                },
                {
                    "stepname": "fromHdfsToSftp",
                    "steptype": "script",
                    "progress": "80",
                    "begingtime": "2016-12-22 12:20:10",
                    "endtime": "2016-12-22 12:21:30",
                    "duration": "1m20s"
                }
            ]
        }
    },
    //执行情况
    task_situation: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": {
            "msg": "",
            "data": [
                {
                    "stepname": "读取数据库内容",
                    "line_number": "0",
                    "read": "100",
                    "write": "12000",
                    "entry": "12000",
                    "export": "12000",
                    "update": "0",
                    "refuse": "0",
                    "error": "0",
                    "time": "4s",
                    "speed": "3000"
                },
                {
                    "stepname": "数据格式转换",
                    "line_number": "0",
                    "read": "12000",
                    "write": "12000",
                    "entry": "0",
                    "export": "0",
                    "update": "0",
                    "refuse": "0",
                    "error": "0",
                    "time": "10s",
                    "speed": "1200"
                },
                {
                    "stepname": "输出文本文件",
                    "line_number": "0",
                    "read": "12000",
                    "write": "12000",
                    "entry": "0",
                    "export": "12000",
                    "update": "0",
                    "refuse": "0",
                    "error": "0",
                    "time": "2s",
                    "speed": "6000"
                }
                
            ]
        }
    },
    //任务日志
    task_log: {
        "status": true,
        "httpstatus": 200,
        "error_code": 0,
        "data": [
            {
                "installTime": "2016-8-25 19:34:26",
                "title": "compareImportPersonnel",
                "content": "下载安装插件，准备安装",
                "appType": 2,
            },
            {
                "installTime": "2016-8-25 20:38:30",
                "title": "copySftpToHdfs",
                "content": "生成IDX文件，解析文件，配置dll文件",
                "appType": 2,
            },
            {
                "installTime": "2016-8-25 21:38:02",
                "title": "copySftpToHdfs",
                "content": "生成模板，安装PEX模板",
                "appType": 2,
            }
        ]
    },
    //用户列表
	user_list: {
	    "status": true,
	    "httpstatus": 200,
	    "error_code": 0,
	    "data": {
	        "msg": "",
	        "users": [
                {
                    "id": "122",
                    "username": "admin",
                    "name": "平台管理者",
                    "phoneNumber": 15200801269,
                    "role": "Administrator",
                    "status": "Enable"
                },
                {
                    "id": "133",
                    "username": "zhangda",
                    "name": "张达",
                    "phoneNumber": 18684775632,
                    "role": "User",
                    "status": "Enable"
                },
                {
                    "id": "144",
                    "username": "liuwang",
                    "name": "刘望",
                    "phoneNumber": 13773410749,
                    "role": "User",
                    "status": "Disable"
                }
	        ]
	    }
	},
    //个人信息
	user: {
	    "status": true,
	    "data": {
	        "msg": "",
	        "user": {
	            "id": "1",
	            "username": "echmin",
	            "name": "达芬奇斯蒂夫",
	            "phoneNumber":'18974009833',
	            "password": "1436864169"
	        }
	    }
	}
};