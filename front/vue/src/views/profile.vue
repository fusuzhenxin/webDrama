<template>
    <div class="outer-container" style="background-color: white;">
      <div class="container" style="min-height: 100%; padding-bottom: 100px;">
        <el-header style="width: 100%;">
          <div class="nav-list">
            <navigation />
          </div>
        </el-header>
        <div class="profile-page">
          <div class="header"></div>
          <div class="content">
            <div class="sidebar">
              <img src="path-to-avatar.jpg" alt="Profile Picture" class="avatar">
              <h2 class="username">S.7-7.5</h2>
              <div class="stats">
                <span>0 关注</span>
                <span>0 粉丝</span>
                <span>0 转赞评</span>
              </div>
              <div class="tabs">
                <span class="tab active">动态</span>
                <span class="tab">视频</span>
              </div>
            </div>
            <div class="main-content">
              <div class="no-content" v-if="Material.length === 0">
                <p>No content available</p>
              </div>
              <div class="content-item" v-for="(item, index) in Material" :key="index" @click="goLoadDetails(item.id, item.name)">
                <img :src="item.cover" :alt="item.name">
                <p>{{ item.name }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import navigation from '@/components/navigation.vue';
  import { ref, onMounted } from 'vue';
  import request from '@/utils/request';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  const Material = ref([]);
  const userName = localStorage.getItem('userName');
  
  const PersonalMaterial = async (userName) => {
    const res = await request.get('/details/personalMaterial', { params: { username: userName } });
    Material.value = res.data.data;
    console.log('--', res.data.data);
  };
  
  const goLoadDetails = async (id, name) => {
    router.push({ name: 'VideoStory', params: { id: id, name: name } });
  };
  
  onMounted(() => {
    PersonalMaterial(userName);
  });
  </script>
  
  <style scoped>
  .nav-list {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 60px;
    list-style: none;
    background-color: black;
    z-index: 1000;
  }
  
  .profile-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #f5f5f5;
  }
  
  .header {
    width: 100%;
    height: 150px;
    background: linear-gradient(to right, #ff7e5f, #feb47b);
  }
  
  .content {
    display: flex;
    width: 100%;
    max-width: 1200px;
    background-color: #fff;
    margin-top: -50px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
  
  .sidebar {
    width: 200px;
    padding: 20px;
    background-color: #f9f9f9;
    display: flex;
    flex-direction: column;
    align-items: center;
    border-right: 1px solid #e0e0e0;
  }
  
  .avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    margin-bottom: 10px;
  }
  
  .username {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .stats {
    font-size: 14px;
    color: #999;
    margin-bottom: 20px;
  }
  
  .stats span {
    display: block;
    margin-bottom: 5px;
  }
  
  .tabs {
    width: 100%;
    display: flex;
    justify-content: space-around;
  }
  
  .tab {
    padding: 10px;
    font-size: 16px;
    cursor: pointer;
  }
  
  .tab.active {
    color: #ff7e5f;
    border-bottom: 2px solid #ff7e5f;
  }
  
  .main-content {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
  }
  
  .no-content {
    text-align: center;
  }
  
  .no-content img {
    width: 100px;
    height: 100px;
    margin-bottom: 10px;
  }
  
  .no-content p {
    color: #999;
  }
  
  .content-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 10px;
    cursor: pointer;
  }
  
  .content-item img {
    width: 100px;
    height: 100px;
    margin-bottom: 10px;
  }
  
  .content-item p {
    color: #333;
  }
  </style>
  