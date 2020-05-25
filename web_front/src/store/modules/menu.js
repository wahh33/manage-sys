import menuRouter from '@/router/menuRouter.js';

const menu = {
  state: {
    childMenu: menuRouter,
    rootMenu: [{
        name: 'InfoRoot',
        title: '信息管理',
        icon: 'ios-contacts',
        children: [],
        index: 0
      },
      {
        name: 'NeedRoot',
        title: '需求管理',
        icon: 'md-megaphone',
        children: [],
        index: 1
      },
      {
        name: 'PlanRoot',
        title: '计划管理',
        icon: 'ios-paper',
        children: [],
        index: 2
      },
      {
        name: 'OrderRoot',
        title: '订单管理',
        icon: 'ios-print',
        children: [],
        index: 3
      },
      {
        name: 'ReturnRoot',
        title: '退单管理',
        icon: 'md-print',
        children: [],
        index: 4
      },
      {
        name: 'StoreRoot',
        title: '库存管理',
        icon: 'md-settings',
        children: [],
        index: 5
      },
      {
        name: 'AnalysisRoot',
        title: '统计分析',
        icon: 'md-pulse',
        children: [],
        index: 6
      },
    ]
  }
}
export default menu;
