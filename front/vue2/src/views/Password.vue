<template>
    <el-card style="width: 500px;">
      <el-form label-width="120px" size="small" :model="form" :rules="rules" ref="pass">
        <el-form-item label="原密码" prop="password">
          <el-input v-model="form.password" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">确 定</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import request from '@/utils/request'
  import { useRouter } from 'vue-router'
  const router=useRouter()
  const userName=localStorage.getItem('userName')
  const form = reactive({
    password: '',
    newPassword: '',
    confirmPassword: '',
    username: ''
  });
  const rules = {
    password: [
      { required: true, message: '请输入原密码', trigger: 'blur' },
      { min: 3, message: '长度不少于3位', trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 3, message: '长度不少于3位', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 3, message: '长度不少于3位', trigger: 'blur' }
    ]
  };
  const user = ref(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {});
  const pass = ref(null);
  
  onMounted(() => {
    form.username = user.value.username;
  });
  
  const save = () => {
    pass.value.validate((valid) => {
      if (valid) {
        if (form.newPassword !== form.confirmPassword) {
          ElMessage.error("2次输入的新密码不相同");
          return false;
        }
        const userData={
            password: form.password,
            newPassword: form.newPassword,
            username: userName
        }
        // Assuming `request` is imported or defined somewhere
        request.post("/user/password", userData).then(res => {
          if (res.data.code === '200') {
            ElMessage.success("修改成功");
            // Assuming `store` is imported or defined somewhere
             //await 的作用是确保在进行后续操作之前，登出请求已经完成。
             request.get('/user/logout')
            // 清除本地存储
            localStorage.removeItem('token')
            localStorage.removeItem('userName')
            localStorage.removeItem('menus')
            localStorage.removeItem('userType')
            // 重定向到登录页面
            router.push('/login')
          } else {
            ElMessage.error(res.msg);
          }
        });
      }
    });
  };
  </script>
  
  <style>
  .avatar-uploader {
    text-align: center;
    padding-bottom: 10px;
  }
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
    width: 138px;
    height: 138px;
    line-height: 138px;
    text-align: center;
  }
  .avatar {
    width: 138px;
    height: 138px;
    display: block;
  }
  </style>
  