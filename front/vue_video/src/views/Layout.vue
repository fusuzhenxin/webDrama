
<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <!-- 放置图片 -->
        <div class="logo-container">
          <img src="../assets/img.png" alt="Logo" class="logo">
        </div>
        <nav>
          <ul class="nav-list">
            <li><h1 style="color: white;margin-right: 20px">短剧网</h1></li>

            <!-- 搜索框和按钮 -->
            <li class="search-box">
              <div class="search-container">
                <input type="text" placeholder="搜索" v-model="searchQuery" @keyup.enter="search">
                <button @click="search"><img src="../assets/1.png"></button>
              </div>
            </li>
            <ul class="top-nav-list">
              <li v-if="isLoggedIn" @click="toggleDropdown" class="user-dropdown">
                <img  src="../assets/img_7.png" class="avatar">
                <span style="color: white;" class="username">{{ username }}</span>
                <!-- 下拉菜单 -->
                <ul v-if="showDropdown" class="dropdown-menu">
                  <li><a @click="logout">立刻登录</a></li>
                </ul>
              </li>
            </ul>

            <!-- 导航项 -->
            <div class="block-list">
              <ul>
                <!-- 首页导航项 -->
                <li><router-link to="/helloWorld">首页</router-link></li>
                <!-- 购物车导航项 -->
                <li><router-link to="/Counterattack">逆袭</router-link></li>
                <!-- 订单导航项 -->
                <li><router-link to="/traversing">穿越</router-link></li>
                <!-- 我的账户导航项 -->
                <li><router-link to="/sweet">甜宠</router-link></li>
                <!-- 我的收藏夹导航项 -->
                <li><router-link to="/rebirth">重生</router-link></li>
              </ul>
            </div>
          </ul>
        </nav>
      </el-header>
      <el-main>
        <HelloWorld/>
      </el-main>
      <el-footer>Footer</el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import HelloWorld from "@/components/HelloWorld.vue";
import { useRouter } from "vue-router";
const router=useRouter()

const showDropdown = ref(false);
const username = ref("User"); // 你可以根据你的需要设置默认用户名
const searchQuery = ref('');

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value; // 切换下拉菜单的显示状态
};
const search = () => {
  console.log('Search method called');
  // 如果搜索框有内容，执行路由跳转
  if (searchQuery.value.trim() !== '') {
    router.push({name: 'search',params:{searchQuery:searchQuery.value}}); // 这里替换为你要跳转的页面路径
  }
};
const isLoggedIn = () => {
  return !!username.value; // 根据用户是否存在判断用户是否已登录
};

const logout = () => {
  // 这里添加注销登录的逻辑，比如清除用户信息等
  console.log('注销登录');
};
</script>


<style>
/* 样式部分省略，保持不变 */
/* 顶部导航栏样式 */
.nav-list {
  position: fixed;
  top: -15px;
  left: 8px;
  width: 97%;
  list-style: none;
  padding: 13px;
  background-color: black;
  display: flex;
  align-items: center;
  z-index: 1000;
}
.common-layout{
  background-color: white;
}
.nav-list li {
  display: inline-block;
  margin-right: 10px;
}

.nav-list li a {
  text-decoration: none;
  color: white;
  font-size: 20px;
  margin-right: 15px;
  font-family: '宋体', Arial, sans-serif;
}

.nav-list li a:hover {
  color: #ff6600;
}

/* 导航项样式 */
.block-list ul {
  margin-left: 120px;
}

/* 图片样式 */
.logo-container {
  top: 0px;
  left: 0;
  padding: 0px;
  z-index: 1001; /* 确保图片在导航栏上方 */

}

.logo {
  margin-top: 76px;
    width: 1400px;
    height: 480px; /* 你想要的高度 */
  margin-right: 20px;

}
.search-box {
  display: flex;
  align-items: center;
  margin-left: 180px; /* 将搜索框和按钮向右移动 */
  position: absolute; /* 设置绝对定位 */
  left: 600px; /* 距离右边缘的距离 */
}

.search-container {
  display: flex;
}

.search-container input {
  padding: 5px;
  margin-right: 0px;
  height: 40px;
  width: 200px;
  border: 1px solid #ccc; /* 添加边框样式 */
  font-family: '宋体', Arial, sans-serif; /* 设置搜索框文字为宋体 */
}

.search-container button {
  width: 51px;
  margin-top: 0px;
  padding: 5px;
  background-color: white;
  color: white;
  cursor: pointer;
  height: 51px;
  border: 1px solid #ccc; /* 添加边框样式 */
  font-family: '宋体', Arial, sans-serif; /* 设置搜索框文字为宋体 */

}
.search-container button:hover {
  background-color: #ff6600; /* 悬停时的颜色变化 */
}

.search-container img {
  width: 25px; /* 使图片填充按钮 */
  height: 25px;
}
/* 添加头像样式 */
.avatar {
  width: 40px;
  height: 40px;
  margin-right: 10px; /* 调整头像和用户名之间的距离 */
  vertical-align: middle; /* 垂直居中对齐 */
}
/* 下拉箭头样式 */
.dropdown-arrow {
  color: white;
  margin-left: 5px; /* 调整箭头与用户名之间的距离 */
}

/* 用户下拉菜单容器样式 */
.user-dropdown {
  position: relative; /* 相对定位，以便绝对定位下拉菜单 */
}

/* 下拉菜单样式 */
.dropdown-menu {
  position: absolute; /* 绝对定位 */
  top: 100%; /* 下拉菜单位于用户名下方 */
  right: 0; /* 右对齐 */
  background-color: #333; /* 背景颜色 */
  z-index: 1; /* 设置 z-index 以确保下拉菜单位于其他内容之上 */
  list-style-type: none; /* 去除列表样式 */
  padding: 5px 0; /* 调整内边距 */
}

/* 下拉菜单项样式 */
.dropdown-menu li {
  margin: 5px 0px;
}

/* 下拉菜单链接样式 */
.dropdown-menu li a {
  color: white;
  text-decoration: none;
  font-size: 14px;
}

/* 下拉菜单链接悬停样式 */
.dropdown-menu li a:hover {
  background-color: #555;
}
</style>
