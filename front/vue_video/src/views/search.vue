<template>
  <div class="common-layout">
    <el-container>
      <el-header>

        <nav>
          <ul class="nav-list">
            <li><h1 style="color: white;margin-right: 20px;margin-left: 26px">短剧网</h1></li>

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
    </el-container>
  </div>
  <div class="search-page">
    <!-- 搜索框和按钮 -->
    <li class="search-box">
      <div class="search-containers">
        <input type="text" placeholder="搜索" v-model="searchQuery" @keyup.enter="search">
        <button @click="search"><img src="../assets/1.png"></button>
      </div>
    </li>
  </div>
  <!-- 搜索结果 -->
  <div class="search-results">
    <!-- 显示搜索结果的内容 -->
    <div v-if="shortDramas.length > 0">
      <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.id, drama.name)">
        <!-- ... -->
      </div>
    </div>
    <!-- 默认内容 -->
    <div v-else>
      <!-- 这里可以添加一些其他内容作为默认显示 -->
      <div class="drama-section">
        <br>
        <div class="drama-list">
          <div v-for="(drama, index) in videoData" :key="index" class="drama-card" @click="goToDramaDetail(drama.id,drama.name)">
            <div class="drama-details">
              <!-- 封面图像 -->
              <div class="image-wrapper">
                <img :src="drama.cover" :alt="drama.name" class="drama-image">
                <div class="play-button">
                  <i class="fas fa-play"></i>
                </div>
              </div>


              <div class="details-s">
                <p>{{ drama.name }}</p>
                <button>{{ drama.classify }}</button>
                <button>民国</button>
                <br>
                <span>{{drama.description}}</span>
              </div>


              <!-- 这里可以添加其他详情，比如剧集简介、演员名单等 -->
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>


  <div class="drama-section">
    <br>
    <div class="drama-list">
      <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.id, drama.name)">
        <div class="drama-details">
          <!-- 封面图像 -->
          <div class="image-wrapper">
            <img :src="drama.cover" :alt="drama.name" class="drama-image">
            <div class="play-button">
              <i class="fas fa-play"></i>
            </div>
          </div>
          <!-- 剧集其他详情 -->

          <div class="details-s">
            <p>{{ drama.name }}</p>
            <button>{{ drama.classify }}</button>
            <button>民国</button>
            <br>
            <span>{{drama.description}}</span>
          </div>
        </div>
      </div>
    </div>

  </div>

</template>

<script setup>
import { ref ,onMounted  } from 'vue';

import request from "@/utils/request";
import { useRouter } from 'vue-router'
import { useRoute } from "vue-router";
const route=useRoute();
const searchQuery1=route.params.searchQuery;
const router = useRouter();
const shortDramas = ref([]);
const searchQuery = ref(searchQuery1);
const showDropdown = ref('');
const  videoData=ref([]);
const username = ref("User"); // 你可以根据你的需要设置默认用户名





const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value; // 切换下拉菜单的显示状态
};

const isLoggedIn = () => {
  return !!username.value; // 根据用户是否存在判断用户是否已登录
};

const logout = () => {
  // 这里添加注销登录的逻辑，比如清除用户信息等
  console.log('注销登录');
};

//根据搜索框输入的名字来查询短剧
const search = async () => {
  try {
    const res = await request.get('/details/search', { params: { name: searchQuery.value } });
    shortDramas.value = res.data.data;

  } catch (error) {
    console.error('Error searching:', error);
  }
}

//默认的剧
const  acquiesce = async (classify) =>{
  try {
    const res=await request.get('/details/selectAcquiesce',{params: {classify: classify}})
    videoData.value=res.data.data

  }catch (error){
    console.error('Error searching:', error);
  }
}

// const  loadDetails =async ()=>{
//   try {
//     const  res=await request.get('/details/finAllName',{params: {name: searchQuery.value}})
//     details.value=res.data.data
//     console.log("=====----",details.value)
//   }catch (error){
//     console.error('Error searching:', error);
//   }
// }

acquiesce('逆袭')
// 在组件加载后执行搜索
onMounted(() => {
  search(); // 页面加载时执行搜索

});
const goToDramaDetail = (dramaId,name) => {
  // Navigate to the detail page and pass drama ID as route parameter
  router.push({ name: 'videoDetail', params: { id: dramaId ,name: name} });
};

</script>


<style scoped>
/* 添加样式，确保分页组件能够正确显示 */
.pagination-container {
  margin: 20px auto; /* 居中显示 */
  text-align: center; /* 文字居中 */
}
.details-s{

  margin-right: 61px;
  margin-top: -129px;
  margin-left: 130px;
}
.details-s button{
  margin-bottom: 30px;
  margin-right: 10px;
  font-size: 14px;
  color: #e1442e;
  font-weight: 400;
  border: 0px solid #000; /* 宽度为2像素的实线边框，颜色为黑色 */
  width: 60px;
  height: 34px;
  background-color: rgba(255, 107, 58, .1);
}
.details-s p{
  color: black;
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 39px;
  margin-top: 130px;
}
.details-s span{
  color: #666666;
  font-size: 20px;
}
.drama-details {
  display: flex; /* 使用 flex 布局，使封面图像和详情页水平排列 */
  justify-content: center; /* 水平居中对齐 */
  align-items: center; /* 垂直居中对齐 */
  height: 264px; /* 设置高度与封面图像相同 */
  margin-right: 2px;
}
.details-p p{
  color: whitesmoke;
}

.image-wrapper {
  width: 184px; /* 调整宽度 */
  height: 264px;
  margin-right: 20px; /* 调整封面图像和详情之间的间距 */
}


.drama-card {
  margin-bottom: 40px; /* 添加底部间距 */
}

.drama-image{
  border-radius: 10px;
  width: 184px;
  height: 264px;
  margin-left: 120px;

}

.drama-section{
  background-color: white;
  margin-top: -100px;
}
.search-page {
  margin: -8px;
  padding: 200px;
  background-color: whitesmoke;
}

input {
  padding: 5px;
  margin-right: 10px;
}

button {
  padding: 5px 10px;
  cursor: pointer;
}
.search-containers {
  display: flex;
}

.search-containers input {
  padding: 5px;
  margin-right: 10px;
  margin-top: -25px;
  height: 40px;
  width: 400px;
  margin-left: -350px;
  border: 1px solid #ccc; /* 添加边框样式 */
  font-family: '宋体', Arial, sans-serif; /* 设置搜索框文字为宋体 */
}

.search-containers button {
  width: 51px;
  margin-top: -25px;
  padding: 5px;
  background-color: white;
  color: white;
  cursor: pointer;
  height: 51px;
  border: 1px solid #ccc; /* 添加边框样式 */
  font-family: '宋体', Arial, sans-serif; /* 设置搜索框文字为宋体 */

}
.search-containers button:hover {
  background-color: #ff6600; /* 悬停时的颜色变化 */
}

.search-containers img {
  width: 25px; /* 使图片填充按钮 */
  height: 25px;
}
.nav-list {
  position: fixed;
  top: -16px;
  left: -2px;
  width: 100%;
  list-style: none;
  padding: 5px;
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
