<template>
  <div>
    <Card :bordered="false">
      <p slot="title">货物查询</p>
      <Row :gutter="15">
        <Col span="4">
        <Input type="text" v-model="filter.name" placeholder="名称..."></Input>
        </Col>
        <Col span="4">
        <Select v-model="filter.type" placeholder="类型..." clearable>
          <Option v-for="(value,index) in goodsType" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="4">
        <Input type="text" v-model="filter.description" placeholder="描述..."></Input>
        </Col>
        <Col span="4" v-if="!isGoodsSelect">
        <InputNumber :min="0.01" :step="0.1" v-model="filter.defaultPrice" placeholder="默认单价..." :formatter="value => `￥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
          :parser="value => value.replace(/\￥\s?|(,*)/g, '')" style="width:100%"></InputNumber>
        </Col>
        <Col span="4" v-if="isGoodsSelect">
        <Select v-model="filter.state" placeholder="状态..." clearable>
          <Option v-for="(value,index) in state" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="4" v-if="isGoodsSelect">
        <Select v-model="filter.isWarn" placeholder="预警..." clearable>
          <Option :value="1">开启</Option>
          <Option :value="0">关闭</Option>
        </Select>
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
        <span style="font-size:16px;color:#17233d">货物表格&nbsp;&nbsp;</span>
        <Button v-if="!isGoodsSelect" type="info" @click="saveGoods(null)">
          <Icon type="md-add"></Icon>添加
        </Button>
      </div>
      <Table border stripe :columns="goodsColumns" :data="pageResult.data" :loading="tableLoading" @on-sort-change="sortPage">
        <div slot="footer">
          <Page :total="pageResult.total" transfer show-total show-sizer show-elevator :page-size-opts="pageSizeOpts" :page-size="pageModel.length"
            style="float:right;margin-right:20px" @on-change="selectPage" @on-page-size-change="changeLength" />
        </div>
      </Table>
    </Card>
    <Modal v-model="goodsModal" :title="goodsTitle" :loading="modalLoading">
      <Form ref="goodsForm" :model="goodsData" :label-width="80" :rules="goodsRules">
        <FormItem label="名称" prop="name">
          <Input v-model="goodsData.name" placeholder="名称..."></Input>
        </FormItem>
        <FormItem label="类型" prop="type">
          <Select v-model="goodsData.type" placeholder="类型...">
            <Option v-for="(value,index) in goodsType" :disabled="filterGoodsType.every(o=>o!==index)" :key="index"
              :value="index">{{value}}</Option>
          </Select>
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input v-model="goodsData.description" placeholder="描述..." type="textarea" maxlength="200" show-word-limit></Input>
        </FormItem>
        <FormItem label="默认单价" prop="defaultPrice">
          <InputNumber :min="0.01" :step="0.1" v-model="goodsData.defaultPrice" placeholder="默认单价..." @on-blur="defaultPriceBlur"
            :formatter="value => `￥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')" :parser="value => value.replace(/\￥\s?|(,*)/g, '')"
            style="width:100%"></InputNumber>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
        <Button type="info" :loading="modalLoading" @click="ok">保存</Button>
      </div>
    </Modal>
    <Modal v-model="chartModal" :footer-hide="true" width="800">
      <z-xdatechart :chartTitle="chartTitle" timeKey="createTime" :countKey="['totalCount','inCount','outCount']"
        :seriesName="['总量','入量','出量']" :filter="chartFilter" :isInit="true" width="750px" style="padding: 20px;" url="/api/saDetail/getSADetailSum"></z-xdatechart>
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
  import zXdatechart from '@/components/z-xdatechart.vue';
  export default {
    components: {
      zXdatechart
    },
    data() {
      return {
        //chart
        chartModal: false,
        chartTitle: '货物出入库统计',
        chartFilter: {},
        //////////////////////////////////////////////////
        modalLoading: false,
        goodsType: typeText.goodsType,
        state: typeText.goodsState,
        stateColor: typeText.goodsStateColor,
        goodsModal: false,
        goodsTitle: '',
        goodsData: {
          id: null,
          name: '',
          type: 0,
          defaultPrice: 1,
          description: ''
        },
        goodsRules: {
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
          description: [{
            type: 'string',
            max: 200,
            message: '（描述长度不能超过200！）',
            trigger: 'blur'
          }]
        },
        ...tableDatas({
          name: '',
          type: null,
          description: '',
          defaultPrice: null,
          isWarn: null,
          state: null,
        }, 'type'),
        goodsColumnsArr: [{
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
          },
          {
            title: '类型',
            key: 'type',
            sortable: true,
            align: 'center',
            sortType: 'asc',
            render: (h, params) => h('span', this.goodsType[params.row.type])
          },
          {
            title: '默认单价',
            key: 'defaultPrice',
            sortable: true,
            align: 'center',
            render: (h, params) => h('span', '￥' + params.row.defaultPrice)
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
            title: '状态',
            key: 'state',
            sortable: true,
            align: 'center',
            render: (h, params) => h('Tag', {
              props: {
                color: this.stateColor[params.row.state]
              }
            }, this.state[params.row.state]),
          },
          {
            title: '库存量',
            key: 'count',
            sortable: true,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.isStoreman ? h('InputNumber', {
                props: {
                  value: self.pageResult.data[params.index].count,
                  min: 0,
                  step: 1
                },
                on: {
                  input: function(value) {
                    self.pageResult.data[params.index].count = value;
                  }
                }
              }) : h('span', +params.row.count);
            }
          },
          {
            title: '最低量',
            key: 'low',
            sortable: true,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.isStoreman ? h('InputNumber', {
                props: {
                  value: self.pageResult.data[params.index].low,
                  min: 0,
                  step: 1
                },
                on: {
                  input: function(value) {
                    self.pageResult.data[params.index].low = value;
                  }
                }
              }) : h('span', +params.row.low);
            }
          },
          {
            title: '最高量',
            key: 'high',
            sortable: true,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.isStoreman ? h('InputNumber', {
                props: {
                  value: self.pageResult.data[params.index].high,
                  min: 0,
                  step: 1
                },
                on: {
                  input: function(value) {
                    self.pageResult.data[params.index].high = value;
                  }
                }
              }) : h('span', +params.row.high);
            }
          },
          {
            title: '预警',
            key: 'isWarn',
            sortable: true,
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.isStoreman ? h('i-switch', {
                props: {
                  value: self.pageResult.data[params.index].isWarn,
                  size: 'large',
                },
                on: {
                  input: function(value) {
                    self.pageResult.data[params.index].isWarn = value;
                  }
                }
              }, [
                h('span', {
                  slot: 'open'
                }, '开启'),
                h('span', {
                  slot: 'close'
                }, '关闭'),
              ]) : h('span', params.row.isWarn ? '开启' : '关闭');
            }
          },
          {
            title: '操作',
            key: 'operate',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return h('div', [
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small',
                    disabled: self.filterGoodsType.every(o => o !== params.row.type)
                  },
                  style: {
                    marginRight: '5px',
                    display: self.isGoodsSelect ? 'none' : 'inline-block',
                  },
                  on: {
                    click: () => {
                      self.saveGoods(params.row);
                    }
                  }
                }, '编辑'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                    disabled: self.filterGoodsType.every(o => o !== params.row.type)
                  },
                  style: {
                    marginRight: '5px',
                    display: self.isGoodsSelect ? 'none' : 'inline-block',
                  },
                  on: {
                    click: () => {
                      self.deleteGoods(params.row.id);
                    }
                  }
                }, '删除'),
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small',
                  },
                  style: {
                    marginRight: '5px',
                    display: self.isStoreman && self.isGoodsSelect ? 'inline-block' : 'none',
                  },
                  on: {
                    click: () => {
                      self.changeGoods(params.index);
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
                      self.showChart(params.row.id, params.row.name);
                    }
                  }
                }, '统计')
              ])
            }
          }
        ]
      }
    },
    computed: {
      ...mapGetters(['id', 'role']),
      filterGoodsType() {
        let self = this;
        switch (self.role) {
          case 'buyer':
            return [0, 2];
            break;
          case 'producer':
            return [1, 2];
            break;
          case 'admin':
          case 'manager':
          default:
            return [0, 1, 2];
            break;
        }
      },
      isStoreman() {
        let self = this;
        return self.role === 'storeman' || self.role === 'admin';
      },
      isGoodsSelect() {
        let self = this;
        return self.$route.name === 'GoodsSelect';
      },
      goodsColumns() {
        let self = this,
          filterArr = self.isGoodsSelect ? ['defaultPrice'] : ['count', 'high', 'low', 'isWarn', 'state'];
        return self.goodsColumnsArr.filter(o => filterArr.every(f => f !== o.key));
      },
    },
    activated() {
      let self = this;
      self.getPage();
    },
    methods: {
      defaultPriceBlur() {
        let self = this;
        if (!self.goodsData.defaultPrice) self.goodsData.defaultPrice = 1;
      },
      ...tableMethods('/api/goods/getGoodsPage'),
      saveGoods(data) {
        let self = this;
        self.$refs['goodsForm'].resetFields();
        if (data === null) {
          self.goodsTitle = '添加货物';
          self.goodsData.id = null;
          self.goodsData.type = self.filterGoodsType[0];
        } else {
          self.goodsTitle = '编辑货物';
          self.goodsData.id = data.id;
          self.goodsData.name = data.name;
          self.goodsData.type = data.type;
          self.goodsData.defaultPrice = data.defaultPrice;
          self.goodsData.description = data.description;
        }
        self.goodsModal = true;
      },
      deleteGoods(id) {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/goods/deleteGoods',
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
        self.$refs['goodsForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/goods/saveGoods',
              data: self.goodsData
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
              self.goodsModal = false;
              self.modalLoading = false;
            }).catch(err => {
              self.$Modal.error({
                title: '保存失败！',
                content: err.message
              });
              self.goodsModal = false;
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
        self.goodsModal = false;
      },
      changeGoods(index) {
        let self = this,
          goods = self.pageResult.data[index];
        self.$Modal.confirm({
          title: '确认更改？',
          loading: true,
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/goods/saveGoods',
              data: goods
            }).then(res => {
              self.$Modal.remove();
              if (res.data.code === 200) {
                self.$Message.success('更改成功！');
                self.getPage();
              } else {
                self.$Message.error('更改失败！' + res.data.message);
              }
            }).catch(err => {
              self.$Modal.remove();
              self.$Message.error('更改失败！' + err.message);
            });
          }
        });
      },
      showChart(goodsId, goodsName) {
        let self = this;
        self.chartTitle = '货物出入库统计(' + goodsName + ')';
        self.chartFilter = {
          goodsId: goodsId
        };
        self.chartModal = true;
      },
    }
  }
</script>

<style scoped>

</style>
