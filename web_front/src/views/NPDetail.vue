<template>
  <div>
    <section v-if="hasData===1">
      <Card class="np-info" :bordered="false" title="基础信息">
        <a slot="extra" @click="fetchData" style="float:right" href="javascript:void(0);">
          <Icon class="refresh" title="刷新" type="md-refresh"></Icon>
        </a>
        <Row>
          <Col span="8">
          <p>类型：{{buySellType[np.buySellType]}}{{needPlanType[np.needPlanType]}}</p>
          <p>序号：{{np.id}}</p>
          <p>预计时间：{{np.startTime}} 至 {{np.endTime}}</p>
          </Col>
          <Col span="8">
          <p>创建人：{{np.creatorName}}</p>
          <p>确认人：{{np.surerName||'无'}}</p>
          <p>描述：{{np.description||'无'}}</p>
          </Col>
          <Col span="8">
          <p style="font-size:large">紧急程度：</p>
          <p>
            <Tag :color="levelColor[np.level]">{{level[np.level]}}</Tag>
          </p>
          </Col>
        </Row>
      </Card>
      <Card :bordered="false" title="状态" style="margin-top:20px">
        <Steps :current="np.state===3?1:np.state" :status="np.state===3?'error':'process'">
          <Step title="待确认"></Step>
          <Step :title="np.state===3?'未通过':'进行中'"></Step>
          <Step title="已完成"></Step>
        </Steps>
      </Card>
      <Card class="np-detail" :bordered="false" title="货物数量" style="margin-top:20px">
        <section v-if="allowGoodsAdd" style="margin-bottom:20px">
          <Row>
            <Col span="10">
            <p>{{np.buySellType===1?'添加产品':'添加货物'}}</p>
            <Select :disabled="!allowEdit" v-model="goodsIds" filterable multiple @click.native="getGoodsList">
              <Option v-for="item in goodsListFilter.filter(o=>np.buySellType===0?o.type===0:o.type!==0)" :value="item.id"
                :key="item.id">{{ item.name }}</Option>
            </Select>
            </Col>
            <Col span="4" style="padding: 0 50px;padding-top: 24px;">
            <Button type="primary" @click="addNPDetail" long>
              <Icon type="md-add"></Icon>添加
            </Button>
            </Col>
            <Col span="10" v-if="np.buySellType===1">
            <p>添加物料</p>
            <Select :disabled="!allowPdMatterEdit" v-model="matterIds" filterable multiple @click.native="getGoodsList">
              <Option v-for="item in goodsListFilter.filter(o=>o.type===0)" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            </Col>
          </Row>
        </section>
        <section>
          <Row>
            <p>{{np.buySellType===0?'物料':'产品'}}</p>
            <Table stripe border :columns="npDetailColumns1" :data="npDetailDatas.filter(o=>np.buySellType===0?o.goodsType===0:o.goodsType!==0)"
              :loading="tableLoading">
            </Table>
          </Row>
          <Row v-if="np.buySellType===1" style="margin-top: 20px;">
            <p>物料</p>
            <Table stripe border :columns="npDetailColumns2" :data="npDetailDatas.filter(o=>o.goodsType===0)" :loading="tableLoading">
            </Table>
          </Row>
        </section>
      </Card>
      <Card :bordered="false" style="margin-top:20px" v-if="np.buySellType===1">
        <div slot="title">
          <span style="font-size:16px;color:#17233d">库存申请&nbsp;&nbsp;</span>
        </div>
        <Row>
          <div class="sa-title">
            <span>入库</span>
            <Button v-if="allowAddInSA" type="dashed" @click="addStoreApply(0)">
              <Icon type="md-add"></Icon>添加
            </Button>
          </div>
          <Table stripe border :columns="storeApplyColumns" :data="storeApplyDatas.filter(o=>o.inOutType===0)" :loading="saTableLoading">
          </Table>
        </Row>
        <Row style="margin-top: 20px;">
          <div class="sa-title">
            <span>出库</span>
            <Button v-if="allowAddOutSA" type="dashed" @click="addStoreApply(1)">
              <Icon type="md-add"></Icon>添加
            </Button>
          </div>
          <Table stripe border :columns="storeApplyColumns" :data="storeApplyDatas.filter(o=>o.inOutType===1)" :loading="saTableLoading">
          </Table>
        </Row>
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
        <Button type="info" @click="changeNPState(1)" v-if="allowSure">确认</Button>
        <Button type="success" @click="changeNPState(0)" v-if="allowReset">恢复</Button>
        <Button type="warning" @click="changeNPState(3)" v-if="allowDeny">驳回</Button>
        <Button type="error" @click="deleteNP" v-if="allowDelete">删除</Button>
      </div>
      <Modal v-model="fileModal" :footer-hide="true">
        <img :src="fileSrc" style="width:100%;" />
      </Modal>
    </section>
    <section v-if="hasData===0">
      <div class="no-data">
        <Icon type="ios-close-circle-outline" />
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
    fileMethods,
    fileDatas
  } from '@/utils/fileHelper.js';
  import {
    mapGetters
  } from 'vuex';
  export default {
    data() {
      return {
        ...fileDatas(),
        spinShow: true,
        inOutFlag: -1,
        hasData: -1,
        isSaAdd: false,
        saTableLoading: true,
        saModalLoading: false,
        saModal: false,
        saModalTitle: '',
        saId: -1,
        sadTableLoading: false,
        tableLoading: true,
        npDetailDatas: [],
        npDetailColumnsArr: [{
            title: '货物名称',
            key: 'goodsName',
            align: 'center',
          },
          {
            title: '默认单价',
            key: 'defaultPrice',
            align: 'center',
            render: (h, params) => h('span', '￥' + params.row.defaultPrice)
          },
          {
            title: '库存',
            key: 'goodsCount',
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
            title: '已完成数',
            key: 'finishCount',
            align: 'center',
          },
          {
            title: '数量',
            key: 'count1',
            align: 'center',
            render: (h, params) => {
              let self = this,
                result;
              if (self.allowEdit) {
                let i = -1;
                self.npDetailDatas.forEach((value, index) => {
                  if (value.id === params.row.id) i = index;
                });
                result = h('InputNumber', {
                  props: {
                    value: self.npDetailDatas[i].count,
                    min: 1,
                    step: 1
                  },
                  on: {
                    input: function(value) {
                      self.npDetailDatas[i].count = value;
                    }
                  }
                });
              } else {
                result = h('span', params.row.count);
              }
              return result;
            }
          },
          {
            title: '数量',
            key: 'count2',
            align: 'center',
            render: (h, params) => {
              let self = this,
                result;
              if (self.allowPdMatterEdit) {
                let i = -1;
                self.npDetailDatas.forEach((value, index) => {
                  if (value.id === params.row.id) i = index;
                });
                result = h('InputNumber', {
                  props: {
                    value: self.npDetailDatas[i].count,
                    min: 1,
                    step: 1
                  },
                  on: {
                    input: function(value) {
                      self.npDetailDatas[i].count = value;
                    }
                  }
                });
              } else {
                result = h('span', params.row.count);
              }
              return result;
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
                  },
                  style: {
                    marginRight: '5px',
                  },
                  on: {
                    click: () => {
                      self.saveNPDetail(params.row.id);
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
                      self.deleteNPDetail(params.row.id);
                    }
                  }
                }, '删除')
              ])
            }
          }
        ],
        needPlanType: typeText.needPlanType,
        buySellType: typeText.buySellType,
        level: typeText.level,
        levelColor: typeText.levelColor,
        state: typeText.npDetailState,
        stateColor: typeText.npDetailStateColor,
        inOutType: typeText.inOutType,
        saState: typeText.storeApplyState,
        saStateColor: typeText.storeApplyStateColor,
        np: {
          id: null,
          creatorId: null,
          creatorName: '',
          surerId: null,
          surerName: '',
          level: null,
          startTime: '',
          endTime: '',
          description: '',
          needPlanType: null,
          buySellType: null,
          state: 0,
          syncTime: null
        },
        goodsIds: [],
        matterIds: [],
        goodsList: [],
        goodsListFilter: [],
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
                      self.editSADetail(params.row.id,params.row.state,params.row.inOutType);
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
                      self.deleteStoreApply(params.row.id, params.row.syncTime);
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
                  max: self.inOutFlag===0?params.row.npCount:Math.min(params.row.goodsCount,params.row.npCount),
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
      };
    },
    computed: {
      ...mapGetters(['role', 'id']),
      allowGoodsAdd() {
        let self = this;
        return self.np.state === 0 && (self.role === 'admin' || self.np.creatorId === self.id || ((self.np.needPlanType ===
          1 || self.np.buySellType === 0) ? false : self.role === 'producer'));
      },
      allowPdMatterEdit() {
        let self = this;
        return self.np.state === 0 && (self.role === 'admin' || (self.np.needPlanType === 0 ? self.role === 'producer' :
          self.np.creatorId === self.id));
      },
      allowEdit() {
        let self = this;
        return self.np.state === 0 && (self.role === 'admin' || self.np.creatorId === self.id);
      },
      allowOtherEdit() {
        let self = this;
        return self.np.state === 1 && (self.role === 'admin' || (self.np.needPlanType === 0 ? self.np.surerId === self.id :
          self.np.creatorId === self.id));
      },
      npDetailColumns1() {
        let self = this,
          filterColumns = ['count2'];
        if (!self.allowEdit) filterColumns.push('operate');
        if (self.np.buySellType !== 1) filterColumns.push('finishCount');
        return self.npDetailColumnsArr.filter(o => filterColumns.every(f => f !== o.key));
      },
      npDetailColumns2() {
        let self = this,
          filterColumns = ['count1'];
        if (!self.allowPdMatterEdit) filterColumns.push('operate');
        if (self.np.buySellType !== 1) filterColumns.push('finishCount');
        return self.npDetailColumnsArr.filter(o => filterColumns.every(f => f !== o.key));
      },
      allowAddInSA() {
        let self = this;
        return self.allowOtherEdit && (self.storeApplyDatas.filter(o => o.inOutType === 0).length === 0 || self.storeApplyDatas
          .filter(o => o.inOutType === 0).every(o => o.state ===
            2));
      },
      allowAddOutSA() {
        let self = this;
        return self.allowOtherEdit && (self.storeApplyDatas.filter(o => o.inOutType === 1).length === 0 || self.storeApplyDatas
          .filter(o => o.inOutType === 1).every(o => o.state ===
            2));
      },
      allowSure() {
        let self = this;
        return self.np.state === 0 && (self.role === 'admin' || ((self.np.needPlanType === 1 && self.role === 'manager') ||
          (self.np.needPlanType === 0 && self.np.buySellType === 1 && self.role === 'producer') ||
          (self.np.needPlanType === 0 && self.np.buySellType === 0 && self.role === 'buyer')));
      },
      allowReset() {
        let self = this;
        return self.np.state === 3 && (self.role === 'admin' || self.np.creatorId === self.id);
      },
      allowDeny() {
        let self = this;
        return self.np.state === 0 && self.np.needPlanType === 1 && (self.role === 'admin' || self.role === 'manager');
      },
      allowDelete() {
        let self = this;
        return self.np.state === 0 && (self.role === 'admin' || self.np.creatorId === self.id);
      }
    },
    watch: {
      $route(to, from) {
        let self = this,
          routeNameArr = ['NeedDetail', 'PlanDetail'];
        if (routeNameArr.some(o => o === to.name) && routeNameArr.some(o => o === from.name)) {
          self.spinShow = true;
          self.hasData = -1;
          self.fetchData();
        }
      }
    },
    activated() {
      let self = this;
      self.spinShow = true;
      self.hasData = -1;
      self.fetchData();
    },
    methods: {
      fetchData() {
        let self = this;
        self.getNP();
        self.getNPDetail();
        self.goodsIds = [];
        self.goodsList = [];
        self.matterIds = [];
      },
      //np
      getNP() {
        let self = this;
        self.$http({
          method: 'post',
          url: '/api/needPlan/getNeedPlanPage',
          data: {
            filter: {
              id: self.$route.query.id,
            },
            start: 1,
            length: 1
          }
        }).then(res => {
          if (res.data.code === 200) {
            if (res.data.data.data.length > 0) {
              self.hasData = 1;
              self.np = res.data.data.data[0];
              if (self.np.buySellType === 1) self.getStoreApply();
            } else {
              self.hasData = 0;
            }
          } else {
            self.$Modal.error({
              title: '获取详情失败！',
              content: res.data.message
            });
          }
          self.spinShow = false;
        }).catch(err => {
          self.$Modal.error({
            title: '获取详情失败！',
            content: err.message
          });
          self.spinShow = false;
        });
      },
      getNPDetail() {
        let self = this;
        self.tableLoading = true;
        self.$http({
          method: 'post',
          url: '/api/npDetail/getNPDetail',
          data: {
            needPlanId: self.$route.query.id,
          }
        }).then(res => {
          if (res.data.code === 200) {
            self.npDetailDatas = res.data.data;
          } else {
            self.$Modal.error({
              title: '获取货物列表失败！',
              content: res.data.message
            });
          }
          self.tableLoading = false;
        }).catch(err => {
          self.$Modal.error({
            title: '获取货物列表失败！',
            content: err.message
          });
          self.tableLoading = false;
        });
      },
      getGoodsList() {
        let self = this;
        if (self.goodsList.length === 0) {
          self.$http({
            method: 'get',
            url: '/api/goods/getAllGoods',
          }).then(res => {
            if (res.data.code === 200) {
              self.goodsList = res.data.data;
              self.filterGoodsList();
            } else {
              self.$Message.error('获取货物列表失败！' + res.data.message);
            }
          }).catch(err => {
            self.$Message.error('获取货物列表失败！' + err.message);
          });
        }
      },
      changeNPState(state) {
        let self = this,
          isEmty = self.np.buySellType === 1 ? (self.npDetailDatas.filter(o => o.goodsType === 0).length === 0 || self.npDetailDatas
            .filter(o => o.goodsType === 1).length === 0) : self.npDetailDatas.length === 0;
        self.$Modal.confirm({
          title: '确认要进行此操作？',
          loading: true,
          onOk: () => {
            if (isEmty) {
              self.$Modal.remove();
              self.$Message.error('保存失败！' + '物料/产品详情不能为空');
              return;
            }
            self.$http({
              method: 'post',
              url: '/api/needPlan/saveNeedPlan',
              data: {
                id: self.np.id,
                surerId: state === 0 ? -1 : self.id,
                state: state,
                syncTime: self.np.syncTime
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
      deleteNP() {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/needPlan/deleteNeedPlan',
              data: {
                id: self.np.id,
                syncTime: self.np.syncTime
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
      addNPDetail() {
        let self = this,
          goodsIds = self.np.buySellType === 1 ? [...self.goodsIds, ...self.matterIds] : self.goodsIds;
        if (goodsIds.length !== 0) {
          self.$http({
            method: 'post',
            url: '/api/npDetail/addNPDetailList',
            data: {
              needPlanId: self.np.id,
              goodsIds: goodsIds,
              syncTime: self.np.syncTime
            }
          }).then(res => {
            if (res.data.code === 200) {
              self.$Message.success("添加成功！");
              self.fetchData();
            } else {
              self.$Modal.error({
                title: '添加货物失败！',
                content: res.data.message
              });
            }
            self.tableLoading = false;
          }).catch(err => {
            self.$Modal.error({
              title: '添加货物失败！',
              content: err.message
            });
            self.tableLoading = false;
          });
        } else {
          self.$Message.error("货物为空，请选择后添加！");
        }
      },
      saveNPDetail(id) {
        let self = this,
          npd = self.npDetailDatas.filter(o => o.id === id)[0];
        if (npd.count) {
          self.$http({
            method: 'post',
            url: '/api/npDetail/saveNPDetail',
            data: {
              id: npd.id,
              count: npd.count,
              needPlanId: self.np.id,
              syncTime: self.np.syncTime
            }
          }).then(res => {
            if (res.data.code === 200) {
              self.$Message.success("保存数量成功！");
              self.fetchData();
            } else {
              self.$Modal.error({
                title: '保存数量失败！',
                content: res.data.message
              });
            }
          }).catch(err => {
            self.$Modal.error({
              title: '保存数量失败！',
              content: err.message
            });
          });
        } else {
          self.$Message.error('数量不能为空！');
        }
      },
      deleteNPDetail(id) {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/npDetail/deleteNPDetail',
              data: {
                id: id,
                needPlanId: self.np.id,
                syncTime: self.np.syncTime
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
      filterGoodsList() {
        let self = this;
        self.goodsListFilter = self.goodsList.filter(goods => !self.npDetailDatas.some(npd => npd.goodsId === goods.id &&
          npd.needPlanId == self.$route.query.id));
      },
      //sa
      getSADetailDatasArr() {
        let self = this,
          results = [];
        self.npDetailDatas.forEach(o => {
          if (o.goodsType === (self.inOutFlag === 0 ? 1 : 0)) {
            results.push({
              id: null,
              goodsName: o.goodsName,
              goodsId: o.goodsId,
              goodsCount: o.goodsCount,
              preInOutCount: null,
              reaInOutCount: 0,
              state: 0,
              npCount: o.count - o.finishCount,
            });
          }
        });
        return results;
      },
      getStoreApply() {
        let self = this;
        self.saTableLoading = true;
        self.$http({
          method: 'post',
          url: '/api/storeApply/getStoreApply',
          data: {
            entityId: self.np.id,
            needPlanOrderType: 0,
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
      addStoreApply(inOutType) {
        let self = this;
        self.file = null;
        self.inOutFlag = inOutType;
        self.saModalTitle = '添加库存申请';
        self.saDetailDatas = self.getSADetailDatasArr();
        self.saDetailColumns = self.saDetailColumnsArr.filter(o => o.key !== 'operate');
        self.isSaAdd = true;
        self.saModal = true;
      },
      saveSADetail(index) {
        let self = this,
          sad = self.saDetailDatas[index];
        sad.syncTime = self.storeApplyDatas.filter(o => o.id === self.saId)[0].syncTime;
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
                  storeApplyId: sad.storeApplyId,
                  syncTime: self.storeApplyDatas.filter(o => o.id === self.saId)[0].syncTime
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
      editSADetail(id,state,inOutType) {
        let self = this;
        self.inOutFlag = inOutType;
        self.saId = id;
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
                d.npCount = o.npCount;
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
      deleteStoreApply(id, syncTime) {
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
                syncTime: syncTime
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
            entityId: self.np.id,
            needPlanOrderType: 0,
            inOutType: self.inOutFlag,
            createTime: newDate.getFullYear() + '-' + (newDate.getMonth() + 1) + '-' + newDate.getDate(),
            state: 0,
            syncTime: self.np.syncTime
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
      //File
      ...fileMethods('api/upload/uploadImg'),
    }
  }
</script>

<style scoped>
  .np-info p {
    line-height: 40px;
    font-size: medium;
  }

  .np-detail p {
    font-size: medium;
  }

  .sa-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 10px;
    font-size: medium;
  }
</style>
