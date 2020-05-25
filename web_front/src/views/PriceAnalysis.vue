<template>
  <div>
    <section>
      <Card dis-hover :bordered="false">
        <z-xdatechart chartTitle="交易额统计" timeKey="createTime" :countKey="['totalPrice']" :seriesName="['金额']" url="/api/payRecord/getPayRecordSum"></z-xdatechart>
      </Card>
    </section>
    <section style="margin-top: 20px;">
      <Row :gutter="20">
        <Col span="12">
        <Card dis-hover :bordered="false">
          <div class="length-filter">
            <Dropdown @on-click="changeSupplierLength">
              <a href="javascript:void(0)">
                {{supplierLengthText}}
                <Icon type="ios-arrow-down"></Icon>
              </a>
              <DropdownMenu slot="list">
                <DropdownItem v-for="(item,index) in supplierLengthArr" :key="index" :name="index">{{item.text}}</DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
          <div id="supplier-top10"></div>
        </Card>
        </Col>
        <Col span="12">
        <Card dis-hover :bordered="false">
          <div class="length-filter">
            <Dropdown @on-click="changeClientLength">
              <a href="javascript:void(0)">
                {{clientLengthText}}
                <Icon type="ios-arrow-down"></Icon>
              </a>
              <DropdownMenu slot="list">
                <DropdownItem v-for="(item,index) in clientLengthArr" :key="index" :name="index">{{item.text}}</DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
          <div id="client-top10"></div>
        </Card>
        </Col>
      </Row>
    </section>
  </div>
</template>

<script>
  let echarts = require('echarts/lib/echarts');
  require('echarts/lib/chart/bar');
  require('echarts/lib/chart/line');
  require('echarts/lib/component/tooltip');
  require('echarts/lib/component/title');
  require('echarts/lib/component/legend');
  import zXdatechart from '@/components/z-xdatechart.vue';
  export default {
    components: {
      zXdatechart
    },
    data() {
      return {
        clientChart: null,
        supplierChart: null,
        clientLengthArr: [{
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
        supplierLengthArr: [{
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
        clientLengthText: '',
        supplierLengthText: '',
        clientTopOption: {
          title: {
            text: ''
          },
          legend: {},
          tooltip: {
            trigger: 'item',
            formatter(params) {
              return params.data.clientName + '</br>' + params.seriesName + '：' + params.data.priceCount + '￥'
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
            name: '金额',
            type: 'bar',
            encode: {
              y: 'clientName',
              x: 'priceCount'
            }
          }]
        },
      }
    },
    mounted(){
      let self=this;
      self.supplierLengthText = self.supplierLengthArr[0].text;
      self.clientLengthText = self.clientLengthArr[0].text;
      self.$nextTick(()=>{
        self.supplierChart = echarts.init(document.getElementById('supplier-top10'), 'light');
        self.clientChart = echarts.init(document.getElementById('client-top10'), 'light');
      });
    },
    activated(){
      let self = this;
      self.$nextTick(()=>{
        self.getClientTop10Data('supplier', self.clientLengthArr[0].length);
        self.getClientTop10Data('client', self.clientLengthArr[0].length);
      });
    },
    methods: {
      getClientTop10Data(type = 'client', length = 5) {
        let self = this;
        if (type === 'client') {
          self.clientChart.showLoading();
        } else self.supplierChart.showLoading();
        self.$http({
          method: 'post',
          url: '/api/client/getClientInfoPage',
          data: {
            filter: {
              clientType: type === 'client' ? 1 : 0,
            },
            start: 1,
            length: length,
            sortColumn: 'priceCount',
            sortType: 'desc',
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.clientTopOption.dataset.source = res.data.data.data.filter(o=>o.priceCount&&o.priceCount!=0).reverse();
            if (type === 'client') {
              self.clientTopOption.title.text = '交易额Top客户';
              self.clientChart.setOption(self.clientTopOption);
              self.clientChart.hideLoading();
            } else {
              self.clientTopOption.title.text = '交易额Top供应商';
              self.supplierChart.setOption(self.clientTopOption);
              self.supplierChart.hideLoading();
            }
          } else {
            self.$Message.error(res.data.message);
          }
        }).catch(err => {
          self.$Message.error(err);
        });
      },
      changeClientLength(index) {
        let self = this;
        self.clientLengthText = self.clientLengthArr[index].text;
        self.getClientTop10Data('client', self.clientLengthArr[index].length);
      },
      changeSupplierLength(index) {
        let self = this;
        self.supplierLengthText = self.supplierLengthArr[index].text;
        self.getClientTop10Data('supplier', self.supplierLengthArr[index].length);
      },
    }
  }
</script>

<style>
  #date-filter,
  #date-button,
  .length-filter {
    position: absolute;
    right: 20px;
    z-index: 1;
  }

  #date-button {
    margin-top: 5px;
    right: 0;
  }

  #total-price,
  #supplier-top10,
  #client-top10 {
    height: 400px;
  }
</style>
