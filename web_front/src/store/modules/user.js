import {
  LoginByPhone,
  GetInfo,
  ChangePassword
} from '@/api/login.js'

const user = {
  state: {
    id: null,
    name: '',
    password: '',
    eMail: '',
    phoneNum: '',
    role: '',
    img: ''
  },
  mutations: {
    setUser(state, user) {
      state.id = user.id || state.id;
      state.name = user.name || state.name;
      state.password = user.password || state.password;
      state.eMail = user.eMail || state.eMail;
      state.phoneNum = user.phoneNum || state.phoneNum;
      state.role = user.role || state.role;
      state.img = user.img || state.img;
    },
    setPassword(state, data) {
      state.password = data;
    }
  },
  actions: {
    loginByPhone({
      commit,
      state
    }, loginForm) {
      return new Promise((resolve, reject) => {
        LoginByPhone(loginForm.phoneNum.trim(), loginForm.password.trim())
          .then(res => {
            const data = res.data;
            if (data.code === 200) {
              commit('setUser', data.data.user);
              resolve(res);
            } else
              reject(res);
          })
          .catch(err => {
            reject(err);
          })
      });
    },
    logoutNative({
      commit,
      state
    }) {
      return new Promise((resolve, reject) => {
        try {
          localStorage.token = '';
          resolve();
        } catch (err) {
          reject(err);
        }
      });
    },
    changePassword({
      commit,
      state
    }, password) {
      return new Promise((resolve, reject) => {
        ChangePassword(password)
          .then(res => {
            const data = res.data;
            if (data.code === 200) {
              commit('setUser', data.data.user);
              resolve(res);
            } else
              reject(res);
          })
          .catch(err => {
            reject(err);
          })
      });
    },
    getInfo({
      commit,
      state
    }) {
      return new Promise((resolve, reject) => {
        GetInfo()
          .then(res => {
            const data = res.data;
            if (data.code === 200) {
              commit('setUser', data.data);
              resolve(res);
            } else
              reject(res);
          })
          .catch(err => {
            reject(err);
          })
      })
    }
  }
}
export default user;
