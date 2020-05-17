<template>
    <div>
    <el-card class="box-card" v-loading="loading">
        <div slot="header" class="clearfix">
            <span>学生信息添加</span>
        </div>
        <el-form :model="stuinfo" :rules="rules" ref="info" label-width="0px" >
                 <el-form-item prop="stuname">
                    <el-input v-model="stuinfo.stuname" placeholder="学生姓名" maxlength="15" style="width:320px">
                        <el-button slot="prepend" icon="el-icon-user" ></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="stuloginuid">
                    <el-input v-model="stuinfo.stuloginuid" placeholder="学生学号" maxlength="15" style="width:320px">
                        <el-button slot="prepend" icon="el-icon-s-promotion
" ></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="stupassword">
                    <el-input
                        type="password"
                        placeholder="学生初始登录密码"
                        v-model="stuinfo.stupassword"
                        @keyup.enter.native="addstudent()"
                        maxlength="15"
                        style="width:320px"
                    >
                    <el-button slot="prepend" icon="el-icon-s-goods" ></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="stuphone">
                    <el-input v-model="stuinfo.stuphone" placeholder="学生手机" maxlength="15" style="width:320px">
                        <el-button slot="prepend" icon="el-icon-phone" ></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="stuteach">
                    <el-input v-model="stuinfo.stuteach" placeholder="学生专业" maxlength="15" style="width:320px">
                        <el-button slot="prepend" icon="el-icon-s-order" ></el-button>
                    </el-input>
                </el-form-item>
                 
                <div class="login-btn" style="margin-top:40px">
                    <el-button type="success" style="width:320px" @click="addstudent()">学生信息添加</el-button>
                </div>
        </el-form>
         
    </el-card>
    <el-dialog title="学生信息确认" :visible.sync="EditdialogFormVisible"  >
            <el-container>
                <el-main>
                    <div>
                         <div class="text item">
                                <div class="pre" style="float:left;width:15%;font-size:23px;margin-left:25%;">学生姓名:</div>
                                <div class="last" style="float:left;width:35%"> <el-input v-model="stuinfo.stuname" placeholder="请输入姓名" :disabled="true"></el-input></div>
                        </div>
                        <div class="text item">
                                <div class="pre" style="float:left;width:15%;font-size:23px;margin-left:25%;">学生学号:</div>
                                <div class="last" style="float:left;width:35%"> <el-input v-model="stuinfo.stuloginuid" placeholder="请输入学号" :disabled="true"></el-input></div>
                        </div>
                        <div class="text item">

                                <div class="pre" style="float:left;width:15%;font-size:23px;margin-left:25%;">初始密码:</div>
                                <div class="last" style="float:left;width:35%"> <el-input v-model="stuinfo.stupassword" placeholder="请输入初始密码" :disabled="true"></el-input></div>
                        </div>
                        <div class="text item">
                                <div class="pre" style="float:left;width:15%;font-size:23px;margin-left:25%;">手机号码:</div>
                                <div class="last" style="float:left;width:35%"> <el-input v-model="stuinfo.stuphone" placeholder="请输入手机号码" :disabled="true"></el-input></div>
                        </div>
                        <div class="text item">
                                <div class="pre" style="float:left;width:15%;font-size:23px;margin-left:25%;">学生专业:</div>
                                <div class="last" style="float:left;width:35%"> <el-input v-model="stuinfo.stuteach" placeholder="请输入专业" :disabled="true"></el-input></div>
                        </div>
                    </div>
                </el-main>
            </el-container>         
            <div slot="footer" class="dialog-footer">
                <el-button @click="EditdialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="addok">确认添加 </el-button>             
            </div>        
        </el-dialog> 
    </div>
</template>
<script>
import {addStudentPost} from '../../api/index';
export default {

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
        return{
            stuinfo:{
                stuname:'',
                stuloginuid:'',
                stupassword:'',
                stuphone:'',
                stuteach:''
            },
            rules: {
                stuname: [{ required: true, message: '请输入学生姓名，不能为空', trigger: 'blur' }],
                stuloginuid: [{ required: true, message: '请输入学生学号，不能为空', trigger: 'blur' }],
                stupassword: [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
                stuphone: [{ validator: checkPhone, trigger: 'blur' }],
                stuteach: [{ required: true, message: '请输入学生专业，不能为空', trigger: 'blur' }],
            }, 
            EditdialogFormVisible:false,
            loading:false
        }
    },
    methods:{
        addstudent(){
            this.$refs.info.validate(valid => {
                if(valid){
                    this.EditdialogFormVisible = true;
                }else{
                    this.$message.error('请正确输入信息');
                    return false;
                }
            })

        },
        addok(){
            this.EditdialogFormVisible = false;
            this.loading = true;
            addStudentPost(this.stuinfo).then(res => {
                if(res==1){
                    this.$message.success("添加成功");
                }else{
                     this.$message.error("存在此学生,添加失败");
                }
                this.loading = false;
            }).catch(res => {
                    this.loading = false;
                    this.$message.info("服务器繁忙,请稍后再试.");
                });
        }
    }
}
</script>
<style>
  .text {
    font-size: 14px;
  }

  .item {
    padding: 18px 0;
    margin-top: 15px;
  }

  .box-card {
    width: 60%;
    height: 80%;
    margin-left: 20%;
    margin-top: 5%;
    text-align: center;
  }
  .el-form-item__error{
   margin-left: 30%;
  }
   
</style>