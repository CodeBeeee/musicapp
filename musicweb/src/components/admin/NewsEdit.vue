<template>
    <div>
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
                             <el-option label="通知公告" value="通知公告"></el-option>
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
                <!-- 测试 -->
                <!-- <div>{{form.title}}<br>{{form.type}}<br>{{form.date}}</div> -->
                
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
                              :on-exceed="handleExceed"
                              list-type="picture"
                              :auto-upload="false">
                              <el-button size="small" type="primary">选择图片</el-button>
                              <div slot="tip" class="el-upload__tip">只能上传jpg文件，且不超过500kb,最多三张</div>
                      </el-upload>     
                      <quill-editor 
                          class="editor"
                          v-model="content"
                          ref="myQuillEditor" 
                          :options="editorOption" 
                          @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
                          @change="onEditorChange($event)">
                      </quill-editor>   
                  </div>
                </div>
                <div style="width:100%;align:left">
                    <el-button class="editor-btn" type="info" @click="getContent" >预览</el-button>
                    <el-button class="editor-btn" type="primary" @click="UploadSubmit" v-loading.fullscreen.lock="fullscreenLoading">提交</el-button>
                </div>
            </el-main>
        </el-container>
        
        <el-dialog 
          :visible.sync="dialogFormVisible" 
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
                    <img :src="this.urlData[0]" style="width:400px;height:300px;"/>  
                </div>
                
                <div v-else-if="urlData.length==2" style="text-align:center">
                      <img :src="this.urlData[0]" style="width:400px;height:300px;"/>
                      <img :src="this.urlData[1]" style="margin-left:50px;width:400px;height:300px;"/>   
                </div>

                <div v-else-if="urlData.length==3" style="text-align:center">
                  <div><img :src="this.urlData[0]" style="width:480px;height:250px;"/></div> 
                  <div style="margin-top:15px">
                    <img :src="this.urlData[1]" style="width:400px;height:250px;"/> 
                    <img :src="this.urlData[2]" style="margin-left:50px;width:400px;height:250px;"/> 
                  </div>
                </div>
              </div>
              <div class="dialogcontent">
                  <div v-html="content"></div>
              </div>
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
import { uploadImg,addNews} from '../../api/index';
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
  created(){
    //  const role = localStorage.getItem('ms_username');
    //  if(!role){
    //     this.$router.push('/');
    //  }
  },
  data() {
    return {
      content: this.value,
      quillUpdateImg: false,// 根据图片上传状态来确定是否显示loading动画，刚开始是false,不显示
      urlData:[],
      dialogFormVisible:false,
      form:{
            newstitle:'',
            newstype:'',
            publishdate:'',
            newscontent:'',
            imgurl:''
      }, 
      fullscreenLoading:false,
      url:'',
      formPicsData:{
        name:'123'
      },
      imgsrc:'',
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
    };
  },

  methods: {
      
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
          if(res=='') this.form.imgurl = null
          else this.form.imgurl = res;
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
     },
     getContent(){
       this.dialogFormVisible = true;
       this.content = this.$refs.myQuillEditor.quill.root.innerHTML;
       console.log(this.content);
     }
  }
};
</script> 

<style>
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