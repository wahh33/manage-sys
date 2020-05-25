<template>
  <div>
    <Card :bordered="false">
      <div slot="title">
        <span style="font-size:16px;color:#17233d">{{role==='admin'?orderData.isReturn?'退单':'订单':orderType[orderData.type+orderData.isReturn*2]}}查询&nbsp;&nbsp;</span>
        <Button type="primary" @click="searchPage">
          <Icon type="md-search"></Icon>查找
        </Button>
        <Button type="error" @click="resetPage">
          <Icon type="md-refresh"></Icon>清除
        </Button>
      </div>
      <Row :gutter="15">
        <Col span="4">
        <Input type="text" v-model="filter.id" placeholder="序号..."></Input>
        </Col>
        <Col span="4">
        <Input type="text" v-model="filter.creatorName" placeholder="创建人..."></Input>
        </Col>
        <Col span="4">
        <Input type="text" v-model="filter.clientName" placeholder="供应商/客户..."></Input>
        </Col>
        <Col span="4" v-if="role!=='buyer'&&role!=='seller'">
        <Select v-model="filter.type" placeholder="类型..." clearable>
          <Option v-for="(value,index) in orderType.slice(0+orderData.isReturn*2,2+orderData.isReturn*2)" :key="index"
            :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="4">
        <Select v-model="filter.state" placeholder="状态..." clearable>
          <Option v-for="(value,index) in state" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="4">
        <DatePicker type="date" :value="filter.createTime" placeholder="创建日期..." style="width: 100%" @on-change="setFilterCreateTime"></DatePicker>
        </Col>
      </Row>
    </Card>
    <Card :bordered="false" :style="{marginTop:'20px'}">
      <div slot="title">
        <span style="font-size:16px;color:#17233d">{{role==='admin'?orderData.isReturn?'退单':'订单':orderType[orderData.type+orderData.isReturn*2]}}表格&nbsp;&nbsp;</span>
        <Button v-if="add" type="info" @click="saveOrder(null)">
          <Icon type="md-add"></Icon>添加
        </Button>
      </div>
      <Table border stripe :columns="orderColumns" :data="pageResult.data" :loading="tableLoading" @on-sort-change="sortPage">
        <div slot="footer">
          <Page :total="pageResult.total" transfer show-total show-sizer show-elevator :page-size-opts="pageSizeOpts" :page-size="pageModel.length"
            style="float:right;margin-right:20px" @on-change="selectPage" @on-page-size-change="changeLength" />
        </div>
      </Table>
    </Card>
    <Modal v-model="orderModal" :title="orderTitle" :loading="modalLoading">
      <Form ref="orderForm" :model="orderData" :label-width="100" :rules="orderRules">
        <FormItem label="创建时间" prop="createTime">
          <DatePicker type="date" :value="orderData.createTime" style="width: 100%" :disabled="true"></DatePicker>
        </FormItem>
        <FormItem label="类型" prop="type">
          <Select v-model="orderData.type" placeholder="类型..." :disabled="role!=='admin'" @on-change="changeType">
            <Option v-for="(value,index) in orderType.slice(0+orderData.isReturn*2,2+orderData.isReturn*2)" :key="index"
              :value="index">{{value}}</Option>
          </Select>
        </FormItem>
        <FormItem label="供应商/客户" prop="clientId">
          <Select v-model="orderData.clientId" placeholder="供应商/客户...">
            <Option v-for="(value,index) in clientFilterList" :key="value.id" :value="value.id">{{value.name}}</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
        <Button type="info" :loading="modalLoading" @click="ok">保存</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
  import {
    tableDatas,
    tableMethods
  } from '@/utils/tableHelper.js';
  import {
    typeText
  } from '@/utils/typeText.js';
  import {
    mapGetters
  } from 'vuex';
  export default {
    data() {
      return {
        add: false,
        clientList: [],
        clientFilterList: [],
        modalLoading: false,
        clientType: typeText.clientType,
        orderType: typeText.orderType,
        state: typeText.orderState,
        stateColor: typeText.orderStateColor,
        orderModal: false,
        orderTitle: '',
        orderData: {
          id: null,
          userId: null,
          clientId: null,
          createTime: null,
          type: null,
          state: null,
          isReturn: null,
          syncTime:null
        },
        orderRules: {
          clientId: [{
            type: 'number',
            required: true,
            message: '（供应商/客户不能为空！）',
            trigger: 'change'
          }]
        },
        ...tableDatas({
          id: null,
          creatorName: '',
          clientName: '',
          createTime: '',
          type: null,
          state: null,
          isReturn: null,
        }, 'state'),
        orderColumns: [{
            title: '序号',
            key: 'id',
            sortable: true,
            align: 'center',
          },
          {
            title: '创建人',
            key: 'creatorName',
            sortable: true,
            align: 'center',
          },
          {
            title: '供应商/客户',
            key: 'clientName',
            sortable: true,
            align: 'center',
          },
          {
            title: '类型',
            key: 'type',
            sortable: true,
            align: 'center',
            render: (h, params) => h('span', this.orderType.slice(0 + this.orderData.isReturn * 2, 2 + this.orderData
              .isReturn * 2)[params.row.type])
          },
          {
            title: '创建时间',
            key: 'createTime',
            sortable: true,
            align: 'center',
          },
          {
            title: '状态',
            key: 'state',
            sortable: true,
            align: 'center',
            sortType: 'asc',
            render: (h, params) => h('Tag', {
              props: {
                color: this.stateColor[params.row.state]
              }
            }, this.state[params.row.state])
          },
          {
            title: '操作',
            key: 'operate',
            align: 'center',
            minWidth: 80,
            render: (h, params) => {
              let self = this,
                editAndDel = params.row.state === 0 && (self.role === 'admin'||params.row.userId === self.id);
              return h('div', [
                h('Button', {
                  props: {
                    type: 'success',
                    size: 'small',
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      self.showDetail(params.row.id);
                    }
                  }
                }, '详情'),
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small',
                  },
                  style: {
                    display: editAndDel ? 'inline-block' : 'none',
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      self.saveOrder(params.row);
                    }
                  }
                }, '编辑'),
              ])
            }
          }
        ]
      }
    },
    computed: {
      ...mapGetters(['id', 'role'])
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        let self = vm,
          type = self.role === 'buyer' ? 0 : self.role === 'seller' ? 1 : null;
        self.orderData.type = self.role === 'seller' ? 1 : 0;
        self.orderData.isReturn = to.name === 'Order' ? false : true;
        self.filter.type = type;
        self.filter.isReturn = to.name === 'Order' ? false : true;
        self.filterEmpty.type = type;
        self.filterEmpty.isReturn = to.name === 'Order' ? false : true;
        self.pageModel.filter.type = type;
        self.pageModel.filter.isReturn = to.name === 'Order' ? false : true;
        self.add = self.role !== 'manager';
        self.getPage();
      })
    },
    methods: {
      ...tableMethods('/api/order/getOrderPage'),
      setFilterCreateTime(value) {
        let self = this;
        self.filter.createTime = value;
      },
      saveOrder(data) {
        let self = this,
          newDate = new Date();
        self.$refs['orderForm'].resetFields();
        self.getAllClient(data);
        if (data === null) {
          self.orderData.id = null;
          self.orderData.userId = self.id;
          self.orderData.clientId = null;
          self.orderData.createTime = newDate.getFullYear() + '-' + (newDate.getMonth() + 1) + '-' + newDate.getDate();
          self.orderData.type = self.role === 'seller' ? 1 : 0;
          self.orderData.state = 0;
          self.orderData.syncTime=null;
          self.orderTitle = '添加' + (self.role === 'admin' ? '订单' : self.orderType[self.orderData.type]);
        } else {
          self.orderData.id = data.id;
          self.orderData.userId = data.userId;
          self.orderData.clientId = data.clientId;
          self.orderData.createTime = data.createTime;
          self.orderData.type = data.type;
          self.orderData.state = data.state;
          self.orderData.syncTime=data.syncTime;
          self.orderTitle = '编辑' + (self.role === 'admin' ? '订单' : self.orderType[self.orderData.type]);
        }
        self.orderModal = true;
      },
      ok() {
        let self = this;
        self.modalLoading = true;
        self.$refs['orderForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/order/saveOrder',
              data: self.orderData
            }).then(res => {
              if (res.data.code === 200) {
                self.$Modal.success({
                  title: '保存成功！'
                });
                self.getPage();
              } else {
                self.$Modal.error({
                  title: '保存失败！',
                  content: res.data.message
                });
              }
              self.orderModal = false;
              self.modalLoading = false;
            }).catch(err => {
              self.$Modal.error({
                title: '保存失败！',
                content: err.message
              });
              self.orderModal = false;
              self.modalLoading = false;
            });
          } else {
            self.$Message.error('表单验证出错!');
            self.modalLoading = false;
          }
        })
      },
      cancel() {
        let self = this;
        self.orderModal = false;
      },
      showDetail(id) {
        let self = this;
        self.$router.push({
          path: self.orderData.isReturn ? 'returnDetail' : 'orderDetail',
          query: {
            id: id
          }
        });
      },
      getAllClient(value) {
        let self = this;
        if (self.clientList.length === 0) {
          self.$http({
            method: 'get',
            url: '/api/client/getAllClient',
          }).then(res => {
            if (res.data.code === 200) {
              let data = res.data.data;
              self.clientList = data;
              if (value === null) {
                self.clientFilterList = self.role === 'seller' ? self.clientList.filter(o => o.type === 1) : self.clientList
                  .filter(o => o.type === 0);
              } else {
                self.clientFilterList = self.clientList.filter(o => o.type === value.type);
              }
            } else {
              self.$Message.error('获取供应商/客户列表失败！' + res.data.message);
            }
          }).catch(err => {
            self.$Message.error('获取供应商/客户列表失败！' + err.message);
          });
        } else {
          if (value === null) {
            self.clientFilterList = self.role === 'seller' ? self.clientList.filter(o => o.type === 1) : self.clientList
              .filter(o => o.type === 0);
          } else {
            self.clientFilterList = self.clientList.filter(o => o.type === value.type);
          }
        }
      },
      changeType(value) {
        let self = this;
        self.clientFilterList = self.clientList.filter(o => o.type === value);
      }
    }
  }
</script>

<style scoped>

</style>
