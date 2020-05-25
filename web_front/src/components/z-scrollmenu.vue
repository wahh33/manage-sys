<template>
  <div id="tag-top" @mousewheel.prevent="tagWheelScroll">
    <Icon class="tag-both tag-left" type="md-arrow-dropleft" size="36" @mousedown.native="tagScroll('left')"
      @mouseup.native="tagScroll('left',true)" @mouseleave.native="tagScroll('left',true)"/>
    <Dropdown transfer class="tag-right">
      <Icon class="tag-both tag-down" type="md-arrow-dropdown" size="36" />
      <DropdownMenu slot="list">
        <DropdownItem @click.native="tagArrClose('left')">
          <Icon type="md-arrow-back" />&nbsp;关闭左侧</DropdownItem>
        <DropdownItem @click.native="tagArrClose('right')">
          <Icon type="md-arrow-forward" />&nbsp;关闭右侧</DropdownItem>
        <DropdownItem @click.native="tagArrClose('other')">
          <Icon type="md-close" />&nbsp;关闭两侧</DropdownItem>
      </DropdownMenu>
    </Dropdown>
    <Icon class="tag-both tag-right" type="md-arrow-dropright" size="36" @mousedown.native="tagScroll('right')"
      @mouseup.native="tagScroll('right',true)" @mouseleave.native="tagScroll('right',true)"/>
    <div id="tag-inside">
      <transition name="fade" v-for="(item,key) in list" :key="key">
        <Tag type="dot" checkable closable class="tag-item" :color="item[checkedKey]?'primary':'default'"
          :name="key" @on-close="tagClose" @on-change="tagChange">
          {{item[textKey]}}
        </Tag>
      </transition>
    </div>
  </div>
</template>

<script>
  export default {
    name: "zScrollmenu",
    props: [],
    data() {
      return {
        list: [],
        urlKey: 'url',
        textKey: 'text',
        checkedKey: 'checked',
        checkedIndex: -1,
      }
    },
    computed: {
      route() {
        let self = this;
        return {
          url: self.$route.fullPath,
          text: self.$route.meta.title + (self.$route.query.id || ''),
        };
      }
    },
    watch: {
      route() {
        let self = this,
          exist = false;
        self.list.forEach((value, index) => {
          if (value[self.urlKey] === self.route.url) {
            value[self.checkedKey] = true;
            self.checkedIndex = index;
            exist = true;
          } else {
            value[self.checkedKey] = false;
          }
        });
        if (!exist) {
          let appendTag = {};
          appendTag[self.urlKey] = self.route.url;
          appendTag[self.textKey] = self.route.text;
          appendTag[self.checkedKey] = true;
          self.list.push(appendTag);
          self.checkedIndex = self.list.length - 1;
          self.tagScroll('right', false, 13);
        }
      }
    },
    created() {
      let self = this,
        appendTag = {};
      appendTag[self.urlKey] = self.route.url;
      appendTag[self.textKey] = self.route.text;
      appendTag[self.checkedKey] = true;
      if (self.list.length === 0) {
        self.list.push(appendTag);
        self.checkedIndex = 0;
      }
    },
    methods: {
      tagClose(event, name) {
        let self = this;
        if (self.list.length > 1) {
          if (self.list[name][self.checkedKey]) {
            if (name === 0) {
              self.tagChange(null, name + 1);
            } else {
              self.tagChange(null, name - 1);
            }
          }
          self.list.splice(name, 1);
          self.tagScroll('left', false, 13);
        }
      },
      tagChange(checked, name) {
        let self = this;
        self.$router.push(self.list[name][self.urlKey]);
      },
      tagScroll: function() {
        let timer = null;
        return function(type, isClose = false, totalCount = null) {
          let self = this,
            tagInside = document.getElementById('tag-inside'),
            count = 0;
          if (isClose) {
            clearInterval(timer);
            timer = null;
          } else {
            let newTimer = setInterval(() => {
              count++;
              tagInside.scrollLeft += type === 'left' ? -10 : 10;
              if (totalCount && count === totalCount) {
                clearInterval(newTimer);
              };
            }, 30);
            if (!totalCount) timer = newTimer;
          }
        }
      }(),
      tagWheelScroll(e) {
        let self = this,
          scroll = e.wheelDelta || e.detail;
        if (scroll > 0) {
          self.tagScroll('left', false, 5);
        } else {
          self.tagScroll('right', false, 5);
        }
      },
      tagArrClose(type) {
        let self = this;
        if (type === "left") {
          self.list.splice(0,self.checkedIndex);
          self.checkedIndex = 0;
        } else if (type === 'right') {
          self.list.splice(self.checkedIndex+1,self.list.length-1-self.checkedIndex);
        } else if (type === 'other') {
          self.list.splice(self.checkedIndex+1,self.list.length-1-self.checkedIndex);
          self.list.splice(0,self.checkedIndex);
          self.checkedIndex = 0;
        }
      },
    }
  }
</script>

<style scoped>
  #tag-top {
    background-color: #f5f7f9;
    padding: 5px;
  }

  .tag-both,
  .tag-item {
    cursor: pointer;
  }

  .tag-both:hover {
    color: #57a3f3;
  }

  .tag-left {
    float: left;
  }

  .tag-right {
    float: right;
  }

  .tag-down {
    background-color: white;
  }

  #tag-inside {
    margin-left: 38px;
    margin-right: 74px;
    overflow: hidden;
    white-space: nowrap;
  }
</style>
