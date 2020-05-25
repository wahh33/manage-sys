import Vue from 'vue';
import ViewUI from 'view-design';
import 'view-design/dist/styles/iview.css';
import Router from 'vue-router';
import menuRouter from './menuRouter.js';
import store from '@/store';

Vue.use(ViewUI);
Vue.use(Router);

//iview
ViewUI.LoadingBar.config({
  color: '#87CEFA',
  height: 5
});

// 添加这下面一段代码，就可以解决报错
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
};

const router = new Router({
  mode: 'history',
  scrollBehavior: () => ({
    y: 0
  }),
  routes: [{
      name: 'Login',
      path: '/login',
      component: () => import('@/views/Login.vue')
    },
    {
      name: 'Layout',
      path: '/',
      component: () => import('@/components/Layout.vue'),
      children: menuRouter
    },
    {
      name: 'Default',
      path: '*',
      component: () => import('@/views/Login.vue')
    }
  ],
});

router.beforeEach((to, from, next) => {
  ViewUI.LoadingBar.start();
  if (to.name === 'Login') {
    next(); //不是return！相当于promis的resolve，resolve完会回来继续向下执行！
    return;
  }
  let token = localStorage.token,
    role = store.getters.role;
  if (!token) {
    if (from.name === 'Login') ViewUI.LoadingBar.finish();
    next('/login');
    return;
  }
  if (!role) {
    store.dispatch('getInfo').then(res => {
      role = store.getters.role;
      if (to.meta.roles.some(r => r === role)) {
        next();
      } else {
        if (from.name === 'Login') ViewUI.LoadingBar.finish();
        next('/login');
      }
    }).catch(err => {
      ViewUI.Message.error('获取用户信息出错!' + (err.message || err.data.message));
      if (from.name === 'Login') ViewUI.LoadingBar.finish();
      next('/login');
    });
  } else {
    if (to.meta.roles.some(r => r === role)) {
      next();
    } else {
      if (from.name === 'Login') ViewUI.LoadingBar.finish();
      next('/login');
    }
  }
});

router.afterEach((to, from) => {
  let content=document.getElementsByClassName('content')[0];
  if(content){
    content.scrollTop=0;
  }
  ViewUI.LoadingBar.finish();
});

export default router;
