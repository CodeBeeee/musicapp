<template>
    <div>
        <el-container>
                <el-header>
                    <!-- 通过 slot 来指定在 input 中前置或者后置内容。 -->
                    <el-input placeholder="请输入内容" v-model="searchinput" class="input-with-select" style="margin-top:30px;height:20px" >
                         <el-select v-model="select" slot="prepend" placeholder="请选择">
                            <el-option label="账号" value="username"></el-option>
                            <el-option label="手机号码" value="phonenum"></el-option>
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
                            prop="uid"
                            label="用户ID"
                            width="200">
                        </el-table-column>
                        <el-table-column
                                prop="username"
                                label="账号"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                prop="usernick"
                                label="用户昵称"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                prop="email"
                                label="邮箱"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                prop="phonenum"
                                label="手机号"
                                width="auto">
                        </el-table-column>
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="200">
                        <template slot-scope="scope">
                            <el-button @click="handleClick(scope.row)" type="text" size="small">编辑</el-button>
                            <el-button @click="deleteClick(scope.row.uid,scope.$index)" type="text" size="small">删除</el-button>
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
                    <!-- <div>
                        测试数值显示:<br>
                        每页:{{size}}条<br>
                        当前页:{{currentSize}}
                    </div> -->
                </div>
                <!-- 个人信息编辑弹出框 -->
                <el-dialog title="个人信息" :visible.sync="dialogFormVisible"  >
                    <el-form :model="editdata" :rules="rules" ref="edit">
                        <el-form-item prop="username" label="账号" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.username"   style="width:60%;"></el-input>
                        </el-form-item>
                        <el-form-item prop="usernick" label="用户昵称" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.usernick"   style="width:60%;" ></el-input>
                        </el-form-item>
                        <el-form-item prop="email" label="邮箱" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.email"   style="width:60%;"></el-input>
                        </el-form-item>
                        <el-form-item prop="phonenum" label="手机号码" :label-width="formLabelWidth" style="margin-left:15%">
                            <el-input v-model="editdata.phonenum"   style="width:60%;"></el-input>
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
import {findUserInfo,getUserCount,getUserSearch,delUser,updateUser} from '../../api/index';
import { log } from 'util';
export default {
    name:"userinfo",
    data(){
        var checkPhone = (rule, value, callback) => {
            const phoneReg = /^1[3|4|5|7|8][0-9]{9}$/
            if (!value) {
            return callback(new Error('电话号码不能为空'))
            }
            setTimeout(() => {
            // Number.isInteger是es6验证数字是否为整数的方法,但是我实际用的时候输入的数字总是识别成字符串
            // 所以我就在前面加了一个+实现隐式转换

            if (!Number.isInteger(+value)) {
                callback(new Error('请输入数字值'))
            } else {
                if (phoneReg.test(value)) {
                callback()
                } else {
                callback(new Error('电话号码格式不正确'))
                }
            }
            }, 100)
        }
        var checkEmail = (rule, value, callback) => {
            const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/
            if (!value) {
            return callback(new Error('邮箱不能为空'))
            }
            setTimeout(() => {
            if (mailReg.test(value)) {
                callback()
            } else {
                callback(new Error('请输入正确的邮箱格式'))
            }
            }, 100)
        }
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
                username:'',
                usernick:'',
                email:'',
                phonenum:''
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
            rules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                passwordagain: [{ required: true,maxlength:"15", message: '再次输入密码', trigger: 'blur' }],
                email: [{ validator: checkEmail, trigger: 'blur' }],
                phonenum: [{ validator: checkPhone, trigger: 'blur' }]
            },
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
            this.findMsg();
            console.log(`当前页: ${val}`);
        },
        getUserCount(){
            getUserCount().then(res=>{
                  
                this.total = res;
            })
        },
        findMsg(){
            this.loading=true;
            this.formdata.stpage = this.currentSize<=1?0:this.currentSize-1;
            this.formdata.pagesize = this.size;
            findUserInfo(this.formdata).then(res=>{
                this.tableData = res.data;
                this.loading=false;
            }).catch(err => {
                this.$message.error('服务器出错');
                this.loading=false;
            })
        },
        userSearch(){
            this.loading=true;
            this.searchform.searchvalue = this.searchinput;
            getUserSearch(this.searchform).then(res=>{
                this.tableData = res.data;
                this.total = this.tableData.length;
                this.loading=false;
            }).catch(err => {
                this.$message.error('服务器出错');
                this.loading=false;
            })
        },
        handleClick(val){
            this.dialogFormVisible = true;
            this.editdata = val;
        },
        deleteClick(uid,index){
            this.loading=true;
            this.delform.id = uid;
            delUser(this.delform).then(res=>{
                if(res==1){
                    this.tableData.splice(index,1);//删除掉tableData数组上的第index个元素
                    this.getNewsCount();
                    this.loading=false;
                    this.$message.success("删除成功");
                }else{
                    this.loading=false;
                    this.$message.error("服务器繁忙,删除失败");
                }
               
            }).catch(err => {
                this.$message.error('服务器出错');
                this.loading=false;
            })
        },
        updateData() {
            this.$confirm('您确认修改信息了吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            }).then(() => {
                
                this.loading=true;
                this.dialogFormVisible = false;
                this.$refs.edit.validate(valid => {
                    if(valid){
                    updateUser(this.editdata).then(res=>{
                        if(res==1){
                            this.loading=false;
                            this.getUserCount();
                            this.findMsg();
                            this.$message.success("修改成功");
                        }else{
                            this.loading=false;
                            this.$message.success("服务器繁忙");
                        }}).catch(() => {
                            this.dialogFormVisible = false;
                            this.loading=false;
                            this.$message.success("服务器出错");
                    });
                
           
                    }else{
                          this.loading=false;
                          this.findMsg();
                          this.$message.error('请正确填写信息');
                          return false;
                    }

                })
                 }).catch(() => {
                    this.dialogFormVisible = false;
                    this.loading=false;
                    this.$message.success("服务器出错");
                });
                
          }
    },
        
    created(){
        this.getUserCount();
        this.findMsg();
    },
    watch:{
        size(val,oldvalue){
            console.log(val+"  "+oldvalue);
        },
        currentSize(){
            console.log("000");
        },
        select(val,oldvalue){
            this.searchform.search = val;
        },
        $route(to,from){
            if(to.path=='/usersinfo'){
                this.getUserCount();
                this.findMsg();
            }
        }
    }
}
</script>