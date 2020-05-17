<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">音乐后台管理系统</div>
            <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.adminusername" placeholder="username" maxlength="15">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                        type="password"
                        placeholder="password"
                        v-model="param.adminpassword"
                        @keyup.enter.native="submitForm()"
                        maxlength="15"
                    >
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm()">登录</el-button>
                </div>
                <p class="login-tips">Tips : 用户名和密码请准确填写，不能为空。</p>
            </el-form>
        </div>
    </div>
</template>

<script>
import {AdminLoginCheck} from '../../api/index';
export default {
    data: function() {
        return {
            param: {
                adminusername: 'admin',
                adminpassword: '123123',
            },
            rules: {
                adminusername: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                adminpassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
            },
        };
    },
    methods: {
        submitForm() {
            this.$refs.login.validate(valid => {
                if (valid) {
                    AdminLoginCheck(this.param).then(res=>{
                        console.log(res);
                        if(res.msg=="loginsuccess"){
                            this.$message.success('登录成功');
                            localStorage.setItem('ms_username', this.param.adminusername);
                            this.$router.push('/');
                        }else if(res.msg=="loginerror" || res.msg=="nullerror"){
                            this.$message.error('账号密码错误');
                        }
                        
                    })
                    // this.$router.push('/');
                } else {
                    this.$message.error('请输入账号和密码');
                    return false;
                }
            });
        },
    },
};
</script>

<style scoped>
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background-image: url(../../assets/img/login-bg.jpg);
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 20px;
    color: black;
    border-bottom: 1px solid black;
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: rgba(255, 255, 255, 0.3);
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
</style>