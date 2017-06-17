$(function () {

    var id = getUrlParam('id');
    //初始化图表
    var mychart = echarts.init(document.getElementById('my_echart'));

    mychart.setOption({
        color: ['#4F81BD'],
        title: {
            text: '任务完成图表信息',
            left: 'center',
            bottom: '5',
            textStyle: {
                fontWeight: 'normal',
                fontSize: 12,
                color: '#000'
            }
        },
        tooltip: {
            trigger: 'axis',
            formatter: '完成进度：{b}<br />用时：{c}分钟'
        },
        grid: {
            top: '7%',
            left: '4%',
            right: '13%',
            bottom: '10%',
            containLabel: true
        },
        toolbox: {
            show: true,
            right: '6%',
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                dataView: { readOnly: false },
                magicType: {
                    type: ['line', 'bar']
                },
                restore: {},
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            name: '任务完成进度(%)',
            data: [],
        },
        yAxis: {
            type: 'value',
            name: '任务用时(分钟)',
            scale: true,
        },
        series: [
            {
                name: '任务用时(m)',
                type: 'line',
                data: []
            }
        ]
    });

    if (GLOBAL_DEBUG) {
        //本地数据
        localEchartOne(GLOBAL_JSON.task_chart.data);
    } else {
        //ajax加载echart数据
        mychart.showLoading();        
        _ajax({
            url: GLOBAL_AJAX_URL.task_chart,
            data: {},
            success: function (res) {
                mychart.hideLoading();
                if (res.status) {
                    localEchartOne(res.data);
                } else {
                    if (res.messages == 'nomessage') {
                        mychart.showLoading({
                            text: '暂无数据...'
                        });
                    } else {
                        mychart.showLoading({
                            text: res.messages
                        });
                    }
                }
            }
        });
    }

    function localEchartOne(data) {
        mychart.setOption({
            xAxis: {
                data: data.xaxis
            },
            series: [{
                data: data.yaxis
            }]
        });
    }
    //页面跳转
    $('.vice_nav>.vice_name').on('click', function () {
        var url = $(this).attr('hrefurl');
        window.location.href = url + '.html?id=' + id;
    });
    //$('#close_win').on('click', function () {
    //    window.location.href = 'running.html';
    //})
    //获取浏览器参数方法
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }
});