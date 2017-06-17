$(function () {
  /*初始化时间控件*/
  init_time_control();
  var mychart_02_01 = echarts.init(document.getElementById('chart_02_01'));
  var mychart_02_02 = echarts.init(document.getElementById('chart_02_02'));
  var mychart_03_01 = echarts.init(document.getElementById('chart_03_01'));
  var mychart_04_01 = echarts.init(document.getElementById('chart_04_01'));
  var mychart_05_01 = echarts.init(document.getElementById('chart_05_01'));
  var mychart_06_01 = echarts.init(document.getElementById('chart_06_01'));
  var mychart_08_01 = echarts.init(document.getElementById('chart_08_01'));
  if (GLOBAL_DEBUG) {
    var data_03_01 = {
      "xaxis": [],
      "yaxis": []
    };
    for (var i = GLOBAL_JSON.bigdepartmentFeesTop10.length - 1, len = GLOBAL_JSON.bigdepartmentFeesTop10.length; i >= 0; i--) {
      data_03_01.xaxis.push(GLOBAL_JSON.bigdepartmentFeesTop10[i][1]);
      data_03_01.yaxis.push(GLOBAL_JSON.bigdepartmentFeesTop10[i][0]);
    }
    var data_04_01 = {
      "xaxis": [],
      "yaxis": []
    };
    for (var i = GLOBAL_JSON.hospitalizeddepartmentFeesTop10.average.length - 1, len = GLOBAL_JSON.hospitalizeddepartmentFeesTop10.average.length; i >= 0; i--) {
      data_04_01.xaxis.push(GLOBAL_JSON.hospitalizeddepartmentFeesTop10.average[i][1]);
      data_04_01.yaxis.push(GLOBAL_JSON.hospitalizeddepartmentFeesTop10.average[i][0]);
    }
    var data_05_01 = {
      "xaxis": [],
      "yaxis": []
    };
    for (var i = GLOBAL_JSON.hospitalizeddepartmentFeesTop10.mantimes.length - 1, len = GLOBAL_JSON.hospitalizeddepartmentFeesTop10.mantimes.length; i >= 0; i--) {
      data_05_01.xaxis.push(GLOBAL_JSON.hospitalizeddepartmentFeesTop10.mantimes[i][1]);
      data_05_01.yaxis.push(GLOBAL_JSON.hospitalizeddepartmentFeesTop10.mantimes[i][0]);
    }
    var data_06_01 = {
      "xaxis": [],
      "yaxis": []
    };
    for (var i = GLOBAL_JSON.hospitalizeddepartmentFeesTop10.sumcost.length - 1, len = GLOBAL_JSON.hospitalizeddepartmentFeesTop10.sumcost.length; i >= 0; i--) {
      data_06_01.xaxis.push(GLOBAL_JSON.hospitalizeddepartmentFeesTop10.sumcost[i][1]);
      data_06_01.yaxis.push(GLOBAL_JSON.hospitalizeddepartmentFeesTop10.sumcost[i][0]);
    }
    var data_08_01 = {
      "xaxis": [],
      "yaxis": []
    };
    for (var i = GLOBAL_JSON.hospitalizeddepartmentItemFeesTop10.length - 1, len = GLOBAL_JSON.hospitalizeddepartmentItemFeesTop10.length; i >= 0; i--) {
      data_08_01.xaxis.push(GLOBAL_JSON.hospitalizeddepartmentItemFeesTop10[i][1]);
      data_08_01.yaxis.push(GLOBAL_JSON.hospitalizeddepartmentItemFeesTop10[i][0]);
    }
    //本地数据
    mychart_02_01.setOption({
      color: ['#32ce7a', '#0a4592', '#035aab', '#d9191e', '#ea6320', '#ffae22', '#f564c4', '#503123', '#56969a', '#9b9d04'],
      title: {
        text: '2017年住院均次费用构成比例',
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
          name: '构成比例',
          type: 'pie',
          selectedMode: 'single',
          radius: '70%',
          label: {
            normal: {
              formatter: '{b} {c}例\n{d}%'
            }
          },
          center: ['50%', '48%'],
          data: GLOBAL_JSON.hospitalizedCostConstitute
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
        text: '2017年05月住院均次费用构成比例',
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
          name: '构成比例',
          type: 'pie',
          selectedMode: 'single',
          radius: '70%',
          label: {
            normal: {
              formatter: '{b} {c}例\n{d}%'
            }
          },
          center: ['50%', '48%'],
          data: GLOBAL_JSON.hospitalizedCostConstitute
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
    mychart_03_01.setOption({
      color: ['#d9191e'],
      title: {
        text: '2017年大项目各科室均次费用Top10',
        left: 'center',
        bottom: '10',
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
        bottom: '8%',
        top: '7%',
        containLabel: true
      },
      yAxis: {
        type: 'category',
        data: data_03_01.yaxis,
        axisTick: {
          alignWithLabel: true
        }
      },
      xAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: '费用',
          type: 'bar',
          barWidth: 17,
          data: data_03_01.xaxis
        }
      ]
    });
    mychart_04_01.setOption({
      color: ['#d9191e'],
      title: {
        text: '2017年住院各科室均次费用Top10',
        left: 'center',
        bottom: '10',
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
        bottom: '8%',
        top: '7%',
        containLabel: true
      },
      yAxis: {
        type: 'category',
        data: data_04_01.yaxis,
        axisTick: {
          alignWithLabel: true
        }
      },
      xAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: '费用',
          type: 'bar',
          barWidth: 17,
          data: data_04_01.xaxis
        }
      ]
    });
    mychart_05_01.setOption({
      color: ['#d9191e'],
      title: {
        text: '2017年住院各科室人次Top10',
        left: 'center',
        bottom: '10',
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
        bottom: '8%',
        top: '7%',
        containLabel: true
      },
      yAxis: {
        type: 'category',
        data: data_05_01.yaxis,
        axisTick: {
          alignWithLabel: true
        }
      },
      xAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: '人次',
          type: 'bar',
          barWidth: 17,
          data: data_05_01.xaxis
        }
      ]
    });
    mychart_06_01.setOption({
      color: ['#d9191e'],
      title: {
        text: '2017年住院各科室总费用Top10',
        left: 'center',
        bottom: '10',
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
        bottom: '8%',
        top: '7%',
        containLabel: true
      },
      yAxis: {
        type: 'category',
        data: data_06_01.yaxis,
        axisTick: {
          alignWithLabel: true
        }
      },
      xAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: '费用',
          type: 'bar',
          barWidth: 17,
          data: data_06_01.xaxis
        }
      ]
    });
    mychart_08_01.setOption({
      color: ['#d9191e'],
      title: {
        text: '2017年科室住院均次费用项目贡献度排行Top10',
        left: 'center',
        bottom: '10',
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
        bottom: '8%',
        top: '7%',
        containLabel: true
      },
      yAxis: {
        type: 'category',
        data: data_08_01.yaxis,
        axisTick: {
          alignWithLabel: true
        }
      },
      xAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: '费用',
          type: 'bar',
          barWidth: 17,
          data: data_08_01.xaxis
        }
      ]
    });
  } else {
    //远程数据
    ajax_panel_01_02();
    ajax_chart_02_01();
    ajax_chart_02_02();
    ajax_chart_03_01();
    ajax_chart_04_01();
    ajax_chart_05_01();
    ajax_chart_06_01();
    ajax_chart_08_01();
    // ajax_list_03_01();
    // ajax_list_04_01();
    // ajax_list_05_01();
    // ajax_list_06_01();
    // ajax_list_08_01();
    ajax_table_07_01()
  }
  /*所有图表点击事件开始*/
  $('#querybtn_01_02').click(function () {
    ajax_panel_01_02();
  });
  $('#querybtn_02_01').click(function () {
    ajax_chart_02_01();
  });
  $('#querybtn_02_02').click(function () {
    ajax_chart_02_02();
  });
  $('#querybtn_03_01').click(function () {
    ajax_chart_03_01();
  });
  $('#querybtn_04_01').click(function () {
    ajax_chart_04_01();
  });
  $('#querybtn_05_01').click(function () {
    ajax_chart_05_01();
  });
  $('#querybtn_06_01').click(function () {
    ajax_chart_06_01();
  });
  $('#querybtn_08_01').click(function () {
    ajax_chart_08_01();
  });
  // $('#querybtn_03_01').click(function () {
  //     ajax_list_03_01();
  // });
  // $('#querybtn_04_01').click(function () {
  //     ajax_list_04_01();
  // });
  // $('#querybtn_05_01').click(function () {
  //     ajax_list_05_01();
  // });
  // $('#querybtn_06_01').click(function () {
  //     ajax_list_06_01();
  // });
  $('#querybtn_07_01').click(function () {
    ajax_table_07_01();
  });
  // $('#querybtn_08_01').click(function () {
  //     ajax_list_08_01();
  // });
  /*所有数据远程请求方法开始*/
  function ajax_panel_01_02() {
    var year = $('#year_01_02').val();
    var month = $('#month_01_02').val();
    var department = $('#department_01_02').val();
    var feetype = $('#feetype_01_02').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizedCostPer,
      data: JSON.stringify({
        year: year,
        month: month,
        department: department,
        feetype: feetype
      }),
      success: function (res) {
        if (res.status) {
          $('#title_01_02').text(year + '年' + month + '月' + department + feetype + '住院均次费用同比、环比统计');
          $('#cost_01_02').text(parseFloat(res.data.cost).toFixed(2));
          $('#period_01_02').text(parseFloat(res.data.yoy).toFixed(2) + '%');
          $('#per_year_01_02').text(parseFloat(res.data.qoq).toFixed(2) + '%');
          if (res.data.yoy_flag == 'up') {
            $('#period_01_02').removeClass('flag-up').removeClass('flag-down').addClass('flag-up');
          } else if (res.data.yoy_flag == 'down') {
            $('#period_01_02').removeClass('flag-up').removeClass('flag-down').addClass('flag-down');
          } else {
            $('#period_01_02').removeClass('flag-up').removeClass('flag-down')
          }
          if (res.data.qoq_flag == 'up') {
            $('#per_year_01_02').removeClass('flag-up').removeClass('flag-down').addClass('flag-up');
          } else if (res.data.qoq_flag == 'down') {
            $('#per_year_01_02').removeClass('flag-up').removeClass('flag-down').addClass('flag-down');
          } else {
            $('#per_year_01_02').removeClass('flag-up').removeClass('flag-down')
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

  function ajax_chart_02_01() {
    var year = $('#year_02_01').val();
    var department = $('#department_02_01').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizedCostConstitute,
      data: JSON.stringify({
        year: year,
        department: department
      }),
      success: function (res) {
        if (res.status) {
          chart_clear_error($('#mychart_02_01'));
          var data_seriesdata = [];
          for (var i = 0, len = res.data.seriesdata.length; i < len; i++) {
            data_seriesdata.push({
              "name": res.data.seriesdata[i].name,
              "value": parseFloat(res.data.seriesdata[i].value).toFixed(2)
            });
          }
          mychart_02_01.setOption({
            color: ['#32ce7a', '#0a4592', '#035aab', '#d9191e', '#ea6320', '#ffae22', '#f564c4', '#503123', '#56969a', '#9b9d04'],
            title: {
              text: year + department + '年住院均次费用构成比例',
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
                name: '构成比例',
                type: 'pie',
                selectedMode: 'single',
                radius: '75%',
                label: {
                  normal: {
                    formatter: '{b} {c}\n{d}%'
                  }
                },
                center: ['50%', '48%'],
                data: data_seriesdata
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
        } else {
          mychart_02_01.clear();
          chart_append_error($('#mychart_02_01'), res.messages);
        }
      }
    });
  }

  function ajax_chart_02_02() {
    var year = $('#year_02_02').val();
    var month = $('#month_02_02').val();
    var department = $('#department_02_02').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizedCostConstitute,
      data: JSON.stringify({
        year: year,
        month: month,
        department: department
      }),
      success: function (res) {
        if (res.status) {
          chart_clear_error($('#mychart_02_02'));
          var data_seriesdata = [];
          for (var i = 0, len = res.data.seriesdata.length; i < len; i++) {
            data_seriesdata.push({
              "name": res.data.seriesdata[i].name,
              "value": parseFloat(res.data.seriesdata[i].value).toFixed(2)
            });
          }
          mychart_02_02.setOption({
            color: ['#32ce7a', '#0a4592', '#035aab', '#d9191e', '#ea6320', '#ffae22', '#f564c4', '#503123', '#56969a', '#9b9d04'],
            title: {
              text: year + '年' + month + '月' + department + '院均次费用构成比例',
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
                name: '构成比例',
                type: 'pie',
                selectedMode: 'single',
                radius: '70%',
                label: {
                  normal: {
                    formatter: '{b} {c}\n{d}%'
                  }
                },
                center: ['50%', '48%'],
                data: data_seriesdata
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
        } else {
          mychart_02_02.clear();
          chart_append_error($('#mychart_02_02'), res.messages);
        }
      }
    });
  }

  function ajax_chart_03_01() {
    var year = $('#year_03_01').val();
    var feetype = $('#feetype_03_01').val();
    _ajax({
      url: GLOBAL_AJAX_URL.bigdepartmentFeesTop10,
      data: JSON.stringify({
        year: year,
        feetype: feetype
      }),
      success: function (res) {
        if (res.status) {
          var data = {
            "xaxis": [],
            "yaxis": []
          };
          for (var i = res.data.length - 1; i >= 0; i--) {
            data.xaxis.push(res.data[i][1]);
            data.yaxis.push(res.data[i][0]);
          }
          chart_clear_error($('#mychart_03_01'));
          mychart_03_01.setOption({
            color: ['#d9191e'],
            title: {
              text: year + '年' + feetype + '大项目各科室均次费用Top10',
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
            yAxis: {
              type: 'category',
              data: data.yaxis,
              axisTick: {
                alignWithLabel: true
              }
            },
            xAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '费用',
                type: 'bar',
                data: data.xaxis,
                barWidth: '17'
              }
            ]
          });
        } else {
          mychart_03_01.clear();
          chart_append_error($('#mychart_03_01'), res.messages);
        }
      }
    });
  }

  function ajax_chart_04_01() {
    var year = $('#year_04_01').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizeddepartmentFeesTop10,
      data: JSON.stringify({
        year: year
      }),
      success: function (res) {
        if (res.status) {
          var data = {
            "xaxis": [],
            "yaxis": []
          };
          for (var i = res.data.average.length - 1; i >= 0; i--) {
            data.xaxis.push(res.data.average[i][1]);
            data.yaxis.push(res.data.average[i][0]);
          }
          chart_clear_error($('#mychart_04_01'));
          mychart_04_01.setOption({
            color: ['#d9191e'],
            title: {
              text: year + '年' + '住院各科室均次费用Top10',
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
            yAxis: {
              type: 'category',
              data: data.yaxis,
              axisTick: {
                alignWithLabel: true
              }
            },
            xAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '费用',
                type: 'bar',
                data: data.xaxis,
                barWidth: '17'
              }
            ]
          });
        } else {
          mychart_04_01.clear();
          chart_append_error($('#mychart_04_01'), res.messages);
        }
      }
    });
  }

  function ajax_chart_05_01() {
    var year = $('#year_05_01').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizeddepartmentFeesTop10,
      data: JSON.stringify({
        year: year
      }),
      success: function (res) {
        if (res.status) {
          var data = {
            "xaxis": [],
            "yaxis": []
          };
          for (var i = res.data.mantimes.length - 1; i >= 0; i--) {
            data.xaxis.push(res.data.mantimes[i][1]);
            data.yaxis.push(res.data.mantimes[i][0]);
          }
          chart_clear_error($('#mychart_05_01'));
          mychart_05_01.setOption({
            color: ['#d9191e'],
            title: {
              text: year + '年' + '住院各科室人次Top10',
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
            yAxis: {
              type: 'category',
              data: data.yaxis,
              axisTick: {
                alignWithLabel: true
              }
            },
            xAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '人次',
                type: 'bar',
                data: data.xaxis,
                barWidth: '17'
              }
            ]
          });
        } else {
          mychart_05_01.clear();
          chart_append_error($('#mychart_05_01'), res.messages);
        }
      }
    });
  }

  function ajax_chart_06_01() {
    var year = $('#year_06_01').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizeddepartmentFeesTop10,
      data: JSON.stringify({
        year: year
      }),
      success: function (res) {
        if (res.status) {
          var data = {
            "xaxis": [],
            "yaxis": []
          };
          for (var i = res.data.sumcost.length - 1; i >= 0; i--) {
            data.xaxis.push(res.data.sumcost[i][1]);
            data.yaxis.push(res.data.sumcost[i][0]);
          }
          chart_clear_error($('#mychart_06_01'));
          mychart_06_01.setOption({
            color: ['#d9191e'],
            title: {
              text: year + '年' + '住院各科室总费用Top10',
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
            yAxis: {
              type: 'category',
              data: data.yaxis,
              axisTick: {
                alignWithLabel: true
              }
            },
            xAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '费用',
                type: 'bar',
                data: data.xaxis,
                barWidth: '17'
              }
            ]
          });
        } else {
          mychart_06_01.clear();
          chart_append_error($('#mychart_06_01'), res.messages);
        }
      }
    });
  }

  function ajax_chart_08_01() {
    var year = $('#year_08_01').val();
    var department = $('#department_08_01').val();
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalizeddepartmentItemFeesTop10,
      data: JSON.stringify({
        year: year,
        department: department
      }),
      success: function (res) {
        if (res.status) {
          var data = {
            "xaxis": [],
            "yaxis": []
          };
          for (var i = res.data.length - 1; i >= 0; i--) {
            data.xaxis.push(res.data[i][1]);
            data.yaxis.push(res.data[i][0]);
          }
          chart_clear_error($('#mychart_08_01'));
          mychart_08_01.setOption({
            color: ['#d9191e'],
            title: {
              text: year + '年' + department + '住院均次费用项目贡献度排行Top10',
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
            yAxis: {
              type: 'category',
              data: data.yaxis,
              axisTick: {
                alignWithLabel: true
              }
            },
            xAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '费用',
                type: 'bar',
                data: data.xaxis,
                barWidth: '17'
              }
            ]
          });
        } else {
          mychart_08_01.clear();
          chart_append_error($('#mychart_08_01'), res.messages);
        }
      }
    });
  }

  // function ajax_list_03_01() {
  //     var year = $('#year_03_01').val();
  //     var $title=$('#title_03_01');
  //     var $list=$('#list_03_01');
  //     _ajax({
  //         url: GLOBAL_AJAX_URL.bigdepartmentFeesTop10,
  //         data:JSON.stringify({
  //             year: year
  //         }),
  //         success: function (res) {
  //             if (res.status) {
  //                 var items=res.data;
  //                 $title.text(year+'年大项目各科室均次费用Top10');
  //                 $list.find('dd').remove();
  //                 for(var i=0,len=items.length;i<len;i++){
  //                     $list.append('<dd class="clearfix"> <span class="pull-left">'+items[i][0]+'</span> <span class="pull-right cost">'+parseFloat(items[i][1]).toFixed(2)+'</span></dd>');
  //                 }
  //             } else {
  //                 top.dhtmlx.alert({
  //                     text: res.messages,
  //                     title: '提示信息',
  //                     ok: '确定'
  //                 });
  //             }
  //         }
  //     });
  // }
  // function ajax_list_04_01() {
  //     var year = $('#year_04_01').val();
  //     var $title=$('#title_04_01');
  //     var $list=$('#list_04_01');
  //     _ajax({
  //         url: GLOBAL_AJAX_URL.hospitalizeddepartmentFeesTop10,
  //         data:JSON.stringify({
  //             year: year
  //         }),
  //         success: function (res) {
  //             if (res.status) {
  //                 var items=res.data.average;
  //                 $title.text(year+'年住院各科室均次费用Top10');
  //                 $list.find('dd').remove();
  //                 for(var i=0,len=items.length;i<len;i++){
  //                     $list.append('<dd class="clearfix"> <span class="pull-left">'+items[i][0]+'</span> <span class="pull-right cost">'+parseFloat(items[i][1]).toFixed(2)+'</span></dd>');
  //                 }
  //             } else {
  //                 top.dhtmlx.alert({
  //                     text: res.messages,
  //                     title: '提示信息',
  //                     ok: '确定'
  //                 });
  //             }
  //         }
  //     });
  // }
  // function ajax_list_05_01() {
  //     var year = $('#year_05_01').val();
  //     var $title=$('#title_05_01');
  //     var $list=$('#list_05_01');
  //     _ajax({
  //         url: GLOBAL_AJAX_URL.hospitalizeddepartmentFeesTop10,
  //         data:JSON.stringify({
  //             year: year
  //         }),
  //         success: function (res) {
  //             if (res.status) {
  //                 var items=res.data.mantimes;
  //                 $title.text(year+'年住院各科室人次Top10');
  //                 $list.find('dd').remove();
  //                 for(var i=0,len=items.length;i<len;i++){
  //                     $list.append('<dd class="clearfix"> <span class="pull-left">'+items[i][0]+'</span> <span class="pull-right cost">'+items[i][1]+'</span></dd>');
  //                 }
  //             } else {
  //                 top.dhtmlx.alert({
  //                     text: res.messages,
  //                     title: '提示信息',
  //                     ok: '确定'
  //                 });
  //             }
  //         }
  //     });
  // }
  // function ajax_list_06_01() {
  //     var year = $('#year_06_01').val();
  //     var $title=$('#title_06_01');
  //     var $list=$('#list_06_01');
  //     _ajax({
  //         url: GLOBAL_AJAX_URL.hospitalizeddepartmentFeesTop10,
  //         data:JSON.stringify({
  //             year: year
  //         }),
  //         success: function (res) {
  //             if (res.status) {
  //                 var items=res.data.sumcost;
  //                 $title.text(year+'年住院各科室总费用Top10');
  //                 $list.find('dd').remove();
  //                 for(var i=0,len=items.length;i<len;i++){
  //                     $list.append('<dd class="clearfix"> <span class="pull-left">'+items[i][0]+'</span> <span class="pull-right cost">'+parseFloat(items[i][1]).toFixed(2)+'</span></dd>');
  //                 }
  //             } else {
  //                 top.dhtmlx.alert({
  //                     text: res.messages,
  //                     title: '提示信息',
  //                     ok: '确定'
  //                 });
  //             }
  //         }
  //     });
  // }
  function ajax_table_07_01() {
    var year = $('#year_07_01').val();
    var department = $.trim($('#department_07_01').val());
    var inhospitalnum = $.trim($('#inhospitalnum_07_01').val());
    var name = $.trim($('#name_07_01').val());
    var $tbody = $('#tbody_07_01');
    _ajax({
      url: GLOBAL_AJAX_URL.hospitalDetails,
      data: JSON.stringify({
        year: year,
        department: department,
        inhospitalnum: inhospitalnum,
        name: name
      }),
      success: function (res) {
        if (res.status) {
          var items = res.data;
          $tbody.find('tr').remove();
          for (var i = 0, len = items.length; i < len; i++) {
            $tbody.append('<tr><td>' + items[i][0] + '</td><td>' + items[i][1] + '</td><td>' + items[i][2] + '</td><td>' + items[i][3] + '</td><td>' + items[i][4] + '</td><td>' + items[i][5] + '</td><td>' + items[i][6] + '</td><td>' + items[i][7] + '</td><td>' + items[i][8] + '</td></tr>');
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

  // function ajax_list_08_01() {
  //     var year = $('#year_08_01').val();
  //     var $title=$('#title_08_01');
  //     var $list=$('#list_08_01');
  //     _ajax({
  //         url: GLOBAL_AJAX_URL.hospitalizeddepartmentItemFeesTop10,
  //         data:JSON.stringify({
  //             year: year
  //         }),
  //         success: function (res) {
  //             if (res.status) {
  //                 var items=res.data;
  //                 $title.text(year+'年科室住院均次费用项目贡献度排行Top10');
  //                 $list.find('dd').remove();
  //                 for(var i=0,len=items.length;i<len;i++){
  //                     $list.append('<dd class="clearfix"> <span class="pull-left">'+items[i][0]+'</span> <span class="pull-right cost">'+parseFloat(items[i][1]).toFixed(2)+'</span></dd>');
  //                 }
  //             } else {
  //                 top.dhtmlx.alert({
  //                     text: res.messages,
  //                     title: '提示信息',
  //                     ok: '确定'
  //                 });
  //             }
  //         }
  //     });
  // }
})