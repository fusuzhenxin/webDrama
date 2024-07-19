<template>
  <el-header style="height: 60px;">
    <el-menu :default-active="activeIndex" style="justify-content: center; background-color: black; border-bottom: 0px solid;" mode="horizontal" @select="handleMenuSelect">
      <img style="width: 150px; height: 80px; margin-top: -5px;" src="@/assets/logo_transparent.png" />
      <router-link to="/HelloWorld">
        <el-menu-item style="color: white;" index="HelloWorld">首页</el-menu-item>
      </router-link>
      <router-link to="/traversing">
        <el-menu-item style="color: white;" index="traversing">时空之旅</el-menu-item>
      </router-link>
      <router-link to="/counterattack">
        <el-menu-item style="color: white;" index="counterattack">逆袭</el-menu-item>
      </router-link>
      <router-link to="/sweet">
        <el-menu-item style="color: white;" index="sweet">甜宠</el-menu-item>
      </router-link>
      <router-link to="/rebirth">
        <el-menu-item style="color: white;" index="rebirth">重生</el-menu-item>
      </router-link>
      <router-link to="/Entertainment">
        <el-menu-item style="color: white;" index="Entertainment">娱乐新闻</el-menu-item>
      </router-link>
      <router-link to="/korean">
        <el-menu-item style="color: white;" index="korean">韩剧</el-menu-item>
      </router-link>
      <div class="flex gap-4 items-center">
        <el-input
          v-model="searchQuery"
          style="width: 240px; margin-top: 10px; margin-left: 30px; height: 34px; background-color: black;"
          size="large"
          placeholder="搜索"
          :prefix-icon="Search"
        />
      </div>
      <el-button @click="search" style="width: 34px; height: 34px; margin-top: 10px; margin-left: 10px; border-radius: 2px; background-color: black;">
        <img style="width: 20px; height: 20px;" src="../assets/1.png" />
      </el-button>
      <div v-if="isLoggedIn" class="demo-type">
        <div style="margin-left: 30px; margin-top: 5px;">
          <el-dropdown trigger="click" class="custom-dropdown">
            <span class="el-dropdown-link" style="width: 35px;height: 35px;">
              <el-avatar :icon="UserFilled" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">登出</el-dropdown-item>
                <el-dropdown-item @click="Sidebar">我的信息</el-dropdown-item>
                <el-dropdown-item @click="personal"></el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <span class="dropdown-label">{{ userName }}</span>
        </div>
      </div>
      <el-text v-else class="login" @click="login">登录</el-text>
      <el-text class="login" @mouseenter="showWatchRecordBox" @mouseleave="hideWatchRecordBox">观看记录
        <div v-if="showBox" class="watch-record-box">
          <ul>
            <li v-for="(drama, index) in recordsList" :key="index" class="records" @click="recordsDetails(drama.id,drama.name)">
              {{ drama.name }}
            </li>
          </ul>
        </div>
      </el-text>
    </el-menu>
  </el-header>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { UserFilled, Search } from '@element-plus/icons-vue';
import request from '@/utils/request';
import axios from 'axios';

const userName = localStorage.getItem('userName');
const router = useRouter();
const searchQuery = ref('');
const isLoggedIn = ref(false);
const recordsList = ref([]);
isLoggedIn.value = localStorage.getItem('isLoggedIn'); // 控制登录状态
const showBox = ref(false);

const search = () => {
  if (searchQuery.value.trim() !== '') {
    router.push({ name: 'search', params: { searchQuery: searchQuery.value } });
  }
};

// 观看记录
const records = async () => {
  console.log('12424', userName);
  const res = await request.post('/news/findAll', {
    username: userName,
  });
  recordsList.value = res.data.data;
  console.log('12424', res.data);
};

//观看记录跳转
const recordsDetails =async(dramaId,name)=>{
  router.push({ name: 'VideoStory', params: { id: dramaId ,name: name} });
}

const login = () => {
  router.push('/login');
};

const logout = async () => {
  try {
    // 从 localStorage 中获取动态 Token
    const token = localStorage.getItem('token');

    // 配置对象包含请求头，使用动态获取的 Token
    const config = {
      headers: {
        'Authorization': `Bearer ${token}`, // 使用从 localStorage 获取的 Token
        'Content-Type': 'application/json'
      }
    };

    // 发送带有请求头的 GET 请求
    await axios.get('http://localhost:9090/api/user/logout', config);
    
    // 更新状态和清除本地存储
    isLoggedIn.value = false;
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('userName');
    localStorage.removeItem('token'); // 清除 Token
    // 调用记录函数
    records();
  } catch (error) {
    console.error('Logout failed:', error);
    // 处理错误情况
  }
};



const Sidebar = () => {
  router.push('/Sidebar');
};

const showWatchRecordBox = () => {
  showBox.value = true;
};

const hideWatchRecordBox = () => {
  showBox.value = false;
};

onMounted(() => {
  records();
});
</script>

<style scoped>
.el-input--large .el-input__wrapper {
  background-color: black;
}
.demo-type {
  display: flex;
}
.login {
  cursor: pointer;
  color: white;
  margin-left: 10px;
  position: relative;
}
.watch-record-box {
  display: none;
  position: absolute;
  top: 100%; /* 显示在文本下方 */
  left: 0;
  width: 180px;
  height: auto;
  background-color: white;
  border: 1px solid #ccc;
  z-index: 1000;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}
.login:hover .watch-record-box {
  display: block;
}
.custom-dropdown .dropdown-label {
  cursor: pointer;
  color: white;
  margin-left: 10px;
}
.records{
  color: black; /* 默认文字颜色 */
  list-style-type: disc; /* 列表项前加上点 */
  padding-left: 20px; /* 为列表项留出点的空间 */
  text-decoration: underline; /* 文字下划线 */
  cursor: pointer; /* 鼠标样式为指针 */
  margin-bottom: 10px; /* 每个名字间距 */
}

.records:hover {
  color: red; /* 鼠标悬停时的颜色 */
}

</style>
