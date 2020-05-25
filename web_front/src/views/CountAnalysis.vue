<template>
  <div>
    <section>
      <Card dis-hover :bordered="false">
        <div class="length-filter">
          <Dropdown @on-click="changeBuyGoodsLength">
            <a href="javascript:void(0)">
              {{buyGoodsLengthText}}
              <Icon type="ios-arrow-down"></Icon>
            </a>
            <DropdownMenu slot="list">
              <DropdownItem v-for="(item,index) in buyGoodsLengthArr" :key="index" :name="index">{{item.text}}</DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </div>
        <div id="buy-goods-top10"></div>
      </Card>
    </section>
    <section style="margin-top: 20px;">
      <Card dis-hover :bordered="false">
        <div class="length-filter">
          <Dropdown @on-click="changeSellGoodsLength">
            <a href="javascript:void(0)">
              {{sellGoodsLengthText}}
              <Icon type="ios-arrow-down"></Icon>
            </a>
            <DropdownMenu slot="list">
              <DropdownItem v-for="(item,index) in sellGoodsLengthArr" :key="index" :name="index">{{item.text}}</DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </div>
        <div id="sell-goods-top10"></div>
      </Card>
    </section>
  </div>
</template>

<script>
  let echarts = require('echarts/lib/echarts');
  require('echarts/lib/chart/bar');
  require('echarts/lib/component/tooltip');
  require('echarts/lib/component/title');
  require('echarts/lib/component/legend');
  export default {
    data() {
      return {
        buyChart: null,
        sellChart: null,
        buyGoodsLengthArr: [{
            text: 'Top5',
            length: 5,
          },
          {
            text: 'Top10',
            length: 10,
          },
          {
            text: 'Top15',
            length: 15,
          },
          {
            text: 'Top20',
            length: 20,
          }
        ],
        sellGoodsLengthArr: [{
            text: 'Top5',
            length: 5,
          },
          {
            text: 'Top10',
            length: 10,
          },
          {
            text: 'Top15',
            length: 15,
          },
          {
            text: 'Top20',
            length: 20,
          }
        ],
        buyGoodsLengthText: '',
        sellGoodsLengthText: '',
        buySellTopOption: {
          title: {
            text: ''
          },
          legend: {},
          tooltip: {
            trigger: 'item',
            formatter(params) {
              return params.data.goodsName + '</br>' + params.seriesName + '：' + params.data.finishCount
            }
          },
          dataset: {
            source: []
          },
          yAxis: {
            type: 'category'
          },
          xAxis: {},
          series: [{
            name: '数量',
            type: 'bar',
            encode: {
              y: 'goodsName',
              x: 'finishCount'
            },
          }]
        },
      }
    },
    mounted(){
      let self=this;
      self.buyGoodsLengthText = self.buyGoodsLengthArr[0].text;
      self.sellGoodsLengthText = self.sellGoodsLengthArr[0].text;
      self.$nextTick(() => {
        self.buyChart = echarts.init(document.getElementById('buy-goods-top10'), 'light');
        self.sellChart = echarts.init(document.getElementById('sell-goods-top10'), 'light');
      });
    },
    activated(){
      let self = this;
      self.$nextTick(() => {
        self.getBuySellTop10Data('buy', self.buyGoodsLengthArr[0].length);
        self.getBuySellTop10Data('sell', self.sellGoodsLengthArr[0].length);
      });
    },
    methods: {
      getBuySellTop10Data(type = 'buy', length = 5) {
        let self = this;
        if (type === 'buy') {
          self.buyChart.showLoading();
        } else self.sellChart.showLoading();
        self.$http({
          method: 'post',
          url: '/api/orderDetail/getOrderDetailSumPage',
          data: {
            filter: type === 'buy' ? 0 : 1,
            start: 1,
            length: length,
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.buySellTopOption.dataset.source = res.data.data.data.filter(o=>o.finishCount&&o.finishCount!=0).reverse();
            if (type === 'buy') {
              self.buySellTopOption.title.text = '采购交易量Top';
              self.buyChart.setOption(self.buySellTopOption);
              self.buyChart.hideLoading();
            } else {
              self.buySellTopOption.title.text = '销售交易量Top';
              self.sellChart.setOption(self.buySellTopOption);
              self.sellChart.hideLoading();
            }
          } else {
            self.$Message.error(res.data.message);
          }
        }).catch(err => {
          self.$Message.error(err.message);
        });
      },
      changeBuyGoodsLength(index) {
        let self = this;
        self.buyGoodsLengthText = self.buyGoodsLengthArr[index].text;
        self.getBuySellTop10Data('buy', self.buyGoodsLengthArr[index].length);
      },
      changeSellGoodsLength(index) {
        let self = this;
        self.sellGoodsLengthText = self.sellGoodsLengthArr[index].text;
        self.getBuySellTop10Data('sell', self.sellGoodsLengthArr[index].length);
      }
    }
  }
</script>

<style>
  .length-filter {
    position: absolute;
    right: 20px;
    z-index: 1;
  }

  #buy-goods-top10,
  #sell-goods-top10 {
    height: 400px;
  }
</style>
