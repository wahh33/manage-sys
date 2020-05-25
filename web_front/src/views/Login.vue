<template>
  <div class="bg">
    <div class="logo-title">
      <div class="logo"></div>
      <span class="title">企业管理系统</span>
    </div>
    <div class="login-card">
      <Form ref="user" :model="formUser" :rules="ruleUser">
        <FormItem prop="phoneNum">
          <Input type="text" v-model="formUser.phoneNum" placeholder="请输入手机名..." size="large"
            @on-enter="$refs['passwordInput'].focus()">
          <Icon type="ios-person-outline" slot="prefix" size="25"></Icon>
          </Input>
        </FormItem>
        <FormItem prop="password">
          <Input ref="passwordInput" :type="passwordInputType" v-model="formUser.password" placeholder="请输入密码..." size="large"
            @on-enter="submit('user')">
          <Icon type="ios-lock-outline" slot="prefix" size="25"></Icon>
          <Icon :type="passwordIcon" @click.native="changePWDInputType" slot="suffix" size="25" style="cursor: pointer;"></Icon>
          </Input>
        </FormItem>
        <FormItem>
          <Button long type="primary" @click="submit('user')" :loading="loginLoading" size="large">登录</Button>
        </FormItem>
      </Form>
    </div>
    <footer ref="footer" class="footer" @mouseover="footerShow" @mouseleave="footerLeave">©{{new Date().getFullYear()}}&nbsp;企业管理系统&nbsp;&nbsp;Designed&nbsp;By&nbsp;湛梓健</footer>
  </div>
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
  export default {
    data() {
      return {
        passwordInputType:'password',
        passwordIcon:'ios-eye-outline',
        loginLoading: false,
        formUser: {
          name: "",
          password: "",
          role: "",
          phoneNum: "",
          eMail: ""
        },
        ruleUser: {
          phoneNum: [{
              required: true,
              message: '（手机号不能为空！）',
              trigger: 'blur'
            },
            {
              validator: validatePhoneNum,
              trigger: 'blur'
            }
          ],
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
        },
      }
    },
    methods: {
      ...mapActions(["loginByPhone"]),
      submit(name) {
        var self = this;
        self.$refs[name].validate(valid => {
          if (valid) {
            self.login();
          } else {
            self.$Message.error('登录失败!');
          }
        })
      },
      login() {
        var self = this;
        self.loginLoading = true;
        self.loginByPhone({
          phoneNum: self.formUser.phoneNum.toLowerCase(),
          password: self.formUser.password
        }).then(res => {
          self.$Message.success(res.data.message);
          self.$router.push({name: 'Home'});
          self.loginLoading = false;
        }).catch(err => {
          self.$Message.error(err.message||err.data.message);
          self.loginLoading = false;
        });
      },
      changePWDInputType(){
        let self=this;
        switch(self.passwordInputType){
          case 'password':
            self.passwordInputType='text';
            self.passwordIcon='ios-eye-off-outline';
            break;
          case 'text':
            self.passwordInputType='password';
            self.passwordIcon='ios-eye-outline';
            break;
          default:
            break;
        }
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
  .bg {
    width: 100%;
    height: 100%;
    background-image: url(../assets/bg.svg);
    background-position: center;
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-color: #f5f7f9;
    display: flex;
    flex-direction: column;
    align-items: center;
    overflow: auto;
  }

  .logo-title {
    padding-top: 115px;
    padding-bottom: 20px;
    width: 30%;
    height: 250px;
    display: flex;
    justify-content: center;
  }

  .login-card {
    width: 30%;
  }

  .logo {
    width: 110px;
    height: 110px;
    background-image: url(../assets/logo.png);
    background-position: center;
    background-size: 100% 100%;
    background-repeat: no-repeat;
  }

  .title {
    padding-top: 33px;
    font-size: xx-large;
    font-weight: bolder;
    color: #808695;
  }
</style>
