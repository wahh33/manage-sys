<template>
  <div :class="sideClass">
    <div class="logo" @click="returnHome" title="首页">
      <slot name="z-logo" v-if="!isCollapsed"></slot>
      <a class="col-logo" href="javascript:;" v-else></a>
    </div>
    <Menu ref="side_menu" :active-name="menuActiveName" :open-names="openMenuName" theme="light" width="auto" :class="menuitemClasses"
      :accordion="accordion" @on-select="chooseMenu" v-if="!isCollapsed">
      <template v-for="(menu,menu_index) in zList">
        <Submenu :name="menu.name" v-if="menu.children" :key="menu_index">
          <template slot="title">
            <div :class="itemClass">
              <Icon :size="23" :type="menu.icon"></Icon>
              <span>{{menu.title}}</span>
            </div>
          </template>
          <MenuItem :name="child.name" v-for="(child ,child_index) in menu.children" :key="child_index">
          <div :class="itemClass">
            <Icon :size="23" :type="child.icon"></Icon>
            <span>{{child.title}}</span>
          </div>
          </MenuItem>
        </Submenu>
        <MenuItem :name="menu.name" v-if="!menu.children" :key="menu_index">
        <div :class="itemClass">
          <Icon :size="23" :type="menu.icon"></Icon>
          <span>{{menu.title}}</span>
        </div>
        </MenuItem>
      </template>
    </Menu>
    <div class="col-menu" v-else>
      <template v-for="(menu,menu_index) in zList">
        <Dropdown placement="right-start" :key="menu_index" trigger="click" @on-click="chooseMenu" :transfer="true">
          <Button class="col-button" type="text">
            <Icon :size="25" :type="menu.icon"></Icon>
          </Button>
          <DropdownMenu slot="list">
            <template v-if="menu.children" v-for="(child,child_index) in menu.children">
              <DropdownItem :name="child.name" :key="child_index" class="drop">
                <div class="col-child">
                  <Icon :size="20" :type="child.icon"></Icon>
                  <span class="col-child-text">
                    {{ child.title }}
                  </span>
                </div>
              </DropdownItem>
            </template>
            <DropdownItem :name="menu.name" v-if="!menu.children" class="drop">
              <div class="col-child">
                <Icon :size="20" :type="menu.icon"></Icon>
                <span class="col-child-text">
                  {{ menu.title }}
                </span>
              </div>
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </template>
    </div>
  </div>
</template>

<script>
  export default {
    name: "zMenu",
    props: ["zList", "isCollapsed", "homeName", "accordion", "menuitemClasses"],
    data() {
      return {
        menuActiveName: '',
        openMenuName: [],
      }
    },
    computed: {
      itemClass() {
        return [
          'item',
          this.isCollapsed ? 'col-item' : ''
        ];
      },
      sideClass() {
        return [
          'side',
          this.isCollapsed ? 'col-side' : ''
        ];
      }
    },
    methods: {
      returnHome() {
        let self = this;
        self.$router.push({
          name: self.homeName
        });
      },
      chooseMenu(name) {
        let self = this;
        self.$router.push({
          name: name
        });
      },
    }
  }
</script>

<style scoped>
  .side {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    position: fixed;
    width: 200px;
    transition: all 0.2s ease-in-out;
  }

  .col-side {
    width: 80px;
  }

  .logo {
    height: 64px;
    background: linear-gradient(to left bottom, #3498db, #9b59b6);
    cursor: pointer;
    transition: all 0.2s ease-in-out;
  }

  .logo:hover {
    filter: brightness(120%);
  }

  .col-logo {
    background-image: url(../assets/logo.png);
    background-position: center;
    background-size: contain;
    background-repeat: no-repeat;
    display: block;
    height: 100%;
  }

  .col-menu {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;
    flex: 1;
  }

  .col-button {
    width: 80px;
    height: 50px;
  }

  .col-button:focus {
    background-color: #f0faff;
  }

  .drop:hover {
    background-color: #3498db;
    color: white;
  }

  .item {
    width: 100px;
    white-space: nowrap;
    overflow: hidden;
    transition: all 0.2s ease-in-out;
  }

  .item span {
    font-size: medium;
  }

  .col-item {
    width: 0px;
  }
</style>
