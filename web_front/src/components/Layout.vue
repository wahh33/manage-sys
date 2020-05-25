<template>
  <Layout class="layout">
    <Sider class="side" ref="side" hide-trigger collapsible :collapsed-width="80" v-model="isCollapsed">
      <z-menu :zList="menus" :isCollapsed="isCollapsed" :accordion="isAccordion" homeName="Home">
        <div id="logo-text" slot="z-logo"><span>企业管理系统</span></div>
      </z-menu>
    </Sider>
    <Layout style="position: relative;">
      <Header class="header">
        <Icon @click.native="isCollapsed=!isCollapsed" :class="rotateIcon" type="md-menu" size="30"></Icon>
        <Icon v-if="!isFullScreen" type="md-expand" size="30" class="warn-tag" title="全屏" @click.native="fullScreenFun"></Icon>
        <Icon v-else type="md-contract" size="30" class="warn-tag" title="退出全屏" @click.native="fullScreenFun"></Icon>
        <Poptip padding="0" placement="bottom" transfer trigger="hover" v-if="['buyer','producer','seller'].some(o=>o===role)">
          <Badge :count="warnPageResult.total" class="warn-tag">
            <Icon type="ios-notifications-outline" size="30"></Icon>
          </Badge>
          <div slot="content" style="width:267px;">
            <List>
              <ListItem v-if="role==='producer'">
                <ButtonGroup style="margin:auto">
                  <Button :type="isLow?'primary':'default'" @click="swichWarn(true)">
                    低库存
                  </Button>
                  <Button :type="isLow?'default':'primary'" @click="swichWarn(false)">
                    高库存
                  </Button>
                </ButtonGroup>
              </ListItem>
              <ListItem v-if="warnPageResult.data.length===0">
                <span class="warn-none">没有库存警告</span>
              </ListItem>
              <ListItem style="padding:0" v-if="warnPageResult.data.length>0">
                <List class="warn-list">
                  <ListItem class="warn-list-item" v-for="(item,index) in warnPageResult.data" :key="index">
                    <router-link :to="{name:'GoodsSelect'}" class="warn-type" :style="{backgroundColor:item.type===0?'#3498db':item.type===1?'#9b59b6':role==='seller'?'#9b59b6':item.state===1?'#9b59b6':'#3498db'}">
                      {{item.type===0?'物料':item.type===1?'产品':role==='seller'?'产品':item.state===1?'产品':'物料'}}
                    </router-link>
                    <div class="warn-text">
                      <p>
                        <span style="font-weight:bold">
                          {{item.name}}
                        </span>
                        <span style="float:right" :style="{color:item.state===1?'#70a1ff':'#ff6b81'}">
                          {{item.state===1?'低库存':'高库存'}}
                        </span>
                      </p>
                      <p>
                        <span>
                          {{'现：'+item.count}}
                        </span>
                        <span style="float:right">
                          {{item.state===1?('低：'+item.low):('高：'+item.high)}}
                        </span>
                      </p>
                    </div>
                  </ListItem>
                </List>
              </ListItem>
              <ListItem v-if="warnPageResult.data.length<warnPageResult.total">
                <a class="warn-more" href="javascript:void(0)" @click="getWarn('append')">
                  <Icon type="md-add"></Icon>更多
                </a>
              </ListItem>
            </List>
          </div>
        </Poptip>
        <Button type="text" class="info-button" size="large" @click="isDrawered=!isDrawered">
          {{name}}&nbsp;<Icon type="md-return-left"></Icon>
        </Button>
        <Drawer transfer v-model="isDrawered" width="25" :styles="drawerStyles" @on-visible-change="resetDrawer">
          <Divider>
            <div class="rotate-user-icon">
              <Icon class="rotate-user-icon-button" type="ios-contact" size="50" />
              <span class="rotate-user-icon-button rotate-user-icon-button-save" v-if="isEditUser" @click="saveUser">保存</span>
              <span class="rotate-user-icon-button rotate-user-icon-button-edit" v-else @click="editUser">编辑</span>
            </div>
          </Divider>

          <Form ref="userForm" :model="userData" :rules="userRules" :label-width="80">
            <FormItem label="姓名：" prop="name">
              <Input v-if="isEditUser" v-model="userData.name"></Input>
              <span v-else>{{name}}</span>
            </FormItem>
            <FormItem label="电话：" prop="phoneNum">
              <Input v-if="isEditUser" v-model="userData.phoneNum"></Input>
              <span v-else>{{phoneNum}}</span>
            </FormItem>
            <FormItem label="邮箱：" prop="eMail">
              <Input v-if="isEditUser" v-model="userData.eMail"></Input>
              <span v-else>{{eMail}}</span>
            </FormItem>
            <FormItem label="角色：" prop="role">
              <Select v-if="isEditUser" v-model="userData.role" :disabled="role!=='admin'">
                <Option v-for="(value,key,index) in allRolesObj" :key="index" :value="key">{{value}}</Option>
              </Select>
              <span v-else>{{allRolesObj[role]}}</span>
            </FormItem>
          </Form>

          <Divider><span style="font-size:larger">密码修改</span></Divider>

          <Form ref="password" :model="formPassword" :rules="rulePassword">
            <FormItem prop="oldPassword">
              <span>密码：</span>
              <Input @on-enter="$refs['newPSW'].focus()" v-model="formPassword.oldPassword" type="password" password
                placeholder="请输入密码..."></Input>
            </FormItem>
            <FormItem prop="newPassword">
              <span>新密码：</span>
              <Input @on-enter="$refs['againPSW'].focus()" ref="newPSW" v-model="formPassword.newPassword" type="password"
                password placeholder="请输入新密码..."></Input>
            </FormItem>
            <FormItem prop="checkPassword">
              <span>再输一次：</span>
              <Input @on-enter="savePassword" ref="againPSW" v-model="formPassword.checkPassword" type="password"
                password placeholder="请再次输入新密码..."></Input>
            </FormItem>
            <FormItem>
              <Button long type="success" @click="savePassword" :loading="passwordLoading">
                保存
              </Button>
            </FormItem>
            <FormItem>
              <Button long type="error" @click="logout" :loading="logoutLoading">
                登出
              </Button>
            </FormItem>
          </Form>
        </Drawer>
      </Header>
      <z-scrollmenu style="margin-top: 64px;"></z-scrollmenu>
      <Content class="content">
        <keep-alive>
          <router-view></router-view>
        </keep-alive>
      </Content>
      <footer ref="footer" class="footer" @mouseover="footerShow" @mouseleave="footerLeave">©{{new Date().getFullYear()}}&nbsp;企业管理系统&nbsp;&nbsp;Designed&nbsp;By&nbsp;湛梓健</footer>
    </Layout>
  </Layout>
</template>

<script>
  import {
    validatePassword,
    validatePhoneNum
  } from '@/utils/validator.js';
  import {
    mapGetters,
    mapMutations,
    mapActions
  } from 'vuex';
  import zMenu from './z-menu.vue';
  import zScrollmenu from './z-scrollmenu.vue';
  import {
    allRolesObj
  } from '@/router/menuRouter.js'
  export default {
    components: {
      zMenu,
      zScrollmenu
    },
    data() {
      const validatePasswordCheck = (rule, value, callback) => {
        if (value !== this.formPassword.newPassword) {
          callback(new Error('(两个密码不相同！)'));
        } else {
          callback();
        }
      };
      return {
        isEditUser: false,
        userData: {
          id: null,
          name: '',
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
        isFullScreen: document.isFullScreen || document.mozIsFullScreen || document.webkitIsFullScreen,
        warnPageResult: {
          total: 0,
          data: [],
        },
        warnPageModel: {
          filter: {
            isWarn: true,
            type: null,
            _type: null,
            state: null,
          },
          start: 1,
          length: 3,
        },
        isLow: true,
        allRolesObj: allRolesObj,
        isCollapsed: false,
        isAccordion: true,
        isDrawered: false,
        logoutLoading: false,
        passwordLoading: false,
        drawerStyles: {
          padding: "0 20px",
          overflow: "auto",
        },
        formPassword: {
          oldPassword: '',
          newPassword: '',
          checkPassword: '',
        },
        rulePassword: {
          oldPassword: [{
            validator: validatePassword,
            trigger: 'blur'
          }],
          newPassword: [{
            validator: validatePassword,
            trigger: 'blur'
          }],
          checkPassword: [{
              validator: validatePassword,
              trigger: 'blur'
            },
            {
              validator: validatePasswordCheck,
              trigger: 'blur'
            }
          ]
        }
      }
    },
    computed: {
      rotateIcon() {
        return [
          'icon',
          this.isCollapsed ? 'rotate-icon' : ''
        ];
      },
      ...mapGetters(['id', 'menus', 'role', 'eMail', 'phoneNum', 'name']),
    },
    created() {
      let self = this;
      self.getWarn('init');
      window.onresize = function() {
        self.isFullScreen = document.isFullScreen || document.mozIsFullScreen || document.webkitIsFullScreen;
      };
    },
    methods: {
      ...mapActions(['changePassword', 'logoutNative']),
      ...mapMutations(['setUser']),
      logout() {
        let self = this;
        self.logoutLoading = true;
        self.logoutNative().then(res => {
          self.$router.push({
            name: 'Login'
          });
          self.logoutLoading = false;
        }).catch(err => {
          self.$Message.error('登出出错!' + (err.message || err.data.message));
          self.logoutLoading = false;
        });
      },
      savePassword() {
        let self = this;
        self.passwordLoading = true;
        self.$refs['password'].validate(valid => {
          if (valid) {
            self.changePassword(self.formPassword.newPassword).then(res => {
              self.$Message.success('修改密码成功!');
              self.passwordLoading = false;
              self.isDrawered = false;
            }).catch(err => {
              self.$Message.error('修改密码失败!' + (err.message || err.data.message));
              self.passwordLoading = false;
            });
          } else {
            self.$Message.error('修改密码失败!');
            self.passwordLoading = false;
          }
        });
      },
      resetDrawer() {
        let self = this;
        self.$refs['password'].resetFields();
        self.$refs['userForm'].resetFields();
        self.isEditUser = false;
      },
      swichWarn(flag) {
        let self = this;
        self.isLow = flag;
        self.getWarn();
      },
      getWarn(type = null) {
        let self = this;
        switch (self.role) {
          case 'buyer':
            self.warnPageModel.filter.type = 0;
            self.warnPageModel.filter._type = null;
            self.warnPageModel.filter.state = 1;
            break;
          case 'producer':
            self.warnPageModel.filter.type = null;
            self.warnPageModel.filter._type = self.isLow ? 0 : 1;
            self.warnPageModel.filter.state = self.isLow ? 1 : 2;
            break;
          case 'seller':
            self.warnPageModel.filter.type = null;
            self.warnPageModel.filter._type = 0;
            self.warnPageModel.filter.state = 2;
            break;
          default:
            return;
        }
        if (type === 'append') {
          self.warnPageModel.start += 1;
        } else {
          self.warnPageModel.start = 1;
        }
        self.$http({
          url: '/api/goods/getGoodsPage',
          method: 'post',
          data: self.warnPageModel,
        }).then(res => {
          if (res.data.code === 200) {
            self.warnPageResult.total = res.data.data.total;
            if (type === 'init' && res.data.data.data.length === 0) {
              self.swichWarn(false);
            } else if (type === 'append') {
              self.warnPageResult.data = self.warnPageResult.data.concat(res.data.data.data);
            } else {
              self.warnPageResult.data = res.data.data.data;
            }
          } else self.$Message.error('获取库存警告失败！' + res.data.message);
        }).catch(err => {
          self.$Message.error('获取库存警告失败！' + err.message);
        });
      },
      fullScreenFun() {
        let self = this,
          doc,
          fun;
        if (!self.isFullScreen) {
          doc = document.documentElement;
          fun = doc.requestFullScreen || doc.webkitRequestFullScreen || doc.mozRequestFullScreen || doc.msRequestFullscreen;
        } else {
          doc = document;
          fun = doc.exitFullscreen || doc.mozCancelFullScreen || doc.webkitCancelFullScreen || doc.msExitFullscreen;
        }
        if (fun) {
          fun.call(doc);
        }
        return;
      },
      editUser() {
        let self = this;
        self.userData.id = self.id;
        self.userData.name = self.name;
        self.userData.phoneNum = self.phoneNum;
        self.userData.eMail = self.eMail;
        self.userData.role = self.role;
        self.isEditUser = true;
      },
      saveUser() {
        let self = this;
        self.$refs['userForm'].validate((valid) => {
          if (valid) {
            self.$http({
              method: 'post',
              url: '/api/user/saveUser',
              data: self.userData
            }).then(res => {
              if (res.data.code === 200) {
                self.setUser(self.userData);
                self.isEditUser = false;
                self.$Message.success('保存成功！' + res.data.message);
              } else {
                self.$Message.error('保存失败！' + res.data.message);
              }
            }).catch(err => {
              self.$Message.error('保存失败！' + err.message);
            });
          } else {
            self.$Message.error('表单验证出错!');
          }
        });
      },
      footerShow(){
        let self=this;
        if(self.$refs['footer']){
          self.$refs['footer'].style.opacity=1;
        }
      },
      footerLeave(){
        let self=this;
        if(self.$refs['footer']){
          self.$refs['footer'].style.opacity=0;
        }
      },
    },
  }
</script>

<style scoped>
  .layout {
    width: 100%;
    height: 100%;
    background-color: #f1f1f1;
  }

  .side {
    height: 100%;
    background-color: white;
  }

  .content {
    position: relative;
    padding: 0 20px;
    height: 100%;
    overflow: auto;
  }

  .content>div,
  .content>section {
    padding-bottom: 40px;
  }

  .header {
    background: linear-gradient(to right bottom, #3498db, #9b59b6);
    display: flex;
    justify-content: flex-end;
    align-items: center;
    color: white;
    padding: 0 15px;
    position: absolute;
    width: 100%;
    z-index: 3;
    transition: all 0.2s ease-in-out;
  }

  .icon {
    margin-right: auto;
    cursor: pointer;
    transition: all 0.2s ease-in-out;
  }

  .icon:hover {
    transform: scale(1.1);
    filter: brightness(120%);
  }

  .rotate-icon {
    transform: rotate(-90deg);
  }

  .rotate-icon:hover {
    transform: rotate(-90deg) scale(1.1);
  }

  .info-button {
    font-size: large;
    font-weight: bold;
    color: #f0faff;
  }

  .info-button:hover {
    background-color: #f0faff;
    color: #9b59b6;
  }

  .ivu-form-item-content span {
    font-size: large;
  }

  .warn-tag {
    color: white;
    line-height: 30px;
    margin: 20px;
    cursor: pointer;
    transition: all 0.2s ease-in-out;
  }

  .warn-tag:hover {
    transform: scale(1.1);
    filter: brightness(120%);
  }

  .warn-list {
    width: 100%;
    max-height: 195px;
    overflow: auto;
  }

  .warn-list-item {
    padding-left: 12px;
    padding-right: 12px;
  }

  .warn-type {
    line-height: 30px;
    font-size: large;
    font-weight: bold;
    color: white;
    border-radius: 10px;
    padding: 5px;
  }

  .warn-text {
    display: inline-block;
    height: 40px;
    color: black;
    width: 180px;
    line-height: 20px;
    padding-left: 12px;
  }

  .warn-none,
  .warn-more {
    margin: auto;
    color: #8C8C8C;
    font-size: medium;
    font-weight: bold;
    line-height: 20px;
    transition: all 0.2s ease-in-out;
  }

  .warn-more:hover {
    color: #27ae60;
    transform: scale(1.1);
  }

  #logo-text {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    white-space: nowrap;
    overflow-x: hidden;
    font-size: x-large;
    color: white;
    font-weight: bold;
    font-family: "SimHei";
  }

  .rotate-user-icon {
    position: relative;
    width: 50px;
    height: 50px;
    line-height: 50px;
    text-align: center;
    color: white;
    transform-style: preserve-3d;
    transition: all .4s ease-in-out;
    cursor: pointer;
  }

  .rotate-user-icon:hover {
    transform: rotateY(180deg);
  }

  .rotate-user-icon-button {
    position: absolute;
    display: inline-block;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    top: 0;
    left: 0;
    background-color: #9b59b6;
  }

  .rotate-user-icon-button-edit {
    background-color: #3498db;
    transform: rotateY(-180deg);
  }

  .rotate-user-icon-button-save {
    background-color: #2ecc71;
    transform: rotateY(-180deg);
  }
</style>
