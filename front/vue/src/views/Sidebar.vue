<template>
<div class="outer-container" style="background-color: white;">
    <div class="container" style="min-height: 100%; padding-bottom: 100px;">
      <el-container>
        <el-header style="width: 100%;">
          <div class="nav-list">
            <navigation/>
          </div>
        </el-header>
    </el-container>
    <div id="app">
      <div class="sidebar">
        <div class="user-card">
          <img  src="@/assets/111.jpeg" alt="User Avatar" class="avatar">
          <h2>{{userName}}</h2>
          <button>开通</button>
        </div>
        <ul class="menu">
      <li><router-link to="/">创作平台</router-link></li>
      <li><router-link to="/profile">个人主页</router-link></li>      
      <li><router-link to="/Sidebar">观看记录</router-link></li>
      <li><router-link to="/favorites">收藏</router-link></li>
      <li><router-link to="/subscribe">订阅</router-link></li>
      <li><router-link to="/VideoUpload">上传视频</router-link></li>
      <li><router-link to="/vip">VIP会员</router-link></li>
      <li><router-link to="/diamonds">我的钻石</router-link></li>
      <li><router-link to="/settings">账号设置</router-link></li>
      <li><router-link to="/logout">退登</router-link></li>
    </ul>
      </div>
      <div class="main-content">
        <div class="content-grid">
          <div class="content-item" v-for="(item, index) in items" :key="index" @click="goLoadDetails(item.id,item.name)">
            <img :src="item.cover" :alt="item.name">
            <p>{{ item.name }}</p>
            <div style="display:flex">
              <el-icon><Iphone /></el-icon><p>观看到60%</p>
            </div>
            
          </div>
        </div>
      </div>
    </div>
</div>
</div>
  </template>
  <script setup>
  import { ref,onMounted } from 'vue';
  import Navigation from "@/components/navigation.vue";
  import request from "@/utils/request"
  import {  Iphone } from '@element-plus/icons-vue';
import router from '@/router/router';

  const items=ref([])
  const userName=localStorage.getItem('userName')
  
  const loadRecords =async()=>{
    const res = await request.post('/news/findAll', {
    username: userName,
  });

   items.value=res.data.data
  }

const goLoadDetails =async(id,name)=>{
  router.push({name: 'VideoStory',params: {id: id,name: name}});
}
  onMounted(()=>{
    loadRecords()
  })
  </script>
  
  <style scoped>
  #app {
    display: flex;
    height: 100vh;
  }
  .outer-container {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
  }
  .nav-list {
    position: fixed;
    top: 0px;
    left: 0px;
    width: 100%;
    height: 60px;
    list-style: none;
    background-color: black;
    z-index: 1000;
  }
  .sidebar {
    width: 250px;
    background-color: #f8f8f8;
    padding: 20px;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  }
  
  .user-card {
    text-align: center;
  }
  
  .avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
  }
  
  button {
    margin-top: 10px;
    padding: 10px 20px;
    background-color: #ff7b42;
    border: none;
    color: white;
    cursor: pointer;
  }
  
  .menu {
    list-style: none;
    padding: 0;
    margin-top: 20px;
  }
  
  .menu li {
    margin: 10px 0;
  }
  
  .main-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
  }
  
  .content-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .content-item {
    width: calc(15% - -10px);
    /* box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); */
    margin-bottom: 20px;
  }
  
  .content-item img {
    width: 100%;
    height: auto;
  }
  
  .content-item p {
    margin-top: -3px;
    margin-left:5px;
    color: width
  }

  </style>
  