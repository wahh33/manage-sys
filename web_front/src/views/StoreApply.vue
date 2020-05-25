<template>
  <div>
    <Card :bordered="false">
      <p slot="title">库存申请查询</p>
      <Row :gutter="15">
        <Col span="5">
        <Input type="text" v-model="filter.creatorName" placeholder="创建人..."></Input>
        </Col>
        <Col span="5">
        <Select v-model="filter.inOutType" placeholder="类型..." clearable>
          <Option v-for="(value,index) in inOutType" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="5">
        <DatePicker type="date" :value="filter.createTime" placeholder="创建日期..." style="width: 100%" @on-change="setFilterCreateTime"></DatePicker>
        </Col>
        <Col span="5">
        <Select v-model="filter.state" placeholder="状态..." clearable>
          <Option v-for="(value,index) in state" :key="index" :value="index">{{value}}</Option>
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
    <Card :bordered="false" :style="{marginTop:'20px'}" title="库存申请表">
      <Table border stripe :columns="storeApplyColumns" :data="pageResult.data" :loading="tableLoading" @on-sort-change="sortPage">
        <div slot="footer">
          <Page :total="pageResult.total" transfer show-total show-sizer show-elevator :page-size-opts="pageSizeOpts" :page-size="pageModel.length"
            style="float:right;margin-right:20px" @on-change="selectPage" @on-page-size-change="changeLength" />
        </div>
      </Table>
    </Card>
    <Modal v-model="saModal" title="查看库存申请" :loading="saModalLoading" width="750">
      <Table stripe border :columns="saDetailColumns" :data="saDetailDatas" :loading="sadTableLoading">
      </Table>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
      </div>
    </Modal>
    <Modal v-model="fileModal" :footer-hide="true">
      <img :src="fileSrc" style="width:100%;" />
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
        saId:-1,
        file: null,
        fileModal: false,
        fileSrc: '',
        saModalLoading: false,
        saModal: false,
        sadTableLoading: true,
        inOutType: typeText.inOutType,
        state: typeText.storeApplyState,
        stateColor: typeText.storeApplyStateColor,
        ...tableDatas({
          creatorName: null,
          createTime: null,
          inOutType: null,
          state: null,
        }, 'state'),
        storeApplyColumns: [{
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
            title: '创建时间',
            key: 'createTime',
            sortable: true,
            align: 'center',
          },
          {
            title: '类型',
            key: 'inOutType',
            sortable: true,
            align: 'center',
            render: (h, params) => h('span', this.inOutType[params.row.inOutType])
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
                      self.editSADetail(params.row.id);
                    }
                  }
                }, '详情')
              ])
            }
          }
        ],
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
          },
          {
            title: '批准量',
            key: 'reaInOutCount',
            align: 'center',
            render: (h, params) => {
              let self = this;
              return self.isStoreman && params.row.state === 0 ? h('InputNumber', {
                props: {
                  value: self.saDetailDatas[params.index].reaInOutCount,
                  min: 0,
                  max: params.row.inOutType===0?params.row.preInOutCount:Math.min(params.row.goodsCount, params.row.preInOutCount),
                  step: 1
                },
                on: {
                  input: function(value) {
                    self.saDetailDatas[params.index].reaInOutCount = value;
                  }
                }
              }) : h('span', params.row.reaInOutCount);
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
                    disabled: params.row.state === 1,
                  },
                  style: {
                    marginRight: '5px',
                  },
                  on: {
                    click: () => {
                      self.saveSADetail(params.index);
                    }
                  }
                }, '确认')
              ]);
            }
          }
        ],
        saDetailDatas: [],
      }
    },
    computed: {
      ...mapGetters(['id', 'role']),
      isStoreman() {
        let self = this;
        return self.role === 'storeman' || self.role === 'admin';
      },
      saDetailColumns() {
        let self = this;
        return self.isStoreman ? self.saDetailColumnsArr : self.saDetailColumnsArr.filter(o => o.key !== 'operate');
      },
    },
    activated() {
      let self = this;
      self.getPage();
    },
    methods: {
      ...tableMethods('/api/storeApply/getStoreApplyPage'),
      setFilterCreateTime(value) {
        let self = this;
        self.filter.createTime = value;
      },
      editSADetail(id) {
        let self = this;
        self.saId = id;
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
            self.saDetailDatas = res.data.data;
          } else {
            self.$Message.error('获取库存详情列表失败！' + res.data.message);
          }
          self.sadTableLoading = false;
        }).catch(err => {
          self.$Message.error('获取库存详情列表失败！' + err.message);
          self.sadTableLoading = false;
        });
      },
      saveSADetail(index) {
        let self = this,
          sad = self.saDetailDatas[index];
        if (sad.reaInOutCount !== null && sad.reaInOutCount !== 0) {
          self.$http({
            method: 'post',
            url: '/api/saDetail/finishSADetail',
            data: {
              id: sad.id,
              reaInOutCount: sad.reaInOutCount,
              syncTime:self.pageResult.data.filter(o=>o.id===self.saId)[0].syncTime
            }
          }).then(res => {
            if (res.data.code === 200) {
              self.editSADetail(sad.storeApplyId);
              self.getPage();
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
      cancel() {
        let self = this;
        self.saModal = false;
      },
    }
  }
</script>

<style scoped>

</style>
