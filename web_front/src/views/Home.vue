<template>
  <div>
    <section id="work-info">
      <p style="font-size: 16px;font-weight: 500;margin-bottom: 10px;">工作台</p>
      <div style="overflow: auto;">
        <span id="work-hello">贵安，{{name}}，开始您一天的工作吧！</span>
        <div id="work-count">
          <div v-for="(value,key) in workCount[workData.role]" :key="key">
            <p>
              <Icon class="apps-icon" type="md-apps" size="20"></Icon>{{value}}
            </p>
            <p>
              <Tooltip placement="top" :content="(key==='needCount'||key==='storeApplyCount'?'待确认:':'待审批:')+workData[key][0]+'/进行中:'+workData[key][1]"
                style="font-weight: bold;font-size: 20px;">
                {{workData[key][0]+'/'+workData[key][1]}}
              </Tooltip>
            </p>
          </div>
        </div>
      </div>
    </section>
    <section v-if="['buyer','seller','producer'].some(o=>o===workData['role'])" style="margin-bottom: 0;">
      <Row :gutter="20">
        <Col span="16">
        <Card dis-hover :bordered="false" style="margin-bottom: 20px;" v-for="(item,key) in workList[workData.role]"
          :key="key">
          <p slot="title">
            <Icon class="apps-icon" type="md-apps" size="20"></Icon>
            &nbsp;{{item.text}}
          </p>
          <router-link slot="extra" :to="item.allRoute">
            查看全部
          </router-link>
          <Row :gutter="10">
            <Col span="8" v-if="workData[key].length===0">
            <Card :bordered="false" class="work-list work-list-none">
              <span>无</span>
            </Card>
            </Col>
            <Col span="8" v-for="(i,k) in workData[key]" :key="k">
            <Card :bordered="false" class="work-list" @click.native="pushRoute(item.itemRoute+i.id)">
              <Row>
                <Col span="12">
                <p>序号：{{i.id}}</p>
                <p>日期：{{i.createTime||i.startTime}}</p>
                </Col>
                <Col span="12" style="height: 54px; display: flex; justify-content: center; align-items: center;">
                <span :style="{fontSize:'25px',color:key==='orderList'||key==='returnList'?orderStateColor[i.state]:needPlanStateColor[i.state]}">{{key==='orderList'||key==='returnList'?orderState[i.state]:needPlanState[i.state]}}</span>
                </Col>
              </Row>
            </Card>
            </Col>
          </Row>
        </Card>
        </Col>
        <Col span="8">
        <Card dis-hover :bordered="false" style="margin-bottom: 20px;">
          <p slot="title">
            <Icon class="apps-icon" type="logo-usd" size="20"></Icon>
            &nbsp;我的成就
          </p>
          <Tooltip placement="top" slot="extra" content="已完成数">
            <Icon class="apps-icon" type="ios-help-circle-outline" size="16"></Icon>
          </Tooltip>
          <Row class="work-count-finish" v-if="workData['role']==='producer'">
            <Col span="12">
            <p>生产需求</p>
            <p>{{workData.needCount[2]}}</p>
            </Col>
            <Col span="12">
            <p>计划</p>
            <p>{{workData.planCount[2]}}</p>
            </Col>
          </Row>
          <Row class="work-count-finish" v-else>
            <Col span="8">
            <p>计划</p>
            <p>{{workData.planCount[2]}}</p>
            </Col>
            <Col span="8">
            <p>订单</p>
            <p>{{workData.orderCount[2]}}</p>
            </Col>
            <Col span="8">
            <p>退单</p>
            <p>{{workData.returnCount[2]}}</p>
            </Col>
          </Row>
        </Card>
        <Card dis-hover :bordered="false">
          <p slot="title">
            <Icon class="apps-icon" type="md-heart" size="20"></Icon>
            &nbsp;快捷方式
          </p>
          <Row>
            <Col class="work-quick" span="8" v-for="(item,key) in workQuick[workData.role]" :key="key">
            <div @click="pushRoute(item.url)">
              <p>
                <Icon class="apps-icon" :type="item.icon" size="25"></Icon>
              </p>
              <p>{{item.text}}</p>
            </div>
            </Col>
          </Row>
        </Card>
        </Col>
      </Row>
    </section>
    <section v-if="['admin','manager','storeman'].some(o=>o===workData['role'])">
      <Row :gutter="20">
        <Col span="8" v-for="(item,key) in workQuickList[workData.role]" :key="key">
        <Card :bordered="false" class="work-quick-list" @click.native="pushRoute(item.url)">
          <span>{{item.text}}</span>
        </Card>
        </Col>
      </Row>
    </section>
    <section v-if="['producer','seller'].some(o=>o===workData['role'])">
      <Card dis-hover :bordered="false">
        <p slot="title">
          <Icon class="apps-icon" type="md-grid" size="20"></Icon>
          &nbsp;{{'我的'+(workData['role']==='producer'?'采购':'生产')+'需求'}}
        </p>
        <div slot="extra" style="margin-top: -4px;">
          <Input search placeholder="序号..." v-model="need.model.filter.id" @on-search="getNeedPage" />
        </div>
        <Table border stripe :columns="need.columns" :data="need.result.data" :loading="need.tableLoading">
          <div slot="footer">
            <Page :total="need.result.total" transfer show-total show-sizer show-elevator :page-size-opts="need.pageSizeOpts"
              :page-size="need.model.length" style="float:right;margin-right:20px" @on-change="needSelectPage"
              @on-page-size-change="needChangeLength" />
          </div>
        </Table>
      </Card>
    </section>
    <section v-if="['seller','manager','admin'].some(o=>o===workData['role'])">
      <Card dis-hover :bordered="false">
        <p slot="title">
          <Icon class="apps-icon" type="md-grid" size="20"></Icon>
          &nbsp;客户交易情况
        </p>
        <div slot="extra" style="margin-top: -4px;">
          <Input search placeholder="序号..." v-model="customer.model.filter.clientId" @on-search="getCustomerPage" />
        </div>
        <Table border stripe :columns="customer.columns" :data="customer.result.data" :loading="customer.tableLoading"
          @on-sort-change="sortCustomerPage">
          <div slot="footer">
            <Page :total="customer.result.total" transfer show-total show-sizer show-elevator :page-size-opts="customer.pageSizeOpts"
              :page-size="customer.model.length" style="float:right;margin-right:20px" @on-change="customerSelectPage"
              @on-page-size-change="customerChangeLength" />
          </div>
        </Table>
      </Card>
    </section>
    <section v-if="['buyer','manager','admin'].some(o=>o===workData['role'])">
      <Card dis-hover :bordered="false">
        <p slot="title">
          <Icon class="apps-icon" type="md-grid" size="20"></Icon>
          &nbsp;供应商交易情况
        </p>
        <div slot="extra" style="margin-top: -4px;">
          <Input search placeholder="序号..." v-model="supplier.model.filter.clientId" @on-search="getSupplierPage" />
        </div>
        <Table border stripe :columns="supplier.columns" :data="supplier.result.data" :loading="supplier.tableLoading"
          @on-sort-change="sortSupplierPage">
          <div slot="footer">
            <Page :total="supplier.result.total" transfer show-total show-sizer show-elevator :page-size-opts="supplier.pageSizeOpts"
              :page-size="supplier.model.length" style="float:right;margin-right:20px" @on-change="supplierSelectPage"
              @on-page-size-change="supplierChangeLength" />
          </div>
        </Table>
      </Card>
    </section>
    <Modal v-model="chartModal" :footer-hide="true" width="800">
      <z-xdatechart :chartTitle="chartTitle" timeKey="createTime" :countKey="['totalPrice']" :seriesName="['金额']"
        :filter="chartFilter" :isInit="true" width="750px" style="padding: 20px;" url="/api/payRecord/getPayRecordSum"></z-xdatechart>
    </Modal>
  </div>
</template>

<script>
  import {
    mapGetters
  } from 'vuex';
  import {
    typeText
  } from '@/utils/typeText.js';
  import zXdatechart from '@/components/z-xdatechart.vue';
  export default {
    components: {
      zXdatechart
    },
    data() {
      return {
        //chart
        chartModal: false,
        chartTitle: '交易额统计',
        chartFilter: {},
        //////////////////////////////////////////////////
        orderState: typeText.orderState,
        orderStateColor: typeText.orderStateColor,
        needPlanState: typeText.needPlanState,
        needPlanStateColor: typeText.needPlanStateColor,
        level: typeText.level,
        levelColor: typeText.levelColor,
        need: {
          result: {
            data: [],
            total: 0,
          },
          columns: [{
              title: '序号',
              key: 'id',
              align: 'center',
            },
            {
              title: '确认人',
              key: 'surerName',
              align: 'center',
              render: (h, params) => h('span', params.row.surerName || '无')
            },
            {
              title: '紧急度',
              key: 'level',
              align: 'center',
              render: (h, params) => h('Tag', {
                props: {
                  color: this.levelColor[params.row.level]
                }
              }, this.level[params.row.level])
            },
            {
              title: '开始',
              key: 'startTime',
              align: 'center',
            },
            {
              title: '结束',
              key: 'endTime',
              align: 'center',
            },
            {
              title: '描述',
              key: 'description',
              align: 'center',
              render: (h, params) => {
                if (params.row.description) {
                  return h('Tooltip', {
                      props: {
                        placement: 'bottom',
                        transfer: true
                      },
                      style: {
                        width: '100%'
                      }
                    },
                    [
                      h('div', {
                        style: {
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                        }
                      }, params.row.description),
                      h('div', {
                        slot: 'content',
                        style: {
                          whiteSpace: 'normal',
                          wordWrap: 'break-word',
                        }
                      }, params.row.description)
                    ]);
                } else {
                  return h('span', '无');
                }
              }
            },
            {
              title: '状态',
              key: 'state',
              align: 'center',
              render: (h, params) => h('Tag', {
                props: {
                  color: this.needPlanStateColor[params.row.state]
                }
              }, this.needPlanState[params.row.state])
            },
          ],
          pageSizeOpts: [5, 10, 15, 20],
          tableLoading: true,
          model: {
            length: 5,
            start: 1,
            filter: {
              id: null,
              creatorId: null,
              needPlanType: 0,
              buySellType: null,
            },
            sortColumn: 'state',
            sortType: 'asc',
          },
        },
        customer: {
          result: {
            data: [],
            total: 0,
          },
          columns: [{
              title: '序号',
              key: 'clientId',
              align: 'center',
              sortable: true,
            },
            {
              title: '名称',
              key: 'clientName',
              align: 'center',
              sortable: true,
            },
            {
              title: '订单量',
              key: 'orderCount',
              align: 'center',
              sortable: true,
              render: (h, params) => h('span', +params.row.orderCount),
            },
            {
              title: '退单量',
              key: 'returnCount',
              align: 'center',
              sortable: true,
              render: (h, params) => h('span', +params.row.returnCount),
            },
            {
              title: '退单率',
              align: 'center',
              render: (h, params) => {
                let orderCount = +params.row.orderCount,
                  returnCount = +params.row.returnCount,
                  result = +(returnCount / orderCount).toFixed(2) * 100;
                return h('span', (result > 100 ? 100 : result) + '%');
              },
            },
            {
              title: '成交金额',
              key: 'priceCount',
              align: 'center',
              sortable: true,
              sortType: 'desc',
            },
            {
              title: '折扣',
              key: 'discount',
              align: 'center',
              sortable: true,
              render: (h, params) => {
                let self = this;
                return h('InputNumber', {
                  props: {
                    value: self.customer.result.data[params.index].discount,
                    min: 1,
                    max: 10,
                    step: 0.5,
                    precision: 1,
                  },
                  on: {
                    input: function(value) {
                      self.customer.result.data[params.index].discount = value;
                    }
                  }
                });
              },
            },
            {
              title: '操作',
              align: 'center',
              render: (h, params) => {
                let self = this;
                return h('div', [
                  h('Button', {
                    props: {
                      type: 'primary',
                      size: 'small',
                    },
                    style: {
                      marginRight: '5px'
                    },
                    on: {
                      click: () => {
                        this.saveCustomerDiscount(params.index);
                      }
                    }
                  }, '保存'),
                  h('Button', {
                    props: {
                      type: 'success',
                      size: 'small',
                    },
                    on: {
                      click: () => {
                        this.showChart(params.row.clientId, params.row.clientName);
                      }
                    }
                  }, '统计')
                ]);
              },
            },
          ],
          pageSizeOpts: [5, 10, 15, 20],
          tableLoading: true,
          model: {
            length: 5,
            start: 1,
            filter: {
              clientType: 1,
              clientId: null,
            },
            sortColumn: 'priceCount',
            sortType: 'desc',
          },
        },
        supplier: {
          result: {
            data: [],
            total: 0,
          },
          columns: [{
              title: '序号',
              key: 'clientId',
              align: 'center',
              sortable: true,
            },
            {
              title: '名称',
              key: 'clientName',
              align: 'center',
              sortable: true,
            },
            {
              title: '订单量',
              key: 'orderCount',
              align: 'center',
              sortable: true,
              render: (h, params) => h('span', +params.row.orderCount),
            },
            {
              title: '退单量',
              key: 'returnCount',
              align: 'center',
              sortable: true,
              render: (h, params) => h('span', +params.row.returnCount),
            },
            {
              title: '退单率',
              align: 'center',
              render: (h, params) => {
                let orderCount = +params.row.orderCount,
                  returnCount = +params.row.returnCount;
                return h('span', +(returnCount / (returnCount + orderCount)).toFixed(2) * 100 + '%');
              },
            },
            {
              title: '成交金额',
              key: 'priceCount',
              align: 'center',
              sortable: true,
              sortType: 'desc',
            },
            {
              title: '操作',
              align: 'center',
              render: (h, params) => h('Button', {
                props: {
                  type: 'success',
                  size: 'small',
                },
                on: {
                  click: () => {
                    this.showChart(params.row.clientId, params.row.clientName);
                  }
                }
              }, '统计'),
            },
          ],
          pageSizeOpts: [5, 10, 15, 20],
          tableLoading: true,
          model: {
            length: 5,
            start: 1,
            filter: {
              clientType: 0,
              clientId: null,
            },
            sortColumn: 'priceCount',
            sortType: 'desc',
          },
        },
        workCount: {
          buyer: {
            needCount: '采购需求',
            planCount: '我的计划',
            orderCount: '我的订单',
            returnCount: '我的退单',
          },
          producer: {
            needCount: '生产需求',
            planCount: '我的计划',
          },
          seller: {
            planCount: '我的计划',
            orderCount: '我的订单',
            returnCount: '我的退单',
          },
          storeman: {
            storeApplyCount: '存取申请',
          },
          manager: {
            planCount: '计划',
            orderCount: '订单',
            returnCount: '退单',
          },
          admin: {
            planCount: '计划',
            orderCount: '订单',
            returnCount: '退单',
          },
        },
        workList: {
          buyer: {
            planList: {
              text: '我的计划',
              allRoute: '/plan',
              itemRoute: '/planDetail?id=',
            },
            orderList: {
              text: '我的订单',
              allRoute: '/order',
              itemRoute: '/orderDetail?id=',
            },
            returnList: {
              text: '我的退单',
              allRoute: '/return',
              itemRoute: '/returnDetail?id=',
            },
          },
          producer: {
            productNeedList: {
              text: '生产需求',
              allRoute: '/productNeed',
              itemRoute: '/needDetail?id=',
            },
            planList: {
              text: '我的计划',
              allRoute: '/plan',
              itemRoute: '/planDetail?id=',
            },
          },
          seller: {
            planList: {
              text: '我的计划',
              allRoute: '/plan',
              itemRoute: '/planDetail?id=',
            },
            orderList: {
              text: '我的订单',
              allRoute: '/order',
              itemRoute: '/orderDetail?id=',
            },
            returnList: {
              text: '我的退单',
              allRoute: '/return',
              itemRoute: '/returnDetail?id=',
            },
          },
        },
        workData: {},
        workQuick: {
          buyer: [{
              icon: 'md-megaphone',
              text: '采购需求',
              url: '/matterNeed',
            },
            {
              icon: 'ios-paper',
              text: '计划',
              url: '/plan',
            },
            {
              icon: 'ios-print',
              text: '订单',
              url: '/order',
            },
            {
              icon: 'md-print',
              text: '退单',
              url: '/return',
            },
            {
              icon: 'ios-contacts',
              text: '供应商',
              url: '/supplier',
            },
            {
              icon: 'md-settings',
              text: '货物库存',
              url: '/goodsSelect',
            },
          ],
          producer: [{
              icon: 'md-megaphone',
              text: '采购需求',
              url: '/matterNeed',
            },
            {
              icon: 'ios-megaphone',
              text: '生产需求',
              url: '/productNeed',
            },
            {
              icon: 'ios-paper',
              text: '计划',
              url: '/plan',
            },
            {
              icon: 'ios-albums',
              text: '货物信息',
              url: '/goods',
            },
            {
              icon: 'md-settings',
              text: '货物库存',
              url: '/goodsSelect',
            },
          ],
          seller: [{
              icon: 'ios-megaphone',
              text: '生产需求',
              url: '/productNeed',
            },
            {
              icon: 'ios-paper',
              text: '计划',
              url: '/plan',
            },
            {
              icon: 'ios-print',
              text: '订单',
              url: '/order',
            },
            {
              icon: 'md-print',
              text: '退单',
              url: '/return',
            },
            {
              icon: 'ios-contacts',
              text: '客户',
              url: '/customer',
            },
            {
              icon: 'md-settings',
              text: '货物库存',
              url: '/goodsSelect',
            },
          ],
        },
        workQuickList: {
          admin: [{
              text: '计划',
              url: '/plan',
            },
            {
              text: '订单',
              url: '/order',
            },
            {
              text: '退单',
              url: '/return',
            },
          ],
          manager: [{
              text: '计划',
              url: '/plan',
            },
            {
              text: '订单',
              url: '/order',
            },
            {
              text: '退单',
              url: '/return',
            },
          ],
          storeman: [{
              text: '货物查询',
              url: '/goodsSelect',
            },
            {
              text: '存取审批',
              url: '/inOutCheck',
            },
            {
              text: '员工信息',
              url: '/staff',
            },
          ],
        },
      }
    },
    computed: {
      ...mapGetters(['id', 'role', 'name'])
    },
    activated() {
      let self = this;
      self.getWorkInfo();
    },
    methods: {
      getWorkInfo() {
        let self = this;
        self.$http({
          method: 'get',
          url: 'api/user/getWorkInfo'
        }).then(res => {
          if (res.data.code === 200) {
            self.workData = res.data.data;
            self.getNeedPage();
            self.getSupplierPage();
            self.getCustomerPage();
          } else {
            self.$Modal.error({
              title: '获取信息失败！',
              content: res.data.message
            });
          }
        }).catch(err => {
          self.$Modal.error({
            title: '获取信息失败！',
            content: err.message
          });
        })
      },
      pushRoute(url) {
        let self = this;
        self.$router.push(url);
      },
      tableError(content) {
        let self = this;
        self.$Modal.error({
          title: '获取表格数据出错！',
          content: content
        });
      },
      //need
      needSelectPage(index) {
        let self = this;
        self.need.model.start = index;
        self.getNeedPage();
      },
      needChangeLength(length) {
        let self = this;
        self.need.model.length = length;
        self.getNeedPage();
      },
      getNeedPage() {
        let self = this;
        if (!['producer', 'seller'].some(o => o === self.workData['role'])) return;
        if(self.need.model.filter.id&&(self.need.model.filter.id.includes('.')||parseInt(self.need.model.filter.id)!=self.need.model.filter.id)){
          self.$Message.error('请输入整数！');
          return;
        }
        self.need.model.filter.creatorId = self.workData.id;
        self.need.model.filter.buySellType = self.workData.role === 'producer' ? 0 : 1;
        self.need.tableLoading = true;
        self.$http({
          method: 'post',
          url: 'api/needPlan/getNeedPlanPage',
          data: self.need.model
        }).then(res => {
          if (res.data.code === 200) {
            self.need.result = res.data.data;
            self.need.tableLoading = false;
          } else self.tableError(res.data.message);
        }).catch(err => self.tableError(err.message));
      },
      //supplier
      supplierSelectPage(index) {
        let self = this;
        self.supplier.model.start = index;
        self.getSupplierPage();
      },
      supplierChangeLength(length) {
        let self = this;
        self.supplier.model.length = length;
        self.getSupplierPage();
      },
      getSupplierPage() {
        let self = this;
        if (!['buyer', 'manager', 'admin'].some(o => o === self.workData['role'])) return;
        if(self.supplier.model.filter.clientId&&(self.supplier.model.filter.clientId.includes('.')||parseInt(self.supplier.model.filter.clientId)!=self.supplier.model.filter.clientId)){
          self.$Message.error('请输入整数！');
          return;
        }
        self.supplier.tableLoading = true;
        self.$http({
          method: 'post',
          url: 'api/client/getClientInfoPage',
          data: self.supplier.model
        }).then(res => {
          if (res.data.code === 200) {
            self.supplier.result = res.data.data;
            self.supplier.tableLoading = false;
          } else self.tableError(res.data.message);
        }).catch(err => self.tableError(err.message));
      },
      //customer
      customerSelectPage(index) {
        let self = this;
        self.customer.model.start = index;
        self.getCustomerPage();
      },
      customerChangeLength(length) {
        let self = this;
        self.customer.model.length = length;
        self.getCustomerPage();
      },
      getCustomerPage() {
        let self = this;
        if (!['seller', 'manager', 'admin'].some(o => o === self.workData['role'])) return;
        if(self.customer.model.filter.clientId&&(self.customer.model.filter.clientId.includes('.')||parseInt(self.customer.model.filter.clientId)!=self.customer.model.filter.clientId)){
          self.$Message.error('请输入整数！');
          return;
        }
        self.customer.tableLoading = true;
        self.$http({
          method: 'post',
          url: 'api/client/getClientInfoPage',
          data: self.customer.model
        }).then(res => {
          if (res.data.code === 200) {
            self.customer.result = res.data.data;
            self.customer.tableLoading = false;
          } else self.tableError(res.data.message);
        }).catch(err => self.tableError(err.message));
      },
      saveCustomerDiscount(index) {
        let self = this,
          cm = self.customer.result.data[index];
        if (cm.discount !== null && cm.discount !== 0) {
          self.$Modal.confirm({
            title: '确认保存？',
            loading: true,
            onOk: () => {
              self.$http({
                method: 'post',
                url: '/api/client/saveClient',
                data: {
                  id: cm.clientId,
                  discount: cm.discount,
                },
              }).then(res => {
                self.$Modal.remove();
                if (res.data.code === 200) {
                  self.$Message.success("保存折扣成功！");
                } else {
                  self.$Modal.error({
                    title: '保存折扣失败！',
                    content: res.data.message
                  });
                }
              }).catch(err => {
                self.$Modal.error({
                  title: '保存折扣失败！',
                  content: err.message
                });
              });
            }
          });
        } else {
          self.$Modal.remove();
          self.$Message.error('折扣不能为空！');
        }
      },
      sortCustomerPage({
        column,
        key,
        order
      }) {
        let self = this;
        self.customer.model.sortColumn = key;
        self.customer.model.sortType = order == 'normal' ? 'asc' : order;
        self.getCustomerPage();
      },
      sortSupplierPage({
        column,
        key,
        order
      }) {
        let self = this;
        self.supplier.model.sortColumn = key;
        self.supplier.model.sortType = order == 'normal' ? 'asc' : order;
        self.getSupplierPage();
      },
      showChart(clientId, clientName) {
        let self = this;
        self.chartTitle = '交易额统计(' + clientName + ')';
        self.chartFilter = {
          clientId: clientId
        };
        self.chartModal = true;
      },
    }
  }
</script>

<style scoped>
  section {
    margin-bottom: 20px;
  }

  #work-info {
    margin-left: -20px;
    margin-right: -20px;
    padding: 20px;
    background-color: white;
  }

  #work-hello {
    padding-left: 50px;
    font-size: 25px;
    font-weight: bold;
  }

  #work-count {
    float: right;
    font-size: medium;
    text-align: end;
    display: flex;
    justify-content: end;
    align-items: center;
    padding-right: 30px;
  }

  #work-count>div {
    padding-left: 15px;
    padding-right: 15px;
  }

  .work-count-finish p {
    font-size: medium;
    text-align: center;
  }

  .work-count-finish p:nth-child(even) {
    font-size: 20px;
    font-weight: bold;
  }

  .work-list {
    background-color: rgb(164, 205, 235);
    height: 100px;
    margin-bottom: 10px;
    color: white;
    font-size: large;
    font-weight: bold;
    cursor: pointer;
  }

  .work-list-none {
    background-color: gainsboro;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: auto;
  }

  .work-quick {
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    font-size: medium;
    margin-bottom: 20px;
  }

  .work-quick>div {
    cursor: pointer;
  }

  .work-quick>div:hover {
    color: #57a3f3;
  }

  .work-quick-list {
    background-color: rgb(164, 205, 235);
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 22px;
    font-weight: bold;
    color: white;
    cursor: pointer;
  }

  .apps-icon {
    border-radius: 20px;
    background-color: rgb(230, 247, 255);
    color: rgb(24, 144, 255);
  }
</style>
