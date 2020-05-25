import Vue from 'vue'
import App from './App';
import http from './api';
import router from './router';
import store from './store';

Vue.config.productionTip = false;
Vue.prototype.$http = http;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
