<template>
    <div>
        <el-container>
            <el-header style="height:120px;margin-top:20px">
                <div>
                 <el-form ref="form" :model="form" label-width="80px">
                    <el-form-item label="课程名称">
                        <el-input v-model="form.booktitle" maxlength="20" > </el-input>
                    </el-form-item>
                    <el-form-item label="发表时间">
                         <el-date-picker
                            v-model="form.bookpublishdate"
                            type="date"
                            placeholder="选择日期"
                            value-format=" yyyy-MM-dd"
                            format="yyyy-MM-dd">
                         </el-date-picker>
                    </el-form-item>
                    <el-form-item label="封面图片" >
                          <el-upload
                            ref="upload"
                            action="ac"
                            :auto-upload="false"
                            list-type="picture-card"
                            :limit="1"
                            :on-preview="handlePictureCardPreview"
                            :on-remove="handleRemove"
                            :on-exceed="handleExceed"
                            :http-request="uploadFile"
                            >
                            <i class="el-icon-plus"></i>
                           </el-upload>
                        <el-dialog :visible.sync="dialogVisible">
                            <img width="100%" :src="dialogImageUrl" alt="">
                        </el-dialog>
                    </el-form-item>
                    <el-form-item label="课程简介" >
                            <el-input
                                type="textarea"
                                :rows="10"
                                maxlength="250"
                                placeholder="请输入内容"
                                v-model="form.bookcontent">
                            </el-input>

                    </el-form-item>
                 </el-form>
                 <el-button type="success" style="width:200px;margin-left:6%;" @click="add()" v-loading.fullscreen.lock="fullscreenLoading">添加课程信息</el-button>
                </div>
                 
            </el-header>
        </el-container>
        
        <el-dialog 
          :visible.sync="dialogFormVisible" 
          fullscreen
          class="dialog"
          >
        </el-dialog>
         
    </div>

</template>
<style>
 .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
   
</style>

<script>
import {addBookImg,addBookInfo} from '../../api/index';
  export default {
    data() {
      return {
        dialogImageUrl: '',
        dialogVisible: false,
        isimgup:false,
        form:{
            bookcontent:'',
            booktitle:'',
            bookpublishdate:'',
            bookimgpath:''
        },
        imgurl:"http://localhost/upimg/1.jpg",
        formPicsData:{
            name:'123'
        },
        fullscreenLoading:false,
        dialogFormVisible:false,
      };
    },
    methods: {
      handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制只能选择 1 张图片，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      uploadFile(file){
        this.isimgup = true;
        this.formPicsData.append('file', file.file);
      },
      add(){
          this.formPicsData = new FormData();
          this.$refs.upload.submit();
          this.fullscreenLoading = true;
          if(this.isimgup==true){
              addBookImg(this.formPicsData).then(res=>{
            if(res.resCode==1){
                this.form.bookimgpath = res.webShowPath;
                    addBookInfo(this.form).then(res=>{
                        if(res==1){
                            this.$message.success("成功上传");
                        }else{
                            this.$message.error("服务器出错，请重新上传");
                        }
                    })
                    
                }else{
                    this.$message.error("网络不好上传失败，请重新上传");
                }
                this.fullscreenLoading = false;
                }).catch(res => {
                  this.fullscreenLoading = false;
                  this.$message.info(res);
                });
          }else{
                this.$message.error("请添加一张封页图片");
                this.fullscreenLoading = false;
          }
          
      }
    },
    computed: {
         
    },
  }
</script>