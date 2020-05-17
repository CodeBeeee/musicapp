<template>
    <div>
        <el-container>
                <el-header>
                    <!-- 通过 slot 来指定在 input 中前置或者后置内容。 -->
                    <el-input placeholder="请输入内容" v-model="searchinput" class="input-with-select" style="margin-top:30px;height:20px" >
                         <el-select v-model="select" slot="prepend" placeholder="请选择">
                            <el-option label="用户账号" value="username"></el-option>
                            <el-option label="手机号码" value="phone"></el-option>
                        </el-select>
                        <el-button slot="append" icon="el-icon-search" @click="userSearch()" ></el-button>
                    </el-input>
                </el-header>
                <el-main>
                    <el-table
                        v-loading="loading"
                        :data="tableData"
                        border
                        style="width: 100%">
                        <el-table-column
                            fixed
                            prop="id"
                            label="用户编号"
                            width="200">
                        </el-table-column>
                        <el-table-column
                                prop="username"
                                label="用户账号"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                prop="password"
                                label="用户密码"
                                width="auto">
                        </el-table-column>
                         
                        <el-table-column
                                prop="phone"
                                label="用户手机"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                prop="email"
                                label="用户邮件"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="200">
                        <template slot-scope="scope">
                            <el-button @click="handleClick(scope.row)" type="primery"  plain >编辑</el-button>
                            <el-button @click="deleteClick(scope.row.uid,scope.$index)" type="success" plain >删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页代码 -->
                <div class="block">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page="currentSize"
                        :page-sizes="[10, 15, 20, 25]"
                        :page-size="size"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="total">
                    </el-pagination>
                    
                </div>
                <!-- 个人信息编辑弹出框 -->
                <el-dialog title="个人信息" :visible.sync="dialogFormVisible"  >
                    <el-form :model="editdata" :rules="rules" ref="edit">
                        <el-form-item prop="username" label="学生学号" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.stuloginuid"   style="width:60%;"></el-input>
                        </el-form-item>
                        <el-form-item prop="usernick" label="学生姓名" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.stuname"   style="width:60%;" ></el-input>
                        </el-form-item>
                        <el-form-item prop="email" label="学生手机号码" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.stuphone"   style="width:60%;"></el-input>
                        </el-form-item>
                        <el-form-item prop="phonenum" label="学生专业" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.stuteach"   style="width:60%;"></el-input>
                        </el-form-item>
                    </el-form>
                    <div slot="footer" class="dialog-footer">
                            <el-button @click="dialogFormVisible = false">取 消</el-button>
                            <el-button type="primary" @click="updateData()">修 改</el-button>
                    </div>
                </el-dialog>
                </el-main>
        </el-container>
        
    </div>
</template>
<style>
.input-with-select{
    width: 500px;
    height: 33px;
}
.el-select .el-input {
    width: 100px;
    height: 30px;
}
.input-with-select .el-input-group__prepend {
    background-color: #fff;
}
</style>
<script>
import {getSearchUser,getPartUser,findUserInfo} from '../../api/index';
export default {
    name:"userinfo",
    data(){
         
        return{
            formdata:{
               stpage:'',
               pagesize:''     
            },
            searchform:{
               search:'',
               searchvalue:''     
            },
            editdata:{
                uid:'',
                stuloginuid:'',
                stuname:'',
                stuphone:'',
                stuteach:''
            },
            delform:{id:''},
            searchinput: '',
            select: '',
            size:10,
            currentSize:0,
            total:0,
            tableData:[],
            loading:false,
            dialogFormVisible:false,
            rowData:[],
            formLabelWidth: '130px',
             
        }   
    },
    methods:{
        handleSizeChange(val) {
            this.size = val;
            this.findMsg();
            console.log(`每页 ${val} 条`);
        },
        handleCurrentChange(val) {
            this.currentSize = val;
            //this.findMsg();
            console.log(`当前页: ${val}`);
        },
        getUserCount(){
            getUserCount().then(res=>{
                this.total = res;
            })
        },
        findMsg(){
            this.loading=true;
            this.formdata.pagesize = this.size;
            this.formdata.stpage = (this.currentSize<=1?0:this.currentSize-1)*this.formdata.pagesize;
            // 【0,10】
            // 【10,10】
            getPartUser(this.formdata).then(res=>{
                console.log(res);
                this.tableData = res;
                this.loading=false;
            }).catch(err => {
                this.$message.error(res);
                this.loading=false;
            })
        },
        userSearch(){
            this.loading=true;
            this.searchform.searchvalue = this.searchinput;
            getSearchUser(this.searchform).then(res=>{
                this.tableData = res;
                this.total = this.tableData.length;
                this.loading=false;
            }).catch(err => {
                this.$message.error('服务器出错');
                this.loading=false;
            })
        },
       
         
         
    },
        
    created(){
        this.findMsg();
    },
    watch:{
        select(val,oldvalue){
            this.searchform.search = val;
        },
        $route(to,from){
            if(to.path=='/userinfo'){
                 this.findMsg(); 
            }
        }
    }
}
</script>