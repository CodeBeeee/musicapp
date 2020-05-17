<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="8">
                <el-card shadow="hover" class="mgb20" style="height:auto;">
                    <div class="user-info">
                        <img src="../../assets/img/img.jpg" class="user-avator" alt="">
                        <div class="user-info-cont">
                            <div class="user-info-name">{{name}}</div>
                            <div>{{role}}</div>
                        </div>
                    </div>
                
                </el-card>
                <el-card shadow="hover" style="height:400px;">
                    <div slot="header" class="clearfix">
                        <span>公告栏</span>
                        <el-button style="float: right; padding: 3px 0" type="text">添加</el-button>
                    </div>
                    <el-table :data="todoList" :show-header="false" height="304" style="width: 100%;font-size:14px;">
                        <el-table-column width="40">
                            <template slot-scope="scope">
                                <el-checkbox v-model="scope.row.status"></el-checkbox>
                            </template>
                        </el-table-column>
                        <el-table-column>
                            <template slot-scope="scope">
                                <div class="todo-item" :class="{'todo-item-del': scope.row.status}">{{scope.row.title}}</div>
                            </template>
                        </el-table-column>
                        <el-table-column width="60">
                            <template slot-scope="scope">
                                <i class="el-icon-edit"></i>
                                <i class="el-icon-delete"></i>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
                    
            </el-col>
            <el-col :span="16">
                <el-row :gutter="20" class="mgb20" >
                    <el-col :span="12" >
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-1" style="height:160px">
                                <i class="el-icon-lx-people grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{usernums}}</div>
                                    <div>用户总数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="12">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-2" style="height:160px">
                                <i class="el-icon-lx-notice grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{booknums}}</div>
                                    <div>播放总数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    
                </el-row>
                <el-row :gutter="20" class="mgb20">
                        <el-card class="box-card">
                            <div class="text item">
                                登录IP地址:{{ip}}
                            </div>
                            <div class="text item">
                                登录地点:{{area}}
                            </div>
                            <div class="text item">
                                登录时间:{{nowtime}}
                            </div>
                            <div class="text item">
                                浏览器:{{brower}}
                            </div>
                            <div class="text item">
                                操作系统:{{os}}
                            </div>
                        </el-card>
                </el-row>
                
            </el-col>
        </el-row>
    </div>
</template>

 
<script>
    import {getMusicPlayCount,getCount} from '../../api/index';
    import bus from '../common/bus';
    import * as sysTool from '../../assets/js/systemTools'
    export default {
        name: 'dashboard',
        data() {
            return {
                name: localStorage.getItem('ms_username'),
                usernums:'0',
                booknums:'0',
                userdata:[],
                ip: '1.1.1.1',
                area: '北京市',
                brower: 'chrome',
                os: 'windows7',
                nowtime:'2010-10-10'
            }
        },
         
        computed: {
            
            role() {
                return this.name === 'admin' ? '超级管理员' : '普通用户';
            }
        },
        created(){
            this.init();
            this.getTime();
            const role = localStorage.getItem('ms_username');
            this.ip = sessionStorage.getItem('ip')
            this.area = sessionStorage.getItem('area')
            this.brower = sysTool.GetCurrentBrowser()
            this.os = sysTool.GetOs()
            console.log('ip，地区，浏览器，操作系统，：',  this.ip, this.area,this.brower, this.os)
            // if(!role){
            //     this.$router.push('/login');
            // }
             
        },
       
        deactivated(){
             
            bus.$off('collapse', this.handleBus);
        },
        methods: {
             getTime(){
                 this.nowtime = new Date().toLocaleString();
             },
             init(){
                 getMusicPlayCount().then(res=>{
                    this.booknums = res;
                 })
                 getCount().then(res=>{
                    this.usernums = res;
                 })
             }
        },
        watch:{
            $route(to,from){
                if(to.path=='/dashboard'){
                    this.init()
                }
            }
        }
    }

</script>


<style scoped>
    .el-row {
        margin-bottom: 20px;
    }

    .grid-content {
        display: flex;
        align-items: center;
        height: 100px;
    }

    .grid-cont-right {
        flex: 1;
        text-align: center;
        font-size: 14px;
        color: #999;
    }

    .grid-num {
        font-size: 30px;
        font-weight: bold;
    }

    .grid-con-icon {
        font-size: 50px;
        width: 100px;
        height: 100px;
        text-align: center;
        line-height: 100px;
        color: #fff;
    }

    .grid-con-1 .grid-con-icon {
        background: rgb(45, 140, 240);
    }

    .grid-con-1 .grid-num {
        color: rgb(45, 140, 240);
    }

    .grid-con-2 .grid-con-icon {
        background: rgb(100, 213, 114);
    }

    .grid-con-2 .grid-num {
        color: rgb(45, 140, 240);
    }

    .grid-con-3 .grid-con-icon {
        background: rgb(242, 94, 67);
    }

    .grid-con-3 .grid-num {
        color: rgb(242, 94, 67);
    }

    .user-info {
        display: flex;
        align-items: center;
    }

    .user-avator {
        width: 120px;
        height: 120px;
        border-radius: 50%;
    }

    .user-info-cont {
        padding-left: 50px;
        flex: 1;
        font-size: 14px;
        color: #999;
    }

    .user-info-cont div:first-child {
        font-size: 30px;
        color: #222;
    }

    .user-info-list {
        font-size: 14px;
        color: #999;
        line-height: 25px;
    }

    .user-info-list span {
        margin-left: 70px;
    }

    .mgb20 {
        margin-bottom: 20px;
    }

    .todo-item {
        font-size: 14px;
    }

    .todo-item-del {
        text-decoration: line-through;
        color: #999;
    }

    .schart {
        width: 100%;
        height: 200px;
    }
    .text {
        font-size: 14px;
    }

    .item {
        padding: 18px 0;
    }

    .box-card {
        width: 98%;
        margin-left: 1%;
    }

</style>
