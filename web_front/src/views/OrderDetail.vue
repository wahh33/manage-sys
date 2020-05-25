<template>
  <div>
    <section v-if="hasData===1">
      <Card class="order-info" :bordered="false" title="基础信息">
        <a slot="extra" @click="fetchData" style="float:right" href="javascript:void(0);">
          <Icon class="refresh" title="刷新" type="md-refresh"></Icon>
        </a>
        <Row>
          <Col span="8">
          <p>类型：{{orderType[order.type+order.isReturn*2]}}</p>
          <p>序号：{{order.id}}</p>
          </Col>
          <Col span="8">
          <p>创建人：{{order.creatorName}}</p>
          <p>创建时间：{{order.createTime}}</p>
          </Col>
          <Col span="8">
          <p>{{clientType[order.type]}}：{{order.clientName}}</p>
          <p v-if="order.type===1">折扣：{{order.discount}} 折</p>
          </Col>
        </Row>
      </Card>
      <Card :bordered="false" title="状态" style="margin-top:20px">
        <Steps :current="order.state===3?1:order.state" :status="order.state===3?'error':'process'">
          <Step title="待审核"></Step>
          <Step :title="order.state===3?'未通过':'进行中'"></Step>
          <Step title="已完成"></Step>
        </Steps>
      </Card>
      <Card :bordered="false" style="margin-top:20px">
        <div slot="title">
          <span style="font-size:16px;color:#17233d">货物详情&nbsp;&nbsp;</span>
          <Button v-if="allowOrderEdit&&!order.isReturn" type="info" @click="addOrderDetail()">
            <Icon type="md-add"></Icon>添加{{order.type===1?'计划':''}}
          </Button>
          <Button v-if="allowOrderEdit&&(order.isReturn||order.type===1)" type="primary" @click="addOrderDetail2()" style="margin-left:5px">
            <Icon type="md-add"></Icon>添加货物
          </Button>
        </div>
        <Table stripe border show-summary :summary-method="setTotalPrice" :columns="orderDetailColumns" :data="orderDetailDatas"
          :loading="odTableLoading">
        </Table>
        <Modal v-model="odModal" title="添加货物" :loading="odModalLoading">
          <Table stripe border @on-selection-change="setNPDetailIds" :columns="npDetailColumns" :data="npDetailDatas"
            :loading="npTableLoading">
          </Table>
          <div slot="footer">
            <Button type="text" @click="odCancel">取消</Button>
            <Button type="info" :loading="odModalLoading" @click="addNPDetailToOrder">添加</Button>
          </div>
        </Modal>
        <Modal v-model="odModal2" title="添加货物" :loading="odModalLoading2">
          <Form ref="goodsForm" :model="goodsData" :label-width="80" :rules="goodsRules">
            <FormItem label="货物" prop="goodsId">
              <Select v-model="goodsData.goodsId" filterable @click.native="getGoodsList">
                <Option v-for="item in goodsList" :value="item.id" :key="item.id">{{ item.name }}</Option>
              </Select>
            </FormItem>
            <FormItem label="数量" prop="count">
              <InputNumber :min="1" :step="1" v-model="goodsData.count" style="width:100%"></InputNumber>
            </FormItem>
          </Form>
          <div slot="footer">
            <Button type="text" @click="odCancel2">取消</Button>
            <Button type="info" :loading="odModalLoading2" @click="addDetailToOrder">添加</Button>
          </div>
        </Modal>
      </Card>
      <Card :bordered="false" style="margin-top:20px" title="收支记录">
        <Table stripe border show-summary :summary-method="setTotalPay" :columns="payRecordColumns" :data="payRecordDatas"
          :loading="prTableLoading">
        </Table>
        <Button class="pr-add" v-if="allowOtherEdit" type="dashed" long @click="addPayRecord()">
          <Icon type="md-add"></Icon>添加
        </Button>
      </Card>
      <Card :bordered="false" style="margin-top:20px">
        <div slot="title">
          <span style="font-size:16px;color:#17233d">库存申请&nbsp;&nbsp;</span>
          <Button v-if="allowAddSA" type="info" @click="addStoreApply()">
            <Icon type="md-add"></Icon>添加
          </Button>
        </div>
        <Table stripe border :columns="storeApplyColumns" :data="storeApplyDatas" :loading="saTableLoading">
        </Table>
        <Modal v-model="saModal" :title="saModalTitle" :loading="saModalLoading" width="750">
          <Table stripe border :columns="saDetailColumns" :data="saDetailDatas" :loading="sadTableLoading">
          </Table>
          <Upload v-if="isSaAdd" :before-upload="setFile" style="display:inline-block;margin-top: 20px;margin-right:5px;"
            action="">
            <Button type="dashed" size="large" icon="ios-cloud-upload-outline">上传凭据</Button>
          </Upload>
          <span v-if="isSaAdd">{{(file&&file.name)||''}}</span>
          <div slot="footer">
            <Button type="text" @click="saCancel">取消</Button>
            <Button v-if="isSaAdd" type="info" :loading="saModalLoading" @click="saveStoreApply()">添加</Button>
          </div>
        </Modal>
      </Card>
      <div class="state-button-group">
        <Button type="info" @click="changeODState(1)" v-if="allowSure">确认</Button>
        <Button type="success" @click="changeODState(0)" v-if="allowReset">恢复</Button>
        <Button type="warning" @click="changeODState(3)" v-if="allowDeny">驳回</Button>
        <Button type="error" @click="deleteOD" v-if="allowDelete">删除</Button>
      </div>
      <Modal v-model="fileModal" :footer-hide="true">
        <img :src="fileSrc" style="width:100%;" />
      </Modal>
    </section>
    <section v-if="hasData===0">
      <div class="no-data">
        <Icon type="ios-close-circle-outline"/>
        <div class="no-data-text">
          数据不存在
        </div>
      </div>
    </section>
    <Spin fix v-if="spinShow">
      <div class="spin-circle spin-icon-load">
        <span class="spin-icon-load-reverse">Loading</span>
      </div>
    </Spin>
  </div>
</template>

<script>
  import {
    typeText
  } from '@/utils/typeText.js';
  import {
    fileMethods,fileDatas
  } from '@/utils/fileHelper.js';
  import {
    mapGetters
  } from 'vuex';
  export default {
    data() {
      return {
        ...fileDatas(),
        spinShow:true,
        //sa
        hasData:-1,
        saId:-1,
        sadTableLoading: false,
        saTableLoading: true,
        saModal: false,
        saModalTitle: '',
        saModalLoading: false,
        isSaAdd: false,
        //pr
        prTableLoading: true,
        //np
        npTableLoading: true,
        //od
        odTableLoading: true,
        odModal: false,
        odModalLoading: false,
        //od2
        odModal2: false,
        odModalLoading2: false,
        //type
        clientType: typeText.clientType,
        orderType: typeText.orderType,
        state: typeText.orderDetailState,
        stateColor: typeText.orderDetailStateColor,
        needPlanType: typeText.needPlanType,
        inOutType: typeText.inOutType,
        saState: typeText.storeApplyState,
        saStateColor: typeText.storeApplyStateColor,
        order: {
          id: null,
          userId: null,
          clientId: null,
          createName: '',
          clientName: '',
          createTime: '',
          type: null,
          state: 0,
          totalPrice: null,
          finishPrice: null,
          discount: null,
          isReturn: null,
          syncTime:null
        },
        orderDetailDatas: [],
        orderDetailColumnsArr: [{
            title: '货物序号',
            key: 'goodsId',
            align: 'center',
          },
          {
            title: '货物名称',
            key: 'goodsName',
            align: 'center',
          },
          {
            title: '数量',
            key: 'count',
            align: 'center',
          },
          {
            title: '已完成数',
            key: 'finishCount',
            align: 'center',
          },
          {
            title: '状态',
            key: 'state',
            align: 'center',
            render: (h, params) => h('Tag', {
              props: {
                color: this.stateColor[params.row.state]
              }
            }, this.state[params.row.state])
          },
          {
            title: '默认单价',
            key: 'defaultPrice',
            align: 'center',
            render: (h, params) => h('span', '￥ ' + +params.row.defaultPrice)
          },
          {
            title: '单价',
            key: 'price',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.allowOrderEdit ? h('InputNumber', {
                props: {
                  value: self.orderDetailDatas[params.index].price,
                  min: 0,
                  step: 0.1
                },
                on: {
                  input: function(value) {
                    self.orderDetailDatas[params.index].price = value;
                  }
                }
              }) : h('span', '￥ ' + +params.row.price);
            }
          },
          {
            title: '总价',
            key: 'totalPrice',
            align: 'center',
            render: (h, params) => h('span', '￥ ' + +params.row.totalPrice)
          },
          {
            title: '操作',
            key: 'operate',
            width: 130,
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
                    marginRight: '5px',
                  },
                  on: {
                    click: () => {
                      self.saveOrderDetail(params.index);
                    }
                  }
                }, '保存'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                  },
                  on: {
                    click: () => {
                      self.deleteOrderDetail(params.row.id);
                    }
                  }
                }, '删除')
              ])
            }
          }
        ],
        npDetailDatas: [],
        npDetailColumns: [{
            type: 'selection',
            width: 60,
            align: 'center',
          },
          {
            title: '所属序号',
            key: 'needPlanId',
            align: 'center',
          },
          {
            title: '货物名称',
            key: 'goodsName',
            align: 'center',
          },
          {
            title: '类型',
            key: 'needPlanType',
            align: 'center',
            render: (h, params) => h('span', this.needPlanType[params.row.needPlanType])
          },
          {
            title: '数量',
            key: 'count',
            align: 'center',
          },
        ],
        npDetailIds: [],
        payRecordColumnsArr: [{
            title: '创建人',
            key: 'creatorName',
            align: 'center',
          },
          {
            title: '订单序号',
            key: 'orderId',
            align: 'center',
          },
          {
            title: '创建时间',
            key: 'createTime',
            align: 'center',
          },
          {
            title: '凭据',
            key: 'proofUrl',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.allowOtherEdit && params.row.id === null ? [h('Upload', {
                props: {
                  beforeUpload: self.setFile,
                  action: '',
                },
                style: {
                  display: 'inline-block',
                }
              }, [h('Button', {
                props: {
                  type: 'dashed',
                  icon: 'ios-camera',
                  size: 'large',
                },
                style: {
                  marginTop: '5px',
                  marginRight: '5px',
                }
              })]), h('span', (self.file && self.file.name) || '')] : params.row.proofUrl ? h('img', {
                attrs: {
                  src: 'api/img/' + params.row.proofUrl,
                },
                style: {
                  height: '40px',
                  width: '40px',
                  marginTop: '5px',
                  cursor: 'pointer',
                },
                on: {
                  click: function() {
                    self.fileSrc = 'api/img/' + params.row.proofUrl;
                    self.fileModal = true;
                  }
                }
              }) : h('span', '无');
            },
          },
          {
            title: '金额',
            key: 'totalPrice',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.allowOtherEdit && params.row.id === null ? h('InputNumber', {
                props: {
                  value: self.payRecordDatas[params.index].totalPrice,
                  min: 0,
                  max: +(self.order.totalPrice - self.order.finishPrice).toFixed(2),
                  step: 0.1
                },
                on: {
                  input: function(value) {
                    self.payRecordDatas[params.index].totalPrice = value;
                  }
                }
              }) : h('span', '￥ ' + +params.row.totalPrice);
            }
          },
          {
            title: '操作',
            key: 'operate',
            width: 130,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return h('div', [
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small',
                    disabled: params.row.id !== null,
                  },
                  style: {
                    marginRight: '5px',
                  },
                  on: {
                    click: () => {
                      self.savePayRecord(params.index);
                    }
                  }
                }, '保存'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                    disabled: params.row.id !== null,
                  },
                  on: {
                    click: () => {
                      self.deletePayRecord(params.index);
                    }
                  }
                }, '删除')
              ])
            }
          }
        ],
        payRecordDatas: [],
        storeApplyColumns: [{
            title: '序号',
            key: 'id',
            align: 'center',
          },
          {
            title: '创建人',
            key: 'creatorName',
            align: 'center',
          },
          {
            title: '创建时间',
            key: 'createTime',
            align: 'center',
          },
          {
            title: '类型',
            key: 'inOutType',
            align: 'center',
            render: (h, params) => h('span', this.inOutType[params.row.inOutType]),
          },
          {
            title: '凭据',
            key: 'proofUrl',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return params.row.proofUrl ? h('img', {
                attrs: {
                  src: 'api/img/' + params.row.proofUrl,
                },
                style: {
                  height: '40px',
                  width: '40px',
                  marginTop: '5px',
                  cursor: 'pointer',
                },
                on: {
                  click: function() {
                    self.fileSrc = 'api/img/' + params.row.proofUrl;
                    self.fileModal = true;
                  }
                }
              }) : h('span', '无');
            },
          },
          {
            title: '状态',
            key: 'state',
            align: 'center',
            render: (h, params) => h('Tag', {
              props: {
                color: this.saStateColor[params.row.state]
              }
            }, this.saState[params.row.state])
          },
          {
            title: '操作',
            key: 'operate',
            width: 130,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return h('div', [
                h('Button', {
                  props: {
                    type: 'success',
                    size: 'small',
                  },
                  style: {
                    marginRight: '5px',
                  },
                  on: {
                    click: () => {
                      self.editSADetail(params.row.id,params.row.state);
                    }
                  }
                }, '详情'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                    disabled: !(self.allowOtherEdit && params.row.state === 0),
                  },
                  on: {
                    click: () => {
                      self.deleteStoreApply(params.row.id,params.row.syncTime);
                    }
                  }
                }, '删除')
              ])
            }
          }
        ],
        storeApplyDatas: [],
        saDetailColumnsArr: [{
            title: '货物序号',
            key: 'goodsId',
            align: 'center',
          },
          {
            title: '货物名称',
            key: 'goodsName',
            align: 'center',
          },
          {
            title: '货物库存',
            key: 'goodsCount',
            align: 'center',
          },
          {
            title: '申请量',
            key: 'preInOutCount',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.allowOtherEdit && params.row.state === 0 ? h('InputNumber', {
                props: {
                  value: self.saDetailDatas[params.index].preInOutCount,
                  min: 0,
                  max: self.inOutValue===0?params.row.orderCount:Math.min(params.row.goodsCount, params.row.orderCount),
                  step: 1
                },
                on: {
                  input: function(value) {
                    self.saDetailDatas[params.index].preInOutCount = value;
                  }
                }
              }) : h('span', params.row.preInOutCount);
            }
          },
          {
            title: '批准量',
            key: 'reaInOutCount',
            align: 'center',
          },
          {
            title: '操作',
            key: 'operate',
            width: 130,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return h('div', [
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small',
                    disabled: !(self.allowOtherEdit && params.row.state === 0),
                  },
                  style: {
                    marginRight: '5px',
                  },
                  on: {
                    click: () => {
                      self.saveSADetail(params.index);
                    }
                  }
                }, '保存'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                    disabled: !(self.allowOtherEdit && params.row.state === 0),
                  },
                  on: {
                    click: () => {
                      self.deleteSADetail(params.index);
                    }
                  }
                }, '删除')
              ])
            }
          }
        ],
        saDetailColumns: [],
        saDetailDatas: [],
        goodsList: [],
        goodsData: {
          goodsId: null,
          count: null,
        },
        goodsRules: {
          goodsId: [{
            type: 'number',
            required: true,
            message: '（货物不能为空！）',
            trigger: 'change'
          }],
          count: [{
            type: 'number',
            required: true,
            message: '（数目不能为空！）',
            trigger: 'change'
          }]
        },
      };
    },
    computed: {
      ...mapGetters(['role', 'id', 'name']),
      allowOrderEdit() {
        let self = this;
        return self.order.state === 0 && (self.role === 'admin' || self.order.userId === self.id);
      },
      allowOtherEdit() {
        let self = this;
        return self.order.state === 1 && (self.role === 'admin' || self.order.userId === self.id);
      },
      allowAddSA() {
        let self = this;
        return self.allowOtherEdit && (self.storeApplyDatas.length === 0 || self.storeApplyDatas.every(o => o.state ===
          2));
      },
      orderDetailColumns() {
        let self = this;
        return self.allowOrderEdit ? self.orderDetailColumnsArr : self.orderDetailColumnsArr.filter(o => o.key !==
          'operate');
      },
      payRecordColumns() {
        let self = this;
        return self.allowOtherEdit ? self.payRecordColumnsArr : self.payRecordColumnsArr.filter(o => o.key !==
          'operate');
      },
      inOutValue(){
        let self=this;
        return (self.order.type + self.order.isReturn) % 2;
      },
      allowSure(){
        let self=this;
        return self.order.state === 0 && (self.role === 'admin' || self.role === 'manager');
      },
      allowReset(){
        let self=this;
        return self.order.state === 3 && (self.role === 'admin' || self.order.userId === self.id);
      },
      allowDeny(){
        let self=this;
        return self.order.state === 0 && (self.role === 'admin' || self.role === 'manager');
      },
      allowDelete(){
        let self=this;
        return self.order.state === 0 && (self.role === 'admin' || self.order.userId === self.id);
      }
    },
    watch: {
      $route(to, from) {
        let self = this,
          routeNameArr = ['OrderDetail', 'ReturnDetail'];
        if (routeNameArr.some(o => o === to.name) && routeNameArr.some(o => o === from.name)) {
          self.spinShow=true;
          self.hasData=-1;
          self.fetchData();
        }
      }
    },
    activated() {
      let self = this;
      self.spinShow=true;
      self.hasData=-1;
      self.fetchData();
    },
    methods: {
      fetchData() {
        let self = this;
        self.goodsList = [];
        self.npDetailIds = [];
        self.getOrder();
        self.getOrderDetail();
        self.getPayRecord();
        self.getStoreApply();
      },
      getSADetailDatasArr() {
        let self = this,
          results = [];
        self.orderDetailDatas.forEach(o => {
          results.push({
            id: null,
            goodsName: o.goodsName,
            goodsId: o.goodsId,
            goodsCount: o.goodsCount,
            preInOutCount: null,
            reaInOutCount: 0,
            state: 0,
            orderCount: o.count - o.finishCount,
          });
        });
        return results;
      },
      getOrder() {
        let self = this;
        self.$http({
          method: 'post',
          url: '/api/order/getOrderPage',
          data: {
            filter: {
              id: self.$route.query.id,
            },
            start: 1,
            length: 1
          }
        }).then(res => {
          if (res.data.code === 200) {
            if(res.data.data.data.length>0){
              self.hasData=1;
              self.order = res.data.data.data[0];
            }else{
              self.hasData=0;
            }
          } else {
            self.$Modal.error({
              title: '获取详情失败！',
              content: res.data.message
            });
          }
          self.spinShow=false;
        }).catch(err => {
          self.$Modal.error({
            title: '获取详情失败！',
            content: err.message
          });
          self.spinShow=false;
        });
      },
      getOrderDetail() {
        let self = this;
        self.odTableLoading = true;
        self.$http({
          method: 'post',
          url: '/api/orderDetail/getOrderDetail',
          data: {
            orderId: self.$route.query.id,
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.orderDetailDatas = res.data.data.map(o => {
              o.totalPrice = +(o.price * o.count).toFixed(2);
              return o;
            });
          } else {
            self.$Modal.error({
              title: '获取货物列表失败！',
              content: res.data.message
            });
          }
          self.odTableLoading = false;
        }).catch(err => {
          self.$Modal.error({
            title: '获取货物列表失败！',
            content: err.message
          });
          self.odTableLoading = false;
        });
      },
      getNPDetail() {
        let self = this;
        self.npTableLoading = true;
        self.$http({
          method: 'post',
          url: '/api/npDetail/getNPDetail',
          data: {
            state: 0,
            buySellType: self.order.type === 0 ? 0 : 2,
            npState: 1
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.npDetailDatas = res.data.data;
          } else {
            self.$Message.error('获取货物列表失败！' + res.data.message);
          }
          self.npTableLoading = false;
        }).catch(err => {
          self.$Message.error('获取货物列表失败！' + err.message);
          self.npTableLoading = false;
        });
      },
      getPayRecord() {
        let self = this;
        self.prTableLoading = true;
        self.$http({
          method: 'post',
          url: '/api/payRecord/getPayRecord',
          data: {
            orderId: self.$route.query.id
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.payRecordDatas = res.data.data;
          } else {
            self.$Message.error('获取收支列表失败！' + res.data.message);
          }
          self.prTableLoading = false;
        }).catch(err => {
          self.$Message.error('获取收支列表失败！' + err.message);
          self.prTableLoading = false;
        });
      },
      getStoreApply() {
        let self = this;
        self.saTableLoading = true;
        self.$http({
          method: 'post',
          url: '/api/storeApply/getStoreApply',
          data: {
            entityId: self.$route.query.id,
            needPlanOrderType: 1,
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.storeApplyDatas = res.data.data;
          } else {
            self.$Message.error('获取库存列表失败！' + res.data.message);
          }
          self.saTableLoading = false;
        }).catch(err => {
          self.$Message.error('获取库存列表失败！' + err.message);
          self.saTableLoading = false;
        });
      },
      deleteOD() {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/order/deleteOrder',
              data: {
                id: self.order.id,
                syncTime:self.order.syncTime
              }
            }).then(res => {
              self.$Modal.remove();
              if (res.data.code === 200) {
                self.$Message.success('删除成功！');
                self.fetchData();
              } else {
                self.$Message.error('删除失败！' + res.data.message);
              }
            }).catch(err => {
              self.$Modal.remove();
              self.$Message.error('删除失败！' + err.message);
            });
          }
        });
      },
      changeODState(state) {
        let self = this;
        self.$Modal.confirm({
          title: '确认要操作？',
          loading: true,
          onOk: () => {
            if(self.orderDetailDatas.length===0){
              self.$Modal.remove();
              self.$Message.error('保存失败！' + '尚未添加详情');
              return;
            }
            self.$http({
              method: 'post',
              url: '/api/order/saveOrder',
              data: {
                id: self.order.id,
                state: state,
                syncTime:self.order.syncTime
              }
            }).then(res => {
              self.$Modal.remove();
              if (res.data.code === 200) {
                self.$Message.success('保存成功！');
                self.fetchData();
              } else {
                self.$Message.error('保存失败！' + res.data.message);
              }
            }).catch(err => {
              self.$Modal.remove();
              self.$Message.error('保存失败！' + err.message);
            });
          }
        });
      },
      saveOrderDetail(index) {
        let self = this,
          od = self.orderDetailDatas[index];
        if (od.price !== null) {
          self.$http({
            method: 'post',
            url: '/api/orderDetail/saveOrderDetail',
            data: {
              id: od.id,
              price: od.price,
              orderId:self.order.id,
              syncTime:self.order.syncTime
            }
          }).then(res => {
            if (res.data.code === 200) {
              self.$Message.success("保存单价成功！");
              self.fetchData();
            } else {
              self.$Modal.error({
                title: '保存单价失败！',
                content: res.data.message
              });
            }
          }).catch(err => {
            self.$Modal.error({
              title: '保存单价失败！',
              content: err.message
            });
          });
        } else {
          self.$Message.error('单价不能为空！');
        }
      },
      deleteOrderDetail(id) {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/orderDetail/deleteOrderDetail',
              data: {
                id: id,
                syncTime:self.order.syncTime
              }
            }).then(res => {
              self.$Modal.remove();
              if (res.data.code === 200) {
                self.$Message.success('删除成功！');
                self.fetchData();
              } else {
                self.$Message.error('删除失败！' + res.data.message);
              }
            }).catch(err => {
              self.$Modal.remove();
              self.$Message.error('删除失败！' + err.message);
            });
          }
        });
      },
      addOrderDetail() {
        let self = this;
        self.npDetailDatas = [];
        self.odModal = true;
        self.getNPDetail();
      },
      odCancel() {
        let self = this;
        self.odModal = false;
      },
      addNPDetailToOrder() {
        let self = this;
        self.odModalLoading = true;
        if (self.npDetailIds.length > 0) {
          self.$http({
            method: 'post',
            url: '/api/orderDetail/addNPDetailToOrder',
            data: {
              orderId: self.order.id,
              npDetailIds: self.npDetailIds,
              syncTime:self.order.syncTime
            }
          }).then(res => {
            if (res.data.code === 200) {
              self.$Modal.success({
                title: '添加成功！'
              });
              self.fetchData();
            } else {
              self.$Modal.error({
                title: '添加失败！',
                content: res.data.message
              });
            }
            self.odModal = false;
            self.odModalLoading = false;
          }).catch(err => {
            self.$Modal.error({
              title: '添加失败！',
              content: err.message
            });
            self.odModal = false;
            self.odModalLoading = false;
          });
        } else {
          self.$Message.error('请选择后再添加!');
          self.odModalLoading = false;
        }
      },
      setNPDetailIds(selection) {
        let self = this;
        self.npDetailIds = [];
        selection.forEach(o => {
          self.npDetailIds.push(o.id);
        });
      },
      setTotalPrice({
        columns,
        data
      }) {
        let self = this;
        const sums = {};
        columns.forEach((column, index) => {
          const key = column.key;
          if (index === 0) {
            sums[key] = {
              key,
              value: '合计'
            };
            return;
          }
          if (index === 2 || index === 3 || index === 7) {
            const values = data.map(item => Number(item[key]));
            if (!values.every(value => isNaN(value))) {
              const v = values.reduce((prev, curr) => {
                const value = Number(curr);
                if (!isNaN(value)) {
                  return +(prev + curr).toFixed(2);
                } else {
                  return prev;
                }
              }, 0);
              sums[key] = {
                key,
                value: index === 7 ? ('￥ ' + (v * (self.order.discount / 10)).toFixed(2) + '（' + self.order.discount +
                  '折）') : v,
              };
            } else {
              sums[key] = {
                key,
                value: 'N/A'
              };
            }
          } else {
            sums[key] = {
              key,
              value: '—'
            };
          }
        });
        return sums;
      },
      setTotalPay({
        columns,
        data
      }) {
        const sums = {};
        columns.forEach((column, index) => {
          const key = column.key;
          if (index === 0) {
            sums[key] = {
              key,
              value: '合计'
            };
            return;
          }
          if (index === 4) {
            const values = data.map(item => Number(item[key]));
            if (!values.every(value => isNaN(value))) {
              const v = values.reduce((prev, curr) => {
                const value = Number(curr);
                if (!isNaN(value)) {
                  return +(prev + curr).toFixed(2);
                } else {
                  return prev;
                }
              }, 0);
              sums[key] = {
                key,
                value: '￥ ' + v
              };
            } else {
              sums[key] = {
                key,
                value: 'N/A'
              };
            }
          } else {
            sums[key] = {
              key,
              value: '—'
            };
          }
        });
        return sums;
      },
      addPayRecord() {
        let self = this,
          newDate = new Date();
        if (self.payRecordDatas.length > 0 && self.payRecordDatas.some(o => o.id === null)) return;
        self.file = null;
        self.payRecordDatas.push({
          id: null,
          userId: self.id,
          orderId: self.order.id,
          creatorName: self.name,
          createTime: newDate.getFullYear() + '-' + (newDate.getMonth() + 1) + '-' + newDate.getDate(),
          totalPrice: null
        });
      },
      savePayRecord(index) {
        let self = this,
          pr = self.payRecordDatas[index];
        if (pr.totalPrice !== null && pr.totalPrice !== 0 && self.file !== null) {
          self.saveFile().then(res => {
            if (res.data.code === 200) {
              pr.proofUrl = res.data.data;
              self.$http({
                method: 'post',
                url: '/api/payRecord/savePayRecord',
                data: pr
              }).then(res => {
                if (res.data.code === 200) {
                  self.payRecordDatas[index].id = res.data.data;
                  self.getOrder();
                  self.$Message.success("保存收支记录成功！");
                } else {
                  self.$Modal.error({
                    title: '保存收支记录失败！',
                    content: res.data.message
                  });
                }
              }).catch(err => {
                self.$Modal.error({
                  title: '保存收支记录失败！',
                  content: err.message
                });
              });
            } else {
              self.$Modal.error({
                title: '保存收支记录失败！',
                content: res.data.message
              });
            }
          }).catch(err => {
            self.$Modal.error({
              title: '保存收支记录失败！',
              content: err.message
            });
          });
        } else {
          self.$Message.error('金额或凭据不能为空！');
        }
      },
      deletePayRecord(index) {
        let self = this,
          pr = self.payRecordDatas[index];
        if (pr.id === null) {
          self.payRecordDatas.splice(index, 1);
        } else {
          self.$Modal.confirm({
            title: '确认删除？',
            loading: true,
            content: '删除后将不能恢复！',
            onOk: () => {
              self.$http({
                method: 'post',
                url: '/api/payRecord/deletePayRecord',
                data: {
                  id: pr.id
                }
              }).then(res => {
                self.$Modal.remove();
                if (res.data.code === 200) {
                  self.$Message.success('删除成功！');
                  self.payRecordDatas.splice(index, 1);
                } else {
                  self.$Message.error('删除失败！' + res.data.message);
                }
              }).catch(err => {
                self.$Modal.remove();
                self.$Message.error('删除失败！' + err.message);
              });
            }
          });
        }
      },
      addStoreApply() {
        let self = this;
        self.file = null;
        self.payRecordDatas.forEach((value, index) => {
          if (value.id === null) {
            self.payRecordDatas.splice(index, 1);
          }
        });
        self.saModalTitle = '添加库存申请';
        self.saDetailDatas = self.getSADetailDatasArr();
        self.saDetailColumns = self.saDetailColumnsArr.filter(o => o.key !== 'operate');
        self.isSaAdd = true;
        self.saModal = true;
      },
      saveSADetail(index) {
        let self = this,
          sad = self.saDetailDatas[index];
        sad.syncTime=self.storeApplyDatas.filter(o=>o.id===self.saId)[0].syncTime;
        if (sad.preInOutCount !== null && sad.preInOutCount !== 0) {
          self.$http({
            method: 'post',
            url: '/api/saDetail/saveSADetail',
            data: sad
          }).then(res => {
            if (res.data.code === 200) {
              self.getStoreApply();
              self.saDetailDatas[index].id = res.data.data;
              self.$Message.success("保存货物数量成功！");
            } else {
              self.$Modal.error({
                title: '保存货物数量失败！',
                content: res.data.message
              });
            }
          }).catch(err => {
            self.$Modal.error({
              title: '保存货物数量失败！',
              content: err.message
            });
          });
        } else {
          self.$Message.error('数量不能为空！');
        }
      },
      deleteSADetail(index) {
        let self = this,
          sad = self.saDetailDatas[index];
        if (sad.id === null) {
          sad.preInOutCount = null;
        } else {
          self.$Modal.confirm({
            title: '确认删除？',
            loading: true,
            content: '删除后将不能恢复！',
            onOk: () => {
              self.$http({
                method: 'post',
                url: '/api/saDetail/deleteSADetail',
                data: {
                  id: sad.id,
                  storeApplyId:sad.storeApplyId,
                  syncTime:self.storeApplyDatas.filter(o=>o.id===self.saId)[0].syncTime
                }
              }).then(res => {
                self.$Modal.remove();
                if (res.data.code === 200) {
                  self.getStoreApply();
                  sad.id = null;
                  sad.preInOutCount = null;
                  sad.state = 0;
                  self.$Message.success('删除成功！');
                } else {
                  self.$Message.error('删除失败！' + res.data.message);
                }
              }).catch(err => {
                self.$Modal.remove();
                self.$Message.error('删除失败！' + err.message);
              });
            }
          });
        }
      },
      editSADetail(id,state) {
        let self = this;
        self.saId=id;
        self.saModalTitle = '查看库存申请';
        self.saDetailColumns = state === 2 ? self.saDetailColumnsArr.filter(o => o.key !== 'operate') : self.saDetailColumnsArr;
        self.isSaAdd = false;
        self.sadTableLoading = true;
        self.saModal = true;
        self.$http({
          method: 'post',
          url: '/api/saDetail/getSADetail',
          data: {
            storeApplyId: id
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.saDetailDatas = self.getSADetailDatasArr().map(o => {
              o.storeApplyId = id;
              return res.data.data.filter(d => d.goodsId === o.goodsId).map(d => {
                d.orderCount = o.orderCount;
                return d;
              })[0] || o;
            });
          } else {
            self.$Message.error('获取库存详情列表失败！' + res.data.message);
          }
          self.sadTableLoading = false;
        }).catch(err => {
          self.$Message.error('获取库存详情列表失败！' + err.message);
          self.sadTableLoading = false;
        });
      },
      deleteStoreApply(id,syncTime) {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/storeApply/deleteStoreApply',
              data: {
                id: id,
                syncTime:syncTime
              }
            }).then(res => {
              self.$Modal.remove();
              if (res.data.code === 200) {
                self.$Message.success('删除成功！');
                self.getStoreApply();
              } else {
                self.$Message.error('删除失败！' + res.data.message);
              }
            }).catch(err => {
              self.$Modal.remove();
              self.$Message.error('删除失败！' + err.message);
            });
          }
        });
      },
      saveStoreApply() {
        let self = this,
          newDate = new Date(),
          sa = {
            userId: self.id,
            entityId: self.order.id,
            needPlanOrderType: 1,
            inOutType: self.inOutValue,
            createTime: newDate.getFullYear() + '-' + (newDate.getMonth() + 1) + '-' + newDate.getDate(),
            state: 0,
            syncTime:self.order.syncTime
          },
          sadList = self.saDetailDatas.filter(o => o.preInOutCount !== null && o.preInOutCount !== 0);
        self.saModalLoading = true;
        if (sadList.length > 0 && self.file !== null) {
          self.saveFile().then(res => {
            if (res.data.code === 200) {
              sa.proofUrl = res.data.data;
              self.$http({
                method: 'post',
                url: '/api/storeApply/addStoreApply',
                data: {
                  storeApply: sa,
                  saDetailList: sadList,
                }
              }).then(res => {
                if (res.data.code === 200) {
                  self.$Modal.success({
                    title: "添加成功！"
                  });
                  self.fetchData();
                } else {
                  self.$Modal.error({
                    title: '添加失败！',
                    content: res.data.message
                  });
                }
                self.saModalLoading = false;
                self.saModal = false;
              }).catch(err => {
                self.$Modal.error({
                  title: '添加失败！',
                  content: err.message
                });
                self.saModalLoading = false;
                self.saModal = false;
              });
            } else {
              self.$Modal.error({
                title: '添加失败！',
                content: res.data.message
              });
              self.saModalLoading = false;
              self.saModal = false;
            }
          }).catch(err => {
            self.$Modal.error({
              title: '添加失败！',
              content: err.message
            });
            self.saModalLoading = false;
            self.saModal = false;
          });
        } else {
          self.$Message.error('请设置货物数量或上传凭据后添加！');
          self.saModalLoading = false;
        }
      },
      saCancel() {
        let self = this;
        self.saModal = false;
      },
      odCancel2() {
        let self = this;
        self.odModal2 = false;
      },
      addOrderDetail2() {
        let self = this;
        self.odModal2 = true;
      },
      addDetailToOrder() {
        let self = this;
        self.odModalLoading2 = true;
        self.$refs['goodsForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/orderDetail/addDetailToOrder',
              data: {
                orderId: self.order.id,
                syncTime:self.order.syncTime,
                ...self.goodsData
              }
            }).then(res => {
              if (res.data.code === 200) {
                self.$Modal.success({
                  title: '添加成功！'
                });
                self.fetchData();
              } else {
                self.$Modal.error({
                  title: '添加失败！',
                  content: res.data.message
                });
              }
              self.odModal2 = false;
              self.odModalLoading2 = false;
            }).catch(err => {
              self.$Modal.error({
                title: '添加失败！',
                content: err.message
              });
              self.odModal2 = false;
              self.odModalLoading2 = false;
            });
          } else {
            self.$Message.error('表单验证出错!');
            self.odModalLoading2 = false;
          }
        })
      },
      getGoodsList() {
        let self = this;
        if (self.goodsList.length === 0) {
          self.$http({
            method: 'get',
            url: '/api/goods/getAllGoods',
          }).then(res => {
            if (res.data.code === 200) {
              let data = res.data.data;
              self.goodsList = data.filter(o => o.type !== 0)
            } else {
              self.$Message.error('获取货物列表失败！' + res.data.message);
            }
          }).catch(err => {
            self.$Message.error('获取货物列表失败！' + err.message);
          });
        }
      },
      //File
      ...fileMethods('api/upload/uploadImg'),
    }
  }
</script>

<style scoped>
  .order-info p {
    line-height: 40px;
    font-size: medium;
  }

  .pr-add {
    margin-top: 10px;
  }
</style>
