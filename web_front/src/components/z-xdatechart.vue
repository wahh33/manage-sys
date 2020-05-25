<template>
  <div>
    <div class="date-filter">
      <DatePicker :clearable="false" split-panels :value="dateRange_" @on-change="setDateRange" type="daterange" size="small"
        placement="bottom-end" placeholder="请选择日期..." style="width: 200px"></DatePicker>
      <div class="date-button">
        <ButtonGroup size="small">
          <Button size="small" :type="buttonType[0]?'info':'default'" @click="chooseFormatType(0)">日</Button>
          <Button size="small" :type="buttonType[1]?'info':'default'" @click="chooseFormatType(1)">月</Button>
          <Button size="small" :type="buttonType[2]?'info':'default'" @click="chooseFormatType(2)">年</Button>
        </ButtonGroup>
      </div>
    </div>
    <div :id="chartId" :style="{width:width,height:height}"></div>
  </div>
</template>

<script>
  let echarts = require('echarts/lib/echarts');
  require('echarts/lib/chart/bar');
  require('echarts/lib/chart/line');
  require('echarts/lib/component/tooltip');
  require('echarts/lib/component/title');
  require('echarts/lib/component/legend');
  export default {
    name: "zXdatechart",
    props: {
      chartType: {
        type: String,
        default: 'line'
      },
      dateFormatType: {
        type: Number,
        default: 0
      },
      dateRange: {
        type: Array,
        default: () => {
          let newDate = new Date();
          newDate = newDate.getFullYear() + '-' + (newDate.getMonth() + 1) + '-';
          return [newDate + '1', newDate + '30'];
        }
      },
      filter: {
        type: Object,
        default: () => {
          return {};
        }
      },
      chartTitle: {
        type: String
      },
      timeKey: {
        type: String
      },
      countKey: {
        type: Array
      },
      seriesName: {
        type: Array
      },
      url: {
        type: String
      },
      isInit: {
        type: Boolean,
        default: true
      },
      width: {
        type: String,
        default: '100%'
      },
      height: {
        type: String,
        default: '400px'
      },
    },
    data() {
      return {
        countChart: null,
        countOption: {},
        dateRange_: [],
        dateFormatType_: -1,
        hadInit: false,
        chartId: 'total-count-' + new Date().getTime()
      }
    },
    computed: {
      buttonType() {
        let self = this,
          buttonTypeArr = [
            [true, false, false],
            [false, true, false],
            [false, false, true]
          ];
        return buttonTypeArr[self.dateFormatType_];
      },
    },
    watch: {
      filter() {
        let self = this;
        self.dateFormatType_ = 0;
        self.dateRange_ = self.dateRange;
        self.countOption.title.text = self.chartTitle;
        self.$nextTick(() => {
          self.getTotalCountData();
        });
      },
    },
    created() {
      let self = this,
          series=[];
      self.dateRange_ = self.dateRange;
      self.dateFormatType_ = self.dateFormatType;
      self.countKey.forEach((value,index)=>{
        series.push({
          name: self.seriesName[index]||'无名',
          type: self.chartType,
          encode: {
            x: self.timeKey,
            y: value
          },
          areaStyle: {}
        });
      });
      self.countOption = {
        title: {
          text: self.chartTitle
        },
        legend: {},
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        dataset: {
          source: []
        },
        xAxis: {
          type: 'category'
        },
        yAxis: {},
        series: series
      };
    },
    mounted() {
      let self = this;
      self.$nextTick(() => {
        self.hadInit = true;
        self.countChart = echarts.init(document.getElementById(self.chartId), 'light');
      });
    },
    activated(){
      let self=this;
      self.$nextTick(() => {
        self.isInit && self.getTotalCountData();
      });
    },
    methods: {
      setDateRange(value) {
        let self = this;
        self.dateRange_ = value;
        self.getTotalCountData();
      },
      getTotalCountData() {
        let self = this;
        if (!self.hadInit) return;
        self.countChart.showLoading();
        self.$http({
          method: 'post',
          url: self.url,
          data: {
            dateRange: self.dateRange_,
            dateFormatType: self.dateFormatType_,
            ...self.filter
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.countOption.dataset.source = self.getDateRangeFullObject(self.dateRange_[0], self.dateRange_[1],
              res.data.data, self.dateFormatType_, self.timeKey, ...self.countKey);
            self.countChart.setOption(self.countOption);
            self.countChart.hideLoading();
          } else {
            self.$Message.error(res.data.message);
          }
        }).catch(err => {
          self.$Message.error(err);
          console.log(err);
        });
      },
      getDateRangeFullObject(startTime_, endTime_, objs, type, timeKey) {
        let startTime = new Date(startTime_),
          endTime = new Date(endTime_),
          args = [].slice.call(arguments, 5),
          result = [],
          dayTime = 24 * 60 * 60 * 1000;
        startTime = type === 2 ? new Date(startTime.getFullYear().toString()) : type === 1 ? new Date(startTime.getFullYear(),
          startTime.getMonth()) : startTime;
        endTime = type === 2 ? new Date(endTime.getFullYear().toString()) : type === 1 ? new Date(endTime.getFullYear(),
          endTime.getMonth()) : endTime;
        while ((startTime.getTime() - endTime.getTime()) <= 0) {
          let obj = {},
            year = startTime.getFullYear().toString(),
            month = (startTime.getMonth() + 1).toString(),
            day = startTime.getDate().toString();
          obj[timeKey] = type === 2 ? year : type === 1 ? (year + '-' + (month.length === 1 ? ('0' + month) : month)) :
            (year + '-' + (month.length === 1 ? ('0' + month) : month) + '-' + (day.length === 1 ? ('0' + day) : day));
          args.forEach(arg => {
            obj[arg] = 0;
          });
          result.push(obj);
          switch (type) {
            case 2:
              startTime.setFullYear(startTime.getFullYear() + 1);
              break;
            case 1:
              startTime.setMonth(startTime.getMonth() + 1);
              break;
            case 0:
            default:
              startTime.setDate(startTime.getDate() + 1);
              break;
          }
        }
        startTime = new Date(startTime_);
        objs.forEach(o => {
          let time = new Date(o[timeKey]),
            index = type === 2 ? (time.getFullYear() - startTime.getFullYear()) : type === 1 ?
            ((time.getFullYear() - startTime.getFullYear()) * 12 + (time.getMonth() - startTime.getMonth())) :
            (Math.floor((time.getTime() - startTime.getTime()) / dayTime));
          args.forEach(arg => {
            result[index][arg] = o[arg];
          });
        });
        return result;
      },
      chooseFormatType(type) {
        let self = this;
        self.dateFormatType_ = type;
        self.getTotalCountData();
      },
    }
  }
</script>

<style scoped>
  .date-filter,
  .date-button {
    position: absolute;
    right: 20px;
    z-index: 1;
  }

  .date-button {
    margin-top: 5px;
    right: 0;
  }
</style>
