$(function () {
    var mychart_01_01 = echarts.init(document.getElementById('chart_01_01'));
    var mychart_01_02 = echarts.init(document.getElementById('chart_01_02'));
    var mychart_02_01 = echarts.init(document.getElementById('chart_02_01'));
    var mychart_02_02 = echarts.init(document.getElementById('chart_02_02'));
    mychart_01_01.setOption({
        color: ['#22cb64'],
        title: {
            text: '本年门诊工作效率统计',
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
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '22%',
            top: '7%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: ["全院医师日均门诊人次", "日均门诊人次", "日均出诊医师数", "日均出诊医师门诊人次"],
            axisTick: {
                alignWithLabel: true
            },
            axisLabel: {
                interval: 0,
                rotate: 40
            }
        },
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '出诊人次',
                type: 'bar',
                data: [1872, 1001, 1344, 1621],
                barWidth: '17'
            }
        ]
    });
    mychart_01_02.setOption({
        color: ['#22cb64'],
        title: {
            text: '本年本月门诊工作效率统计',
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
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '22%',
            top: '7%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: ["全院医师日均门诊人次", "日均门诊人次", "日均出诊医师数", "日均出诊医师门诊人次"],
            axisTick: {
                alignWithLabel: true
            },
            axisLabel: {
                interval: 0,
                rotate: 40
            }
        },
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '出诊人次',
                type: 'bar',
                data: [165, 97, 189, 198],
                barWidth: '17'
            }
        ]
    });
    mychart_02_01.setOption({
        color: ['#32ce7a', '#0a4592', '#035aab', '#d9191e', '#ea6320', '#ffae22', '#f564c4', '#503123', '#56969a', '#9b9d04'],
        title: {
            text: '本年治疗结果统计情况',
            left: 'center',
            bottom: '10',
            textStyle: {
                fontWeight: 'normal',
                fontSize: 12,
                color: '#000'
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {d}%"
        },
        series: [
            {
                name: '结果比例',
                type: 'pie',
                selectedMode: 'single',
                radius: '70%',
                label: {
                    normal: {
                        formatter: '{b} {c}例\n{d}%'
                    }
                },
                center: ['50%', '48%'],
                data: [
                    {
                        "name": "无效",
                        "value": 721
                    },
                    {
                        "name": "未治",
                        "value": 6787
                    },
                    {
                        "name": "其他",
                        "value": 1219
                    },
                    {
                        "name": "治愈",
                        "value": 223453
                    },
                    {
                        "name": "死亡",
                        "value": 121
                    },
                    {
                        "name": "好转",
                        "value": 60943
                    }
                ]
            }
        ],
        itemStyle: {
            emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        }
    });
    mychart_02_02.setOption({
        color: ['#32ce7a', '#0a4592', '#035aab', '#d9191e', '#ea6320', '#ffae22', '#f564c4', '#503123', '#56969a', '#9b9d04'],
        title: {
            text: '本年本月治疗结果统计情况',
            left: 'center',
            bottom: '10',
            textStyle: {
                fontWeight: 'normal',
                fontSize: 12,
                color: '#000'
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {d}%"
        },
        series: [
            {
                name: '结果比例',
                type: 'pie',
                selectedMode: 'single',
                radius: '70%',
                label: {
                    normal: {
                        formatter: '{b} {c}例\n{d}%'
                    }
                },
                center: ['50%', '48%'],
                data: [
                    {
                        "name": "无效",
                        "value": 88
                    },
                    {
                        "name": "未治",
                        "value": 457
                    },
                    {
                        "name": "其他",
                        "value": 109
                    },
                    {
                        "name": "治愈",
                        "value": 7864
                    },
                    {
                        "name": "死亡",
                        "value": 19
                    },
                    {
                        "name": "好转",
                        "value": 3812
                    }
                ]
            }
        ],
        itemStyle: {
            emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        }
    });
})