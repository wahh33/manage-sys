<template>
  <div>
    <Card :bordered="false">
      <p slot="title">{{clientType[clientData.type]}}查询</p>
      <Row :gutter="15">
        <Col span="5">
        <Input type="text" v-model="filter.name" placeholder="名称..."></Input>
        </Col>
        <Col span="5">
        <Input type="text" v-model="filter.phoneNum" placeholder="手机..."></Input>
        </Col>
        <Col span="5">
        <Input type="text" v-model="filter.eMail" placeholder="邮箱..."></Input>
        </Col>
        <Col span="5">
        <Input type="text" v-model="filter.description" placeholder="描述..."></Input>
        </Col>
        <Col span="4">
        <Button type="primary" @click="searchPage">
          <Icon type="md-search"></Icon>查找
        </Button>
        <Button type="error" @click="resetPage">
          <Icon type="md-refresh"></Icon>清除
        </Button>
        </Col>
      </Row>
    </Card>
    <Card :bordered="false" :style="{marginTop:'20px'}">
      <div slot="title">
        <span style="font-size:16px;color:#17233d">{{clientType[clientData.type]}}表格&nbsp;&nbsp;</span>
        <Button type="info" @click="saveClient(null)">
          <Icon type="md-add"></Icon>添加
        </Button>
      </div>
      <Table border stripe :columns="clientColumns" :data="pageResult.data" :loading="tableLoading" @on-sort-change="sortPage">
        <div slot="footer">
          <Page :total="pageResult.total" transfer show-total show-sizer show-elevator :page-size-opts="pageSizeOpts" :page-size="pageModel.length"
            style="float:right;margin-right:20px" @on-change="selectPage" @on-page-size-change="changeLength" />
        </div>
      </Table>
    </Card>
    <Modal v-model="clientModal" :title="clientTitle" :loading="modalLoading">
      <Form ref="clientForm" :model="clientData" :label-width="80" :rules="clientRules">
        <FormItem label="名称" prop="name">
          <Input v-model="clientData.name" placeholder="名称..."></Input>
        </FormItem>
        <FormItem label="手机" prop="phoneNum">
          <Input v-model="clientData.phoneNum" placeholder="手机..."></Input>
        </FormItem>
        <FormItem label="邮箱" prop="eMail">
          <Input v-model="clientData.eMail" placeholder="邮箱..."></Input>
        </FormItem>
        <FormItem label="地址" prop="address">
          <Input v-model="clientData.address" placeholder="地址..."></Input>
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input v-model="clientData.description" placeholder="描述..." type="textarea" maxlength="200" show-word-limit></Input>
        </FormItem>
        <FormItem label="折扣" prop="discount" v-if="clientData.type===1">
          <InputNumber :max="10" :min="1" :step="0.5" :precision="1" v-model="clientData.discount" placeholder="折扣..."
            @on-blur="discountBlur" :formatter="value => `${value} 折`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')" :parser="value => value.replace(/\折\s?|(,*)/g, '')"></InputNumber>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
        <Button type="info" :loading="modalLoading" @click="ok">保存</Button>
      </div>
    </Modal>
    <Modal v-model="chartModal" :footer-hide="true" width="800">
      <z-xdatechart :chartTitle="chartTitle" timeKey="createTime" :countKey="['totalPrice']" :seriesName="['金额']"
        :filter="chartFilter" :isInit="true" width="750px" style="padding: 20px;" url="/api/payRecord/getPayRecordSum"></z-xdatechart>
    </Modal>
  </div>
</template>

<script>
  import {
    tableDatas,
    tableMethods
  } from '@/utils/tableHelper.js';
  import {
    mapGetters
  } from 'vuex';
  import {
    validatePhoneNum
  } from '@/utils/validator.js';
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
        modalLoading: false,
        clientType: typeText.clientType,
        clientModal: false,
        clientTitle: '',
        clientData: {
          id: null,
          name: '',
          phoneNum: '',
          eMail: '',
          description: '',
          address: '',
          discount: 10.0,
          type: this.$route.name === 'Supplier' ? 0 : 1
        },
        clientRules: {
          name: [{
              required: true,
              message: '（名称不能为空！）',
              trigger: 'blur'
            },
            {
              type: 'string',
              max: 50,
              message: '（名称长度不能超过50！）',
              trigger: 'blur'
            }
          ],
          phoneNum: [{
              required: true,
              message: '（手机不能为空！）',
              trigger: 'blur'
            },
            {
              validator: validatePhoneNum,
              trigger: 'blur'
            }
          ],
          eMail: [{
              required: true,
              message: '（邮箱不能为空！）',
              trigger: 'blur'
            },
            {
              type: 'email',
              message: '（邮箱格式不正确！）',
              trigger: 'blur'
            }
          ],
          description: [{
            type: 'string',
            max: 200,
            message: '（描述长度不能超过200！）',
            trigger: 'blur'
          }]
        },
        ...tableDatas({
          name: '',
          description: '',
          phoneNum: '',
          eMail: '',
          type: null
        }, 'name'),
        clientColumns: [],
        clientColumnsArr: [{
            title: '序号',
            key: 'id',
            sortable: true,
            align: 'center',
          },
          {
            title: '名称',
            key: 'name',
            sortable: true,
            align: 'center',
            sortType: 'asc',
          },
          {
            title: '手机',
            key: 'phoneNum',
            sortable: true,
            align: 'center'
          },
          {
            title: '邮箱',
            key: 'eMail',
            sortable: true,
            align: 'center',
          },
          {
            title: '折扣',
            key: 'discount',
            sortable: true,
            align: 'center',
            render: (h, params) => h('span', params.row.discount + '折')
          },
          {
            title: '地址',
            key: 'address',
            sortable: true,
            align: 'center',
            render: (h, params) => {
              if (params.row.address) {
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
                    }, params.row.address),
                    h('div', {
                      slot: 'content',
                      style: {
                        whiteSpace: 'normal',
                        wordWrap: 'break-word'
                      }
                    }, params.row.address)
                  ]);
              } else {
                return h('span', '无');
              }
            }
          },
          {
            title: '描述',
            key: 'description',
            sortable: true,
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
                        wordWrap: 'break-word'
                      }
                    }, params.row.description)
                  ]);
              } else {
                return h('span', '无');
              }
            }
          },
          {
            title: '操作',
            key: 'operate',
            align: 'center',
            render: (h, params) => {
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
                      this.saveClient(params.row);
                    }
                  }
                }, '编辑'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.deleteClient(params.row.id);
                    }
                  }
                }, '删除'),
                h('Button', {
                  props: {
                    type: 'success',
                    size: 'small',
                  },
                  on: {
                    click: () => {
                      this.showChart(params.row.id, params.row.name);
                    }
                  }
                }, '统计')
              ])
            }
          }
        ]
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        let type = to.name === 'Supplier' ? 0 : 1;
        vm.clientData.type = type;
        vm.filter.type = type;
        vm.filterEmpty.type = type;
        vm.pageModel.filter.type = type;
        vm.clientColumns = vm.clientColumnsArr.filter(o => type === 0 ? o.key !== 'discount' : true);
        vm.getPage();
      })
    },
    methods: {
      discountBlur() {
        let self = this;
        if (!self.clientData.discount) self.clientData.discount = 10.0;
      },
      ...tableMethods('/api/client/getClientPage'),
      saveClient(data) {
        let self = this;
        self.$refs['clientForm'].resetFields();
        if (data === null) {
          self.clientTitle = '添加' + self.clientType[self.clientData.type];
          self.clientData.id = null;
        } else {
          self.clientTitle = '编辑' + self.clientType[self.clientData.type];
          self.clientData.id = data.id;
          self.clientData.name = data.name;
          self.clientData.phoneNum = data.phoneNum;
          self.clientData.eMail = data.eMail;
          self.clientData.address = data.address;
          self.clientData.description = data.description;
          self.clientData.discount = data.discount;
        }
        self.clientModal = true;
      },
      deleteClient(id) {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/client/deleteClient',
              data: {
                id: id
              }
            }).then(res => {
              self.$Modal.remove();
              if (res.data.code === 200) {
                self.$Message.success('删除成功！');
                self.getPage();
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
      ok() {
        let self = this;
        self.modalLoading = true;
        self.$refs['clientForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/client/saveClient',
              data: self.clientData
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
              self.clientModal = false;
              self.modalLoading = false;
            }).catch(err => {
              self.$Modal.error({
                title: '保存失败！',
                content: err.message
              });
              self.clientModal = false;
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
        self.clientModal = false;
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

</style>
