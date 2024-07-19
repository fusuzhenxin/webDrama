<template>
    <div class="auth-container">
      <div class="auth-card">
        <div class="left-panel">
          <div class="illustration">
            <!-- Replace with your illustration -->
            <img src="../assets/111.jpeg" alt="Decorative illustration">
          </div>
        </div>
        <div class="right-panel">
          <h1>{{ panelTitle }}</h1>
          <p>{{ panelDescription }}</p>
          <form @submit.prevent="handleSubmit">
            <div class="input-group">
              <label for="username">用户名</label>
              <input type="text" id="username" v-model="username" required>
            </div>
            <div class="input-group">
              <label for="password">密码</label>
              <input type="password" id="password" v-model="password" required>
            </div>
            <div class="input-group" v-if="isRegistering">
              <label for="confirmPassword">再次输入密码</label>
              <input type="password" id="confirmPassword" v-model="confirmPassword" required>
            </div>
            <div class="input-group">
            <label for="rememberMe">记住我</label>
          
          </div>
          <div class="ewmemb">
            <input type="checkbox" v-model="rememberMe">
          </div>
          
            <a href="#" class="forgot-password">忘记密码？</a>
            <button type="submit" class="submit-btn">{{ isRegistering ? '注册' : '登录' }}</button>
          </form>
          <div class="social-login">
          </div>
          <p class="switch-mode">
            {{ isRegistering ? '已有账户?' : '没有账号?' }}
            <a href="#" @click.prevent="toggleMode">{{ isRegistering ? '登录' : '创建账户' }}</a>
          </p>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>

import { ref, computed } from 'vue'
import request from '@/utils/request';
import router from '@/router/router';
import { ElMessage } from 'element-plus';
// import axios from 'axios';

  const isRegistering = ref(false)
  const username = ref('')
  const password = ref('')
  const confirmPassword = ref('')
  const rememberMe = ref(false)
  const panelTitle = computed(() => 
    isRegistering.value ? '注册账户' : '欢迎回来'
  )
  
  const panelDescription = computed(() => 
    isRegistering.value 
      ? ''
      : '登录体验生活'
  )
  
  const toggleMode = () => {
    isRegistering.value = !isRegistering.value
    username.value = ''
    password.value = ''
    confirmPassword.value = ''
  }
  
  const handleSubmit = () => {
    if (isRegistering.value) {
      handleRegister()
    } else {
      handleLogin()
    }
  } 
  
  const handleLogin = async () => {
  try {
    console.log('当前用户是：', username.value);

    // 确保将数据传递给请求体而不是作为查询参数
    const res = await request.post('/user/login', { 
      userName: username.value, 
      password: password.value, 
    });
    
    if(res.data.code == 200){
    const token= res.data.data.token
    const userName=res.data.data.userName
    localStorage.setItem("userName",userName)
    localStorage.setItem("token",token)
    router.push('/')
    console.log('完整响应：', res.data.data.token);
    }
    if(res.data.code == 401){
      ElMessage.error("账户或者密码错误，请重新输入")
    }
    

    // Login logic here
    console.log('Logging in', username.value, password.value);
  } catch (error) {
    console.error('Error during login:', error);
  }
};

  const handleRegister =async() => {
    // Registration logic here
    if (password.value !== confirmPassword.value) {
      ElMessage.warning('两次输入的密码不一致')
      return
    }
    const res = await request.post('/user/register',{
      userName:username.value,
      password:password.value
    });

    if(res.data.code == 200){
      ElMessage.success('注册成功')
      router.push('/login')
    }
    console.log('Registering', username.value, password.value)
  }
  </script>
  
  <style scoped>
  .auth-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
    padding: 20px;
  }
  
  .auth-card {
    display: flex;
    width: 800px;
    height: 600px;
    background-color: white;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .left-panel, .right-panel {
    flex: 1;
    padding: 40px;
  }
  
  .left-panel {
    background-color: whitesmoke;
    color: #333;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
  }
  
  .illustration {
    margin-bottom: 20px;
  }
  
  .illustration img {
    max-width: 100%;
    height: auto;
  }
  
  .dots span {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #ddd;
    margin: 0 4px;
  }
  
  .dots span.active {
    background-color: #333;
  }
  
  .right-panel {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  h1 {
    margin-bottom: 10px;
  }
  
  .input-group {
    margin-bottom: 15px;
  }
  .input-groupOne {
    margin-bottom: 5px;
  }
  .ewmemb{
    margin-top: -35px;
    margin-left: -212px;
  }
  
  label {
    display: block;
    margin-bottom: 5px;
    font-size: 14px;
  }
  
  input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
  }
  
  .forgot-password {
    display: block;
    text-align: right;
    margin-bottom: 15px;
    color: #666;
    text-decoration: none;
    font-size: 12px;
  }
  
  .submit-btn {
    width: 100%;
    padding: 10px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
  }
  
  .social-login {
    margin-top: 20px;
    text-align: center;
  }
  
  .google-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 8px;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
  }
  
  .google-btn img {
    margin-right: 10px;
    width: 20px;
    height: 20px;
  }
  
  .switch-mode {
    margin-top: 20px;
    text-align: center;
    font-size: 14px;
  }
  </style>
  