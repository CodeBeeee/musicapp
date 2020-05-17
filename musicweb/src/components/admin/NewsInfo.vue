<template>
    <div>
        <el-container>
                <el-header>
                    <!-- 通过 slot 来指定在 input 中前置或者后置内容。 -->
                    <el-input placeholder="请输入内容" v-model="searchinput" class="input-with-select" style="margin-top:30px;height:20px" >
                         <el-select v-model="select" slot="prepend" placeholder="请选择">
                            <el-option label="新闻标题" value="newstitle"></el-option>
                            <el-option label="新闻类别" value="newstype"></el-option>
                            <el-option label="发布日期" value="publishdate"></el-option>
                        </el-select>
                        <el-button slot="append" icon="el-icon-search" @click="newsSearch()" ></el-button>
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
                            label="新闻编号"
                            width="50">
                        </el-table-column>
                        <el-table-column
                                prop="newstitle"
                                label="新闻标题"
                                width="200">
                        </el-table-column>
                        <el-table-column
                                prop="newstype"
                                label="新闻种类"
                                width="100">
                        </el-table-column>
                        <el-table-column
                                label="新闻图片"
                                width="250">
                               <template slot-scope="scope" >
                                 <div v-if="imgurlData[scope.$index]!=null">
                                  <ul v-for="item,index in imgurlData[scope.$index]"> 
                                      <img :src="require('../../assets/uploadimg/'+item)" style="width:200px;height:100px"/>
                                  </ul>
                                 </div>
                                 <div v-else>无图片</div>
                                </template>
                        </el-table-column>
                        <el-table-column
                                prop="newscontent"
                                label="新闻内容"
                                width="780">
                        </el-table-column>
                        <el-table-column
                                prop="publishdate"
                                label="发布日期"
                                width="200">
                        </el-table-column>
                        <el-table-column
                                prop="clicknums"
                                label="浏览数"
                                width="100">
                        </el-table-column>
                        <el-table-column
                                prop="commentstate"
                                label="是否有评论"
                                width="auto">
                        </el-table-column>  
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="250">
                        <template slot-scope="scope">
                            <el-button @click="lookClick(scope.row)" type="success" size="small">查看</el-button>
                            <!-- <el-button @click="handleClick(scope.row)" type="primary" size="small">编辑</el-button> -->
                            <el-button @click="deleteClick(scope.row.uid,scope.$index)" type="danger" size="small">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页代码以及逻辑-->
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
                
                </el-main>
        </el-container>
        <!-- 预览新闻弹窗 -->
        <el-dialog 
          :visible.sync="NewsdialogFormVisible" 
          fullscreen
          class="dialog"
          >
          <div class="ql-editor">
              <div class="dialogheader">
                    <div> <b>{{form.newstitle}}</b> </div>
                    <div style="text-align:right;margin-top:20px;width:93%;font-size:10px">
                        <div>发布日期:{{form.publishdate}}&nbsp&nbsp类别:{{form.newstype}}</div>
                    </div>
              </div>
              <div class="dialogimg">
                <div v-if="urlData.length==1"  style="text-align:center" >
                    <img :src="require('../../assets/uploadimg/'+this.urlData[0])" style="width:400px;height:300px;"/>  
                </div>
                
                <div v-else-if="urlData.length==2" style="text-align:center">
                      <img :src="require('../../assets/uploadimg/'+this.urlData[0])" style="width:400px;height:300px;"/>
                      <img :src="require('../../assets/uploadimg/'+this.urlData[1])" style="margin-left:50px;width:400px;height:300px;"/>   
                </div>

                <div v-else-if="urlData.length==3" style="text-align:center">
                  <div><img :src="require('../../assets/uploadimg/'+this.urlData[0])" style="width:480px;height:250px;"/></div> 
                  <div style="margin-top:15px">
                    <img :src="require('../../assets/uploadimg/'+this.urlData[1])" style="width:400px;height:250px;"/> 
                    <img :src="require('../../assets/uploadimg/'+this.urlData[2])" style="margin-left:50px;width:400px;height:250px;"/> 
                  </div>
                </div>
              </div>
              <div class="dialogcontent">
                  <div v-html="content"></div>
              </div>
          </div>
        </el-dialog>

        <!-- 新闻编辑弹出框 -->
        <el-dialog title="个人信息" :visible.sync="EditdialogFormVisible"  >
            <el-container>
            <el-header style="height:120px;margin-top:20px">
                <div>
                 <el-form ref="form" :model="form" label-width="80px">
                    <el-form-item label="新闻标题">
                        <el-input v-model="form.newstitle" maxlength="20" > </el-input>
                    </el-form-item>
                     <el-form-item label="新闻类别" >
                         <el-select v-model="form.newstype" placeholder="请选择类别"  >
                             <el-option label="本村动态" value="本村动态"></el-option>
                             <el-option label="领导班子" value="领导班子"></el-option>
                             <el-option label="党建园地" value="党建园地"></el-option>
                             <el-option label="特色经济" value="特色经济"></el-option>
                             <el-option label="民生工程" value="民生工程"></el-option>
                             <el-option label="乡村文化" value="乡村文化"></el-option>
                             <el-option label="村务公开" value="村务公开"></el-option>
                             <el-option label="乡村荣誉" value="乡村荣誉"></el-option>
                         </el-select>
                    </el-form-item>
                    <el-form-item label="发表时间">
                         <el-date-picker
                            v-model="form.publishdate"
                            type="date"
                            placeholder="选择日期"
                            value-format=" yyyy-MM-dd"
                            format="yyyy-MM-dd">
                         </el-date-picker>
                    </el-form-item>
                    
                 </el-form>
                </div>
            </el-header>
            <el-main>
                <div class="container" style="height:auto;">
                  <div>
                      <el-upload
                              class="avatar-uploader"
                              ref="upload"
                              action="/as"
                              :headers = "headers"
                              :on-preview="handlePreview"
                              :on-remove="handleRemove"
                              :before-upload="beforeAvatarUpload"
                              :http-request="uploadFile"
                              :on-change="handleChange"
                              multiple
                              :limit="3"
                              :file-list="fileList"
                              :on-exceed="handleExceed"
                              list-type="picture"
                              :auto-upload="false">
                              <el-button size="small" type="primary">选择图片</el-button>
                              <div slot="tip" class="el-upload__tip">只能上传jpg文件，且不超过500kb,最多三张</div>
                      </el-upload> 
                      <quill-editor 
                          style="line-height: normal !important;height: 590px;"
                          v-model="content"
                          ref="myQuillEditor" 
                          :options="editorOption" 
                          @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
                          @change="onEditorChange($event)">
                      </quill-editor>   
                  </div>
                </div>     
            </el-main>
        </el-container>         
            <div slot="footer" class="dialog-footer">
                <el-button @click="EditdialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="updateData()">修 改</el-button>             
            </div>        
        </el-dialog>   
        
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
import {findNewsInfo,getNewsCount,getNewsSearch,delNews} from '../../api/index';
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
            delform:{
                id:''
            },
            form:{
                newstitle:'',
                newstype:'',
                publishdate:'',
                newscontent:'',
                imgurl:''
            }, 
            searchinput: '',
            select: '',
            size:10,
            currentSize:0,
            total:0,
            tableData:[],
            loading:false,
            NewsdialogFormVisible:false,
            EditdialogFormVisible:false,
            rowData:[],
            formLabelWidth: '130px',
            imgurlData:[[]],
            content:'',
            urlData:'',
            fileList:[],
            quillUpdateImg:false,
            fullscreenLoading:false,
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
        getNewsCount(){
            getNewsCount().then(res=>{
                this.total = res;
                console.log("total"+res);
            })
        },
        findMsg(){
            this.loading=true;
            this.formdata.stpage = this.currentSize<=1?0:this.currentSize-1;
            this.formdata.pagesize = this.size;
            findNewsInfo(this.formdata).then(res=>{
                this.tableData = res.data;
                for(var k=0;k<this.tableData.length;k++){
                    if(this.tableData[k].imgurl!=null){
                        this.imgurlData[k] = this.tableData[k].imgurl.split(',');
                    }else{
                        this.imgurlData[k] = null;
                    }
                    
                }
                this.loading=false;
                
            }).catch(err => {
                this.$message.error('服务器出错'+err);
                this.loading=false;
            })
        },
        newsSearch(){
            this.loading=true;
            this.searchform.searchvalue = this.searchinput;
            getNewsSearch(this.searchform).then(res=>{
                this.tableData = res.data;
                this.total = this.tableData.length;
                this.loading=false;
            }).catch(err => {
                this.$message.error('服务器出错');
                this.loading=false;
            })
        },
        handleClick(val){
            console.log(val);
            this.EditdialogFormVisible = true;
            this.form = val;
            this.content = this.form.newscontent;
        },
        deleteClick(uid,index){
            this.loading=true;
            this.delform.id = uid;
            console.log(this.delform);
            delNews(this.delform).then(res=>{
                console.log(res);
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
                this.dialogFormVisible = true;
                this.content = this.$refs.myQuillEditor.quill.root.innerHTML;
                console.log(this.content);
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                    
                });
                this.dialogFormVisible = false;
            }).catch(() => {
                
                
            });
          },
      lookClick(data){
            console.log(data);
            this.NewsdialogFormVisible = true;
            this.form.newstitle = data.newstitle;
            this.form.newstype = data.newstype;
            this.form.publishdata = data.publishdata;
            this.urlData = data.imgurl.split(',');
            this.content = data.newscontent;
            console.log(this.urlData.length+this.urlData[0]);
        },
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
    beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg';
        const isLt2M = file.size / 1024 / 1024 < 2;

        if (!isJPG) {
          this.$message.error('上传头像图片只能是 JPG 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 2MB!');
        }
        return isJPG && isLt2M;
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      //c
      uploadFile(file){
          this.formPicsData.append('file', file.file);
      },
      UploadSubmit(){
        this.form.newscontent = this.$refs.myQuillEditor.quill.root.innerHTML;
        this.fullscreenLoading = true;
        this.formPicsData = new FormData();
        this.$refs.upload.submit();// 这里是执行文件上传的函数，其实也就是获取我们要上传的文件
        uploadImg(this.formPicsData).then(res => {
          this.form.imgurl = res;
          addNews(this.form).then(res=>{
            console.log(res);
            this.fullscreenLoading = false;
            this.$message.success("成功上传");
          }).catch(res => {
            this.fullscreenLoading = false;
            this.$message.info("服务器繁忙,请稍后再试.");
          });
        }).catch(res => {
          this.fullscreenLoading = false;
          this.$message.info("服务器繁忙,请稍后再试.");
        });
     }
    },
        
    created(){
        this.getNewsCount();
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
            console.log(to.path);
            if(to.path=='/newsinfo'){
                this.getNewsCount();
                this.findMsg();
            }
        }
    }
}
</script>
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
.editor {
  line-height: normal !important;
  height: 590px;   
  
}
.editor-btn{
    width: 10%;
    margin-top: 10px;
    height:50px;
}
.content{
  line-height: normal !important; 
  width:100%;
}
.dialog{
  padding-left:150px;
  padding-right:150px;
  padding-top:30px;
  padding-bottom: 30px;
}
.dialogheader{
  width:80%;
  margin-left: 10%;
  margin-top: -20px;
  font-size: 35px;
  text-align: center;
}
.dialogimg{
  margin-top: 30px;
  width:100%;
}
 
.dialogcontent{
  margin-top: 50px;
  width:70%;
  margin-left: 15%;
}
.ql-editor{
   height: 550px;   
}
.ql-container.ql-snow{
  height: 550px;
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