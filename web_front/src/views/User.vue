<template>
  <div>
    <Card :bordered="false">
      <p slot="title">人员查询</p>
      <Row :gutter="15">
        <Col span="5">
        <Input type="text" v-model="filter.name" placeholder="姓名..."></Input>
        </Col>
        <Col span="5">
        <Select v-model="filter.role" placeholder="角色..." clearable>
          <Option v-for="(value,key,index) in allRoles" :key="index" :value="key">{{value}}</Option>
        </Select>
        </Col>
        <Col span="5">
        <Input type="text" v-model="filter.phoneNum" placeholder="手机..."></Input>
        </Col>
        <Col span="5">
        <Input type="text" v-model="filter.eMail" placeholder="邮箱..."></Input>
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
        <span style="font-size:16px;color:#17233d">人员表格&nbsp;&nbsp;</span>
        <Button type="info" @click="saveUser(null)" v-if="role==='admin'">
          <Icon type="md-add"></Icon>添加
        </Button>
      </div>
      <Table border stripe :columns="userColumns" :data="pageResult.data" :loading="tableLoading" @on-sort-change="sortPage">
        <div slot="footer">
          <Page :total="pageResult.total" transfer show-total show-sizer show-elevator :page-size-opts="pageSizeOpts"
            :page-size="pageModel.length" style="float:right;margin-right:20px" @on-change="selectPage"
            @on-page-size-change="changeLength" />
        </div>
      </Table>
    </Card>
    <Modal v-model="userModal" :title="userTitle" :loading="modalLoading">
      <Form ref="userForm" :model="userData" :label-width="80" :rules="userRules">
        <FormItem label="姓名" prop="name">
          <Input v-model="userData.name" placeholder="姓名..."></Input>
        </FormItem>
        <FormItem label="密码" prop="password">
          <Input v-model="userData.password" placeholder="密码..."></Input>
        </FormItem>
        <FormItem label="角色" prop="role">
          <Select v-model="userData.role">
            <Option v-for="(value,key,index) in allRoles" :key="index" :value="key">{{value}}</Option>
          </Select>
        </FormItem>
        <FormItem label="手机" prop="phoneNum">
          <Input v-model="userData.phoneNum" placeholder="手机..."></Input>
        </FormItem>
        <FormItem label="邮箱" prop="eMail">
          <Input v-model="userData.eMail" placeholder="邮箱..."></Input>
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
    allRolesObj
  } from '@/router/menuRouter.js';
  import {
    mapGetters
  } from 'vuex';
  import {
    validatePassword,
    validatePhoneNum
  } from '@/utils/validator.js';
  export default {
    data() {
      return {
        modalLoading: false,
        userModal: false,
        userTitle: '',
        userData: {
          id: null,
          name: '',
          password: '',
          role: '',
          phoneNum: '',
          eMail: ''
        },
        userRules: {
          name: [{
              required: true,
              message: '（姓名不能为空！）',
              trigger: 'blur'
            },
            {
              type: 'string',
              max: 50,
              message: '（姓名长度不能超过50！）',
              trigger: 'blur'
            }
          ],
          role: [{
            required: true,
            message: '（角色不能为空！）',
            trigger: 'blur'
          }],
          password: [{
              required: true,
              message: '（密码不能为空！）',
              trigger: 'blur'
            },
            {
              validator: validatePassword,
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
        },
        allRoles: { ...allRolesObj
        },
        ...tableDatas({
          name: '',
          role: '',
          phoneNum: '',
          eMail: ''
        }, 'role'),
        userColumns: [],
        userColumnsArr: [{
            title: '序号',
            key: 'id',
            sortable: true,
            align: 'center',
          },
          {
            title: '姓名',
            key: 'name',
            sortable: true,
            align: 'center',
          },
          {
            title: '密码',
            key: 'password',
            sortable: true,
            align: 'center'
          },
          {
            title: '角色',
            key: 'role',
            sortable: true,
            align: 'center',
            sortType: 'asc',
            render: (h, params) => h('span', this.allRoles[params.row.role])
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
            align: 'center'
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
                      this.saveUser(params.row);
                    }
                  }
                }, '编辑'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small',
                  },
                  on: {
                    click: () => {
                      this.deleteUser(params.row.id);
                    }
                  }
                }, '删除')
              ])
            }
          }
        ]
      }
    },
    computed: {
      ...mapGetters(['role'])
    },
    created() {
      let self = this;
      self.userColumns = [...self.userColumnsArr.filter(o => self.role === 'admin' ? true : o.key !== 'operate' && o.key !==
        'password')];
    },
    activated() {
      let self = this;
      self.getPage();
    },
    methods: {
      ...tableMethods('/api/user/getUserPage'),
      saveUser(data) {
        let self = this;
        self.$refs['userForm'].resetFields();
        if (data === null) {
          self.userTitle = '添加人员';
          self.userData.id = null;
          self.userData.role = self.role;
        } else {
          self.userTitle = '编辑人员';
          self.userData.id = data.id;
          self.userData.name = data.name;
          self.userData.password = data.password;
          self.userData.phoneNum = data.phoneNum;
          self.userData.eMail = data.eMail;
          self.userData.role = data.role;
        }
        self.userModal = true;
      },
      deleteUser(id) {
        let self = this;
        self.$Modal.confirm({
          title: '确认删除？',
          loading: true,
          content: '删除后将不能恢复！',
          onOk: () => {
            self.$http({
              method: 'post',
              url: '/api/user/deleteUser',
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
        self.$refs['userForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/user/saveUser',
              data: self.userData
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
              self.userModal = false;
              self.modalLoading = false;
            }).catch(err => {
              self.$Modal.error({
                title: '保存失败！',
                content: err.message
              });
              self.userModal = false;
              self.modalLoading = false;
            });
          } else {
            self.$Message.error('表单验证出错!');
            self.modalLoading = false;
          }
        });
      },
      cancel() {
        let self = this;
        self.userModal = false;
      },
    }
  }
</script>

<style scoped>

</style>
