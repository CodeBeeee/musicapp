import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    mode: 'history', // 加上这个配置项，url默认的#就不会出现了
    routes: [
        {
            path: '',
            redirect: '/dashboard'
        },
        {
            path: '',
            component: () => import(/* webpackChunkName: "home" */ '../components/common/Home.vue'),
            meta: { title: '自述文件' },
            children: [
                {
                    path: '/dashboard',
                    component: () => import(/* webpackChunkName: "dashboard" */ '../components/music/Dashboard.vue'),
                    meta: { title: '系统首页' }
                },{
                    path: '/userinfo',
                    component: () => import(/* webpackChunkName: "icon" */ '../components/music/Userinfo.vue'),
                    meta: { title: '用户信息' }
                },{
                    path: '/userplayinfo',
                    component: () => import(/* webpackChunkName: "icon" */ '../components/music/Userplayinfo.vue'),
                    meta: { title: '用户播放记录' }
                },{
                    path: '/musicplayinfo',
                    component: () => import(/* webpackChunkName: "icon" */ '../components/music/Musicplayinfo.vue'),
                    meta: { title: '音乐播放记录'}
                },
                // {
                //     path: '/table',
                //     component: () => import(/* webpackChunkName: "table" */ '../components/admin/BaseTable.vue'),
                //     meta: { title: '基础表格' }
                // },
                // {
                //     path: '/tabs',
                //     component: () => import(/* webpackChunkName: "tabs" */ '../components/admin/Tabs.vue'),
                //     meta: { title: 'tab选项卡' }
                // },
                // {
                //     path: '/form',
                //     component: () => import(/* webpackChunkName: "form" */ '../components/admin/BaseForm.vue'),
                //     meta: { title: '基本表单' }
                // },
                // {
                //     // 富文本编辑器组件
                //     path: '/editor',
                //     component: () => import(/* webpackChunkName: "editor" */ '../components/admin/VueEditor.vue'),
                //     meta: { title: '富文本编辑器' }
                // },
               
                // {
                //     // 图片上传组件
                //     path: '/upload',
                //     component: () => import(/* webpackChunkName: "upload" */ '../components/admin/Upload.vue'),
                //     meta: { title: '文件上传' }
                // },
                // {
                //     // vue-schart组件
                //     path: '/charts',
                //     component: () => import(/* webpackChunkName: "chart" */ '../components/admin/BaseCharts.vue'),
                //     meta: { title: 'schart图表' }
                // },
                // {
                //     // 拖拽列表组件
                //     path: '/drag',
                //     component: () => import(/* webpackChunkName: "drag" */ '../components/admin/DragList.vue'),
                //     meta: { title: '拖拽列表' }
                // },
                // {
                //     // 拖拽Dialog组件
                //     path: '/dialog',
                //     component: () => import(/* webpackChunkName: "dragdialog" */ '../components/admin/DragDialog.vue'),
                //     meta: { title: '拖拽弹框' }
                // },
                {
                    // 国际化组件
                    path: '/i18n',
                    component: () => import(/* webpackChunkName: "i18n" */ '../components/admin/I18n.vue'),
                    meta: { title: '国际化' }
                },
               
                {
                    path: '/404',
                    component: () => import(/* webpackChunkName: "404" */ '../components/admin/404.vue'),
                    meta: { title: '404' }
                },  
                     
            ]
             
        },
        {
            path: '/login',
            component: () => import(/* webpackChunkName: "404" */ '../components/admin/Login.vue'),
            meta: { title: '登录页面' }
        },  
         
    ]
});
