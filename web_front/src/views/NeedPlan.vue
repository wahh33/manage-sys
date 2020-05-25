<template>
  <div>
    <Card :bordered="false">
      <div slot="title">
        <span style="font-size:16px;color:#17233d">{{pageTitle}}查询&nbsp;&nbsp;</span>
        <Button type="primary" @click="searchPage">
          <Icon type="md-search"></Icon>查找
        </Button>
        <Button type="error" @click="resetPage">
          <Icon type="md-refresh"></Icon>清除
        </Button>
      </div>
      <Row :gutter="15">
        <Col span="3">
        <Input type="text" v-model="filter.creatorName" placeholder="创建人..."></Input>
        </Col>
        <Col span="3">
        <Input type="text" v-model="filter.surerName" placeholder="确认人..."></Input>
        </Col>
        <Col span="3" v-if="role==='admin'&&$route.name==='Plan'">
        <Select v-model="filter.buySellType" placeholder="类型..." clearable>
          <Option v-for="(value,index) in buySellType" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="3">
        <Select v-model="filter.level" placeholder="紧急程度..." clearable>
          <Option v-for="(value,index) in level" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="3">
        <Select v-model="filter.state" placeholder="状态..." clearable>
          <Option v-for="(value,index) in state" :key="index" :value="index">{{value}}</Option>
        </Select>
        </Col>
        <Col span="3">
        <DatePicker type="date" :value="filter.startTime" placeholder="开始日期..." style="width: 100%" @on-change="setFilterStartTime"></DatePicker>
        </Col>
        <Col span="3">
        <DatePicker type="date" :value="filter.endTime" placeholder="结束日期..." style="width: 100%" @on-change="setFilterEndTime"></DatePicker>
        </Col>
        <Col span="3">
        <Input type="text" v-model="filter.description" placeholder="描述..."></Input>
        </Col>
      </Row>
    </Card>
    <Card :bordered="false" :style="{marginTop:'20px'}">
      <div slot="title">
        <span style="font-size:16px;color:#17233d">{{pageTitle}}表格&nbsp;&nbsp;</span>
        <Button v-if="add" type="info" @click="saveNeedPlan(null)">
          <Icon type="md-add"></Icon>添加
        </Button>
      </div>
      <Table border stripe :columns="needPlanColumns" :data="pageResult.data" :loading="tableLoading" @on-sort-change="sortPage">
        <div slot="footer">
          <Page :total="pageResult.total" transfer show-total show-sizer show-elevator :page-size-opts="pageSizeOpts"
            :page-size="pageModel.length" style="float:right;margin-right:20px" @on-change="selectPage"
            @on-page-size-change="changeLength" />
        </div>
      </Table>
    </Card>
    <Modal v-model="needPlanModal" :title="needPlanTitle" :loading="modalLoading">
      <Form ref="needPlanForm" :model="needPlanData" :label-width="80" :rules="needPlanRules">
        <FormItem label="紧急程度" prop="level">
          <RadioGroup v-model="needPlanData.level">
            <Radio v-for="(value,index) in level" :label="index" :key="index">
              <Tag :color="levelColor[index]">{{value}}</Tag>
            </Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="开始时间" prop="startTime">
          <DatePicker type="date" :value="needPlanData.startTime" placeholder="开始日期..." style="width: 100%" @on-change="setDataStartTime"></DatePicker>
        </FormItem>
        <FormItem label="结束时间" prop="endTime">
          <DatePicker type="date" :value="needPlanData.endTime" placeholder="结束日期..." style="width: 100%" @on-change="setDataEndTime"></DatePicker>
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input v-model="needPlanData.description" placeholder="描述..." type="textarea" maxlength="200" show-word-limit></Input>
        </FormItem>
        <FormItem label="类型" prop="buySellType" v-if="role==='admin'&&$route.name==='Plan'">
          <Select v-model="needPlanData.buySellType" placeholder="类型...">
            <Option v-for="(value,index) in buySellType" :key="index" :value="index">{{value}}</Option>
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
        pageTitle:'',
        add: false,
        modalLoading: false,
        needPlanType: typeText.needPlanType,
        buySellType: typeText.buySellType,
        buySellTypeObj: {
          buyer: 0,
          producer: 1,
          seller: 2,
          admin: null,
          manager: null
        },
        level: typeText.level,
        levelColor: typeText.levelColor,
        state: typeText.needPlanState,
        stateColor: typeText.needPlanStateColor,
        needPlanModal: false,
        needPlanTitle: '',
        needPlanData: {
          id: null,
          creatorId: null,
          surerId: null,
          level: null,
          startTime: '',
          endTime: '',
          description: '',
          needPlanType: null,
          buySellType: null,
          state: null,
          syncTime: null
        },
        needPlanRules: {
          startTime: [{
            required: true,
            message: '（开始时间不能为空！）',
            trigger: 'blur'
          }],
          endTime: [{
            required: true,
            message: '（结束时间不能为空！）',
            trigger: 'blur'
          }],
          description: [{
            type: 'string',
            max: 200,
            message: '（描述长度不能超过200！）',
            trigger: 'blur'
          }]
        },
        ...tableDatas({
          creatorName: '',
          surerName: '',
          level: null,
          startTime: '',
          endTime: '',
          description: '',
          needPlanType: null,
          buySellType: null,
          state: null
        }, 'state'),
        needPlanColumns: [{
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
            title: '确认人',
            key: 'surerName',
            sortable: true,
            align: 'center',
            render: (h, params) => h('span', params.row.surerName || '无')
          },
          {
            title: '紧急度',
            key: 'level',
            sortable: true,
            align: 'center',
            render: (h, params) => h('Tag', {
              props: {
                color: this.levelColor[params.row.level]
              }
            }, this.level[params.row.level])
          },
          {
            title: '类型',
            key: 'buySellType',
            sortable: true,
            align: 'center',
            render: (h, params) => h('span', this.buySellType[params.row.buySellType])
          },
          {
            title: '开始',
            key: 'startTime',
            sortable: true,
            align: 'center',
          },
          {
            title: '结束',
            key: 'endTime',
            sortable: true,
            align: 'center',
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
                editAndDel = params.row.state === 0 && (self.role === 'admin' || params.row.creatorId === self.id);
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
                      self.saveNeedPlan(params.row);
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
          needPlanType = to.name === 'Plan' ? 1 : 0,
          buySellType = to.name === 'Plan' ? self.buySellTypeObj[self.role] : to.name === 'MatterNeed' ? 0 : 1;
        self.needPlanData.needPlanType = needPlanType;
        self.filter.needPlanType = needPlanType;
        self.filterEmpty.needPlanType = needPlanType;
        self.pageModel.filter.needPlanType = needPlanType;
        self.filter.buySellType = buySellType;
        self.filterEmpty.buySellType = buySellType;
        self.pageModel.filter.buySellType = buySellType;
        self.add = self.role === 'admin' || to.name === 'Plan' && self.role !== 'manager' ||
          (to.name === 'ProductNeed' && self.role === 'seller') ||
          (to.name === 'MatterNeed' && self.role === 'producer');
        self.pageTitle=(self.buySellType[buySellType]||'')+self.needPlanType[needPlanType];
        self.getPage();
      })
    },
    methods: {
      ...tableMethods('/api/needPlan/getNeedPlanPage'),
      setFilterStartTime(value) {
        let self = this;
        self.filter.startTime = value;
      },
      setFilterEndTime(value) {
        let self = this;
        self.filter.endTime = value;
      },
      setDataStartTime(value) {
        let self = this;
        self.needPlanData.startTime = value;
      },
      setDataEndTime(value) {
        let self = this;
        self.needPlanData.endTime = value;
      },
      saveNeedPlan(data) {
        let self = this,
          routeName = self.$route.name;
        self.$refs['needPlanForm'].resetFields();
        if (data === null) {
          self.needPlanTitle = '添加' + self.needPlanType[self.needPlanData.needPlanType];
          self.needPlanData.id = null;
          self.needPlanData.creatorId = self.id;
          self.needPlanData.surerId = -1;
          self.needPlanData.level = 0;
          self.needPlanData.startTime = null;
          self.needPlanData.endTime = null;
          self.needPlanData.description = null;
          self.needPlanData.state = 0;
          self.needPlanData.syncTime = null;
          switch (routeName) {
            case 'Plan':
              self.needPlanData.buySellType = self.role === 'admin' ? 0 : self.buySellTypeObj[self.role];
              break;
            case 'MatterNeed':
              self.needPlanData.buySellType = 0;
              break;
            case 'ProductNeed':
              self.needPlanData.buySellType = 1;
              break;
            default:
              break;
          }
        } else {
          self.needPlanTitle = '编辑' + self.needPlanType[self.needPlanData.needPlanType];
          self.needPlanData.id = data.id;
          self.needPlanData.creatorId = data.creatorId;
          self.needPlanData.surerId = data.surerId;
          self.needPlanData.level = data.level;
          self.needPlanData.startTime = data.startTime + '';
          self.needPlanData.endTime = data.endTime + '';
          self.needPlanData.description = data.description;
          self.needPlanData.buySellType = data.buySellType;
          self.needPlanData.state = data.state;
          self.needPlanData.syncTime = data.syncTime;
        }
        self.needPlanModal = true;
      },
      ok() {
        let self = this;
        self.modalLoading = true;
        self.$refs['needPlanForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/needPlan/saveNeedPlan',
              data: self.needPlanData
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
              self.needPlanModal = false;
              self.modalLoading = false;
            }).catch(err => {
              self.$Modal.error({
                title: '保存失败！',
                content: err.message
              });
              self.needPlanModal = false;
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
        self.needPlanModal = false;
      },
      showDetail(id) {
        let self = this;
        self.$router.push({
          path: self.needPlanData.needPlanType === 0 ? 'needDetail' : 'planDetail',
          query: {
            id: id
          }
        });
      },
    }
  }
</script>

<style scoped>

</style>
