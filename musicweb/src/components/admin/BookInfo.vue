<template>
    <div>
        <el-container>
                <el-header>
                    <!-- 通过 slot 来指定在 input 中前置或者后置内容。 -->
                    <el-input placeholder="请输入内容" v-model="searchinput" class="input-with-select" style="margin-top:30px;height:20px" >
                         <el-select v-model="select" slot="prepend" placeholder="请选择">
                            <el-option label="课程名称" value="bookname" style="width:100px;"></el-option>
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
                            label="课程id"
                            width="50">
                        </el-table-column>
                        <el-table-column
                                prop="booktitle"
                                label="课程名字"
                                width="150">
                        </el-table-column>
                        <el-table-column
                                prop="bookcontent"
                                label="课程简介"
                                width="450px">
                        </el-table-column>
                         
                        <el-table-column
                                prop="bookpeoplenum"
                                label="课程浏览人数"
                                width="50">
                        </el-table-column>
                        <el-table-column
                                label="课程封面"
                                width="auto">
                                <template slot-scope="scope" >
                                 <div v-if="imgurlData[scope.$index]!=null">
                                 <ul v-for="item,index in imgurlData[scope.$index]"> 
                                      <img :src='getImgUrl(item)' style="width:200px;height:100px"/>
                                  </ul>
                                 </div>
                                 <div v-else>无图片</div>
                                </template>
                        </el-table-column>
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="530">
                        <template slot-scope="scope">
                            <el-button @click="aditwz(scope.row)"  size="small" type="primary" plain>添加章节</el-button>
                            <el-button @click="lookwz(scope.row)"  size="small" type="warning" plain>查看章节</el-button>
                            <el-button @click="deletewz(scope.row.id,scope.$index)" size="small" type="danger" plain>删除课程</el-button>
                            <el-button @click="addst(scope.row)"  size="small" type="success" plain>添加答题</el-button>
                            <el-button @click="lookst(scope.row)"  size="small" type="info" plain>查看题库</el-button>
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
                        >
                    </el-pagination>
                </div>
            </el-main>
            <el-dialog :visible.sync="wzEdit" class="dialog" v-loading="loading" element-loading-text="视频正在上传,需等候1分钟左右,请稍等" element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)">
                    <div class="ql-editor">
                        <div>
                            <el-form ref="form" :model="form" label-width="80px">
                                <el-form-item label="章节名称">
                                    <el-input v-model="form.zjtitle" maxlength="35" > </el-input>
                                </el-form-item>
                                <el-form-item label="视频上传">
                                     <el-upload
                                        class="upload-demo"
                                        ref="upload"
                                        drag
                                        action="ac"
                                        :limit="1"
                                        :on-exceed="handleExceed"
                                        :before-upload="beforeAvatarUpload"
                                        :http-request="uploadFile"
                                        :auto-upload="false"
                                        >
                                        <i class="el-icon-upload"></i>
                                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                                        <div class="el-upload__tip" slot="tip">只能上传video/mp4文件，且不超过100M</div>
                                    </el-upload>
                                </el-form-item>
                            </el-form>
                        </div>
                        
                        <div>
                            <quill-editor 
                                class="editor"
                                v-model="form.zjcontent"
                                ref="myQuillEditor" 
                                :options="editorOption" 
                                @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
                                @change="onEditorChange($event)">
                            </quill-editor> 
                        </div>
                        <el-form>
                            <el-form-item>
                                <el-button type="primary" style="margin-top:180px;width:200px" @click="add()">添加章节</el-button>    
                            </el-form-item>
                        </el-form>
                    </div>
                </el-dialog>
                <el-dialog :visible.sync="wzView" class="dialog">
                    <div class="ql-view">
                         <div>
                            <el-collapse v-model="activeName" accordion style="padding:20px;" v-for="item,index in zhangjieData" @change="collhandleChange">
                                <el-collapse-item :title="item.zjtitle" :name='index' style="padding:10px;" >
                                    <div style="width:80%;margin-left:10%;">
                                        <div style="width:80%;height:250px;margin-left:10%;" v-if="item.zjvideopath!=null">
                                            <video :src="'http://localhost/upimg/'+item.zjvideopath" controls="controls" style="width:100%;height:100%;">
                                            您的浏览器不支持 video 标签。
                                            </video>
                                        </div>
                                        <div v-html="item.zjcontent" style="margin-top:30px;"></div>
                                    </div>
                                </el-collapse-item>
                            </el-collapse>
                         </div>
                    </div>
                </el-dialog>
                <el-dialog :visible.sync="tkView" class="dialog">
                    <div class="ql-view" style="padding:10px;">
                            <div style="width:100%;text-align:center;"><h1>题库</h1></div>
                            <el-card style="width:50%;margin-left:25%;margin-top:20px;" v-for="item,index in problemData">
                                <div><h2>{{index+1}}.{{item.problemname}}</h2></div><br>
                                <div :style="item.daan=='A'?'color:#66FF00':''">A.{{item.atitle}}</div><br>
                                <div :style="item.daan=='B'?'color:#66FF00':''">B.{{item.btitle}}</div><br>
                                <div :style="item.daan=='C'?'color:#66FF00':''">C.{{item.ctitle}}</div><br>
                                <div :style="item.daan=='D'?'color:#66FF00':''">D.{{item.dtitle}}</div><br>
                            </el-card>
                    </div>
                </el-dialog>
        </el-container>
         
        <el-drawer
            title="答题添加"
            :visible.sync="drawer"
            :direction="direction"
            style="height:2000px"
            >
            <div style="width:1000px;">
                <div style="margin-left:600px;">
                    <el-form :model="problemform" :rules="rules" ref="info" label-width="0px" v-loading="loading" >
                        <el-form-item prop="problemname">
                            <el-input v-model="problemform.problemname" placeholder="问题" maxlength="35" style="width:400px">
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="atitle">
                            <el-input v-model="problemform.atitle" placeholder="A选项答案" maxlength="40" style="width:400px">
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="btitle">
                            <el-input v-model="problemform.btitle" placeholder="B选项答案" maxlength="40" style="width:400px">
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="ctitle">
                            <el-input v-model="problemform.ctitle" placeholder="C选项答案" maxlength="40" style="width:400px">
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="dtitle">
                            <el-input v-model="problemform.dtitle" placeholder="D选项答案" maxlength="15" style="width:400px">
                            </el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div style="margin-left:600px;">
                    答案:
                    <el-radio-group v-model="radio" style="margin-top:50px;">
                        <el-radio :label="1">A:{{problemform.atitle}}</el-radio><br>
                        <el-radio :label="2">B:{{problemform.btitle}}</el-radio><br>
                        <el-radio :label="3">C:{{problemform.ctitle}}</el-radio><br>
                        <el-radio :label="4">D:{{problemform.dtitle}}</el-radio><br>
                    </el-radio-group>
                </div>
                <div> <el-button type="success" @click="addproblem()" style="margin-left:600px;width:400px;margin-top:20px;">添加答题</el-button></div>
            </div>
        </el-drawer>
    </div>
    
</template>

<script>
// 工具栏配置
const toolbarOptions = [
  ["bold", "italic", "underline", "strike"], // 加粗 斜体 下划线 删除线
  ["blockquote", "code-block"], // 引用  代码块
  [{ header: 1 }, { header: 2 }], // 1、2 级标题
  [{ list: "ordered" }, { list: "bullet" }], // 有序、无序列表
  [{ script: "sub" }, { script: "super" }], // 上标/下标
  [{ indent: "-1" }, { indent: "+1" }], // 缩进
  // [{'direction': 'rtl'}],                         // 文本方向
  [{ size: ["small", false, "large", "huge"] }], // 字体大小
  [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题
  [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
  [{ font: [] }], // 字体种类
  [{ align: [] }], // 对齐方式
  ["clean"], // 清除文本格式
  ["link", "image", "video"] // 链接、图片、视频
];
import { quillEditor } from "vue-quill-editor";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import {addProblem,getProblem,getZhangjie,delBook,addZhangjie,addBookImg,findUserInfo,getUserCount,getUserSearch,delUser,updateUser,getPartStudent,delStudent,getPartBook} from '../../api/index';
import { log } from 'util';
export default {
    props: {
        /*编辑器的内容*/
        value: {
        type: String
        },
        /*图片大小*/
        maxSize: {
        type: Number,
        default: 4000 //kb
        }
    },

    components: {
        quillEditor
    },
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
            problemform:{
                bookid:'',
                problemname:'',
                daan:'',
                atitle:'',
                btitle:'',
                ctitle:'',
                dtitle:''
            },
            problemData:[],
            wzEdit:false,
            wzView:false,
            drawer:false,
            tkView:false,
            direction: 'ttb',
            A:'asdas',
            B:'sa',
            C:'asd',
            D:'asd',
            radio: 1,
            form:{
                bookid:'',
                zjtitle:'',
                zjcontent:'',
                zjvideopath:''
            },
            formPicsData:{
                name:''
            },
            fileList: [],
            delform:{id:''},
            searchinput: '',
            select: '课程名称',
            size:10,
            currentSize:0,
            total:0,
            activeName: '1',
            tableData:[],
            imgurlData:[],
            zhangjieData:[],
            isimgup:false,
            tkok:true,
            zjcontent:'asdasdasda',
            imgpath:'http://localhost/upimg/',
            loading:false,
            dialogFormVisible:false,
            rowData:[],
            formLabelWidth: '130px',
            rules: {
                problemname: [{ required: true, message: '请填写问题,不能为空', trigger: 'blur' }],
                atitle: [{ required: true, message: '请填写选项答案,不能为空', trigger: 'blur' }],
                btitle: [{ required: true, message: '请填写选项答案,不能为空', trigger: 'blur' }],
                ctitle: [{ required: true, message: '请填写选项答案,不能为空', trigger: 'blur' }],
                dtitle: [{ required: true, message: '请填写选项答案,不能为空', trigger: 'blur' }],
            },
            editorOption: {
                    placeholder: "",
                    theme: "snow", // or 'bubble'
                    placeholder: "您想说点什么？",
                    modules: {
                    toolbar: {
                        container: toolbarOptions,
                        // container: "#toolbar",
                        handlers: {
                        image: function(value) {
                            if (value) {
                            // 触发input框选择图片文件
                            document.querySelector(".avatar-uploader input").click();
                            } else {
                            this.quill.format("image", false);
                            }
                        },
                        // link: function(value) {
                        //   if (value) {
                        //     var href = prompt('请输入url');
                        //     this.quill.format("link", href);
                        //   } else {
                        //     this.quill.format("link", false);
                        //   }
                        // },
                        }
                    }
                }
            },
            header: {
                token: sessionStorage.token
            } // 有的图片服务器要求请求头需要有token

        }   
    },
    methods:{
        onEditorBlur() {
        //失去焦点事件
        },
        onEditorFocus() {
        //获得焦点事件
        },
        onEditorChange() {
        //内容改变事件
        this.$emit("input", this.content);
        },

        // 富文本图片上传前
        beforeUpload() {
        // 显示loading动画
        this.quillUpdateImg = true;
        },
        handleChange(response, file, fileList){
            this.urlData.push(response.url);
            console.log(response.url);
        },
        handleRemove(file,fileList){
            this.urlData = [];
            fileList.forEach(element => {
            this.urlData.push(element.url);
            });
        },
        uploadSuccess(res, file) {
        // res为图片服务器返回的数据
        // 获取富文本组件实例
        console.log(file);
        let quill = this.$refs.myQuillEditor.quill;
        // 如果上传成功
        if (res.code == 200) {
            // 获取光标所在位置
            let length = quill.getSelection().index;
            // 插入图片  res.url为服务器返回的图片地址
            quill.insertEmbed(length, "image", res.url);
            // 调整光标到最后
            quill.setSelection(length + 1);
        } else {
            this.$message.error("图片插入失败");
        }
        // loading动画消失
        this.quillUpdateImg = false;
        },
        // 富文本图片上传失败
        uploadError() {
        // loading动画消失
        this.quillUpdateImg = false;
        this.$message.error("图片插入失败");
        },
        handleSizeChange(val) {
            this.size = val;
            // this.findMsg();
            console.log(`每页 ${val} 条`);
        },
        handleCurrentChange(val) {
            this.currentSize = val;
            // this.findMsg();
            console.log(`当前页: ${val}`);
        },
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'video/mp4';
            const isLt2M = file.size / 1024 / 1024 < 100;

            if (!isJPG) {
            this.$message.error('上传视频文件是 MP4 格式!');
            }
            if (!isLt2M) {
            this.$message.error('上传视频大小不能超过 50MB!');
            }
            return isJPG && isLt2M;
        },
        uploadFile(file){
          this.isimgup = true;
          this.formPicsData.append('file', file.file);
        },
        handleExceed(files, fileList) {
            this.$message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
        },
        add(row){
            this.formPicsData = new FormData();
            this.$refs.upload.submit();
            this.loading = true;
            if(this.isimgup==true){
                addBookImg(this.formPicsData).then(res=>{
                if(res.resCode==1){
                    this.form.zjvideopath = res.webShowPath;
                    console.log(this.form);
                    this.addzj(this.form);
                }else{
                    this.$message.error("网络不好上传失败，请重新上传");
                    this.loading = false;
                    this.wzEdit = false;
                }
                this.loading = false;
            }).catch(res => {
                this.loading = false;
                this.$message.info(res);
            });
            }else{
                this.form.zjvideopath = null;
                this.addzj(this.form);
                this.loading = false;
            }
            
        },
        addzj(val){
            addZhangjie(val).then(res=>{
                        if(res==1){
                            this.$message.success("添加成功");
                        }else{
                            this.$message.error("网络异常添加失败，请重新添加");
                        }
                        this.wzEdit = false;
                        this.loading = false;
                    }).catch(res => {
                        this.loading = false;
                        this.wzEdit = false;
                        this.$message.error(res);
                    });
        },
        findMsg(){
            this.loading=true;
            this.formdata.pagesize = this.size;
            this.formdata.stpage = (this.currentSize<=1?0:this.currentSize-1)*this.formdata.pagesize;
            getPartBook(this.formdata).then(res=>{
                console.log(res);
                this.tableData = res.data;
                this.imgurlData = [];
                for(var k=0;k<this.tableData.length;k++){
                    if(this.tableData[k].bookimgpath!=null){
                        this.imgurlData[k] = this.tableData[k].bookimgpath.split(',');
                    }else{
                        this.imgurlData[k] = null;
                    }
                    
                }
                this.loading=false;
            }).catch(err => {
                this.$message.error(res);
                this.loading=false;
            })
        },
        aditwz(row){
            this.form.bookid = row.id;
            this.wzEdit = true; 
        },
        lookwz(row){
            console.log(row.id);
            getZhangjie(row.id).then(res=>{
                console.log(res);
                if(res.data.length==0){
                    this.$message.error("暂无章节,请添加");
                }else{
                    this.zhangjieData = res.data;
                    this.wzView = true;
                }
            })
        },
        addst(row){
            this.drawer = true;
            console.log("id="+row.id);
            this.problemform.bookid = row.id;
        }, 
        lookst(row){
            getProblem(row.id).then(res=>{
                console.log(res);
                if(res.data.length==0){
                    this.$message.error("暂无题库,请添加");
                }else{
                    this.problemData = res.data; 
                    this.tkView = true;
                }
                
            })
        },
        addproblem(){
            console.log(this.radio);
            if(this.radio=='1'){
                this.problemform.daan = 'A';
            }else if(this.radio=='2'){
                this.problemform.daan = 'B';
            }else if(this.radio=='3'){
                this.problemform.daan = 'C';
            }else if(this.radio=='4'){
                this.problemform.daan = 'D';
            }
            this.$refs.info.validate(valid => {
                if(valid){
                    this.loading = true;
                    addProblem(this.problemform).then(res=>{
                        console.log(res.data);
                        if(res==1){
                            this.$message.success("添加成功");
                        }else{
                            this.$message.error("添加失败,请重新添加");
                        }
                        this.loading = false;
                    }).catch(err => {
                        this.$message.error(res);
                        this.loading=false;
                    })
                }else{
                    this.$message.error('请正确输入信息');
                    return false;
                }
            })
        },

        collhandleChange(val){
            console.log(val);
        },
        deletewz(uid,index){
            this.$confirm('您确认删除该学生信息了吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            }).then(() => {
                    this.loading=true;
                    this.dialogFormVisible = false;
                    this.delform.id = uid;
                    delBook(this.delform).then(res=>{
                        if(res==1){
                            this.tableData.splice(index,1);//删除掉tableData数组上的第index个元素
                            this.imgurlData.splice(index,1);
                            this.loading=false;
                            this.$message.success("删除成功");
                        }else{
                            this.loading=false;
                            this.$message.error("删除失败");
                        }
                    }).catch(err => {
                        this.$message.error('服务器出错');
                        this.loading=false;
                    })
                 }).catch(() => {
                    this.dialogFormVisible = false;
                    this.loading=false;
                     
                });
        },
        getImgUrl(val){
            console.log(this.imgpath+val);
            return this.imgpath+val;
        },
        deleteClick(uid,index){
            
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
            if(to.path=='/bookinfo'){
                 this.findMsg();
            }
        }
    }
}
</script>
<style>
.el-pagination__jump{
    margin-left:34px;
}
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
.input-with-select .el-input__inner{
    width:400px;
}
.dialog{
  width:2200px;
  margin-left: -250px;
  margin-top: -100px;
}
.editor {
  line-height: normal !important;
  height: 570px;   
}
.ql-editor{
   height: 620px;   
}
.ql-container .ql-snow{
    height: 620px;
}
.ql-view{
   height: 650px;   
   overflow: auto;
} 
.ql-container.ql-snow{
  height: 650px;
}
.el-select .el-input__inner {
  width: 220px;
}
.ql-snow .ql-tooltip[data-mode=link]::before {
  content: "请输入链接地址:";
}
 
.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
    border-right: 0px;
    content: '保存';
    padding-right: 0px;
}

.ql-snow .ql-tooltip[data-mode=video]::before {
    content: "请输入视频地址:";
}

.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: '14px';
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value=small]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value=small]::before {
  content: '10px';
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value=large]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value=large]::before {
  content: '18px';
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value=huge]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value=huge]::before {
  content: '32px';
}

.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: '文本';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
  content: '标题1';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
  content: '标题2';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
  content: '标题3';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
  content: '标题4';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
  content: '标题5';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
  content: '标题6';
}

.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: '标准字体';
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value=serif]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value=serif]::before {
  content: '衬线字体';
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value=monospace]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value=monospace]::before {
  content: '等宽字体';
}
</style>