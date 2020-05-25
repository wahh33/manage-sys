<template>
  <div id="app">
    <transition name="slide-fade">
      <router-view></router-view>
    </transition>
    <Spin fix v-if="spinShow">
      <Icon class="spin-icon-load" type="ios-loading" size=30></Icon>
      <div>Loading</div>
    </Spin>
  </div>
</template>

<script>
  export default {
    name: 'App',
    data() {
      return {
        spinShow: true
      }
    },
    mounted() {
      let self = this;
      setTimeout(() => {
        self.spinShow = false
      }, 500);
    }
  }
</script>

<style>
  * {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
  }

  html,
  body {
    width: 100vw;
    height: 100vh;
    overflow: hidden;
  }

  :root {
    --size: 60px;
  }

  #app {
    position: relative;
    width: 100%;
    height: 100%;
  }

  ::-webkit-scrollbar {
    background-color: #f1f1f1;
    border-radius: 12px;
    width: 12px;
    height: 12px;
  }

  ::-webkit-scrollbar-thumb {
    border-radius: 12px;
    background: linear-gradient(to bottom, #3498db, #9b59b6);
  }

  ::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(to top, #3498db, #9b59b6);
  }

  .spin-circle {
    width: var(--size);
    height: var(--size);
    border-radius: 50%;
    border-top: calc(var(--size) / 10) solid red;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: calc(var(--size) / 5);
  }

  .spin-circle::before {
    content: '';
    position: absolute;
    top: calc(-1 * var(--size) / 10);
    left: 0;
    width: var(--size);
    height: var(--size);
    border-radius: 50%;
    border-top: calc(var(--size) / 10) solid orange;
    transform: rotate(120deg);
  }

  .spin-circle::after {
    content: '';
    position: absolute;
    top: calc(-1 * var(--size) / 10);
    left: 0;
    width: var(--size);
    height: var(--size);
    border-radius: 50%;
    border-top: calc(var(--size) / 10) solid blue;
    transform: rotate(240deg);
  }

  .spin-icon-load {
    animation: spin-icon-ani 1s linear infinite;
  }

  .spin-icon-load-reverse {
    animation: spin-icon-ani 1s reverse linear infinite;
  }

  @keyframes spin-icon-ani {
    from {
      transform: rotate(0deg);
    }

    50% {
      transform: rotate(180deg);
    }

    to {
      transform: rotate(360deg);
    }
  }

  /* 渐入过渡 */
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity .5s;
  }

  /* .fade-leave-active below version 2.1.8 */
  .fade-enter,
  .fade-leave-to {
    opacity: 0;
  }

  /* 滑动过渡 */
  .slide-fade-enter-active {
    transition: all .3s ease;
  }

  .slide-fade-leave-active {
    transition: all .8s cubic-bezier(1.0, 0.5, 0.8, 1.0);
  }

  /* .slide-fade-leave-active for below version 2.1.8 */
  .slide-fade-enter,
  .slide-fade-leave-to {
    transform: translateX(10px);
    opacity: 0;
  }

  /* 伸缩过渡 */
  .bounce-enter-active {
    animation: bounce-in .5s;
  }

  .bounce-leave-active {
    animation: bounce-in .5s reverse;
  }

  @keyframes bounce-in {
    0% {
      transform: scale(0);
    }

    50% {
      transform: scale(1.5);
    }

    100% {
      transform: scale(1);
    }
  }

  .state-button-group {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
  }

  .state-button-group button:not(:last-child) {
    margin-right: 10px;
  }

  .no-data {
    position: relative;
    margin-top: 100px;
    padding: 20px;
    top: 0;
    left: 50%;
    transform: translate(-50%);
    width: 500px;
    height: 300px;
    background: white;
    border-radius: 4px;
    box-shadow: 0 0 4px rgba(0, 0, 0, 0.15);
  }

  .no-data:hover {
    box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  }

  .no-data i {
    display: inline-block;
    width: 150px;
    height: 150px;
    margin: auto;
    font-size: 150px;
    font-weight: bold;
    color: red;
    display: block;
    transition: all 0.8s ease;
  }

  .no-data:hover i {
    transform: scale(1.1) rotate(360deg);
  }

  .no-data>.no-data-text {
    margin-top: 20px;
    text-align: center;
    font-size: 30px;
    font-weight: bold;
    color: #515a6e;
    transition: all 0.8s ease;
  }

  .no-data:hover>.no-data-text {
    transform: scale(1.1);
  }

  .refresh {
    font-size: 20px !important;
    transition: all 0.4s ease;
  }

  .refresh:hover {
    transform: scale(1.1) rotate(180deg);
    filter: brightness(120%);
  }

  .footer{
    position: absolute;
    bottom: 0;
    padding: 8px;
    text-align: center;
    background: linear-gradient(to right top, #3498db, #9b59b6);
    color: white;
    width: 100%;
    opacity: 0;
    z-index: 3;
    transition: all 0.4s ease;
  }
</style>
