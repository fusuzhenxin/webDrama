<template>

  <div class="container"  v-if="videoData">
    <div class="navigation-wrapper">
      <div class="common-layout">
        <el-container>
          <el-header>

            <nav>
              <ul class="nav-list">
                <li><h1 style="color: white;margin-right: 20px;margin-left: 26px">短剧网</h1></li>

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
        </el-container>
      </div>
    </div>
    <br>
    <br>
    <div class="video-wrapper">
      <div class="video-container">
        <video ref="videoPlayer" controls class="custom-video" >
          <source :src="videoData.url" type="video/mp4">
          Your browser does not support the video tag.
        </video>
      </div>
    </div>

    <div class="pagination-container">

      <h1 style="color: white">{{ videoData.name }}</h1>
      <p style="color: whitesmoke">主演：{{Descriptions && Descriptions.actors}}</p>
      <div>
        <button>{{ Descriptions && Descriptions.classify }}</button>
        <button style="margin-left: 10px">民国</button>
      </div>
      <div>
        <p style="color: darkgray">{{Descriptions && Descriptions.description}}</p>
      </div>

      <div class="like-favorite-container">
        <div class="like-container">
          <button class="like-button" @click="Likes">{{ isLiked ? '取消点赞' : '点赞' }}</button>
          <span class="like-count">{{Descriptions && Descriptions.quantity}}</span>
        </div>
        <div class="favorite-container">
          <button class="favorite-button" @click="Collect()">{{ isCollect ? '取消收藏' : '收藏' }}</button>
          <span class="favorite-count">{{Descriptions && Descriptions.collect}}</span>
        </div>
      </div>

      <br>

      <hr>
      <div style="display: flex; justify-content: space-between;">
        <div>
          <h2 style="color: white">选集</h2>
        </div>
        <div>
          <h4 style="color: #8a8b8d;margin-right: 240px;margin-top: 30px">共{{numberOfEpisodes}}集</h4>
        </div>
      </div>
      <div class="button-bottom-half">  <!-- 上半部分 -->
        <div class="button-inner-container">
          <button v-for="(episode, index) in displayedEpisodes" :key="index" @click="loadEpisode(index+1)" class="episode-button">
            {{ episode }}
          </button>
        </div>
      </div>

      <div class="pagination-top-half"> <!-- 下半部分 -->
        <!-- 这里放其他内容 -->
        <div class="pagination">
          <button v-for="page in totalPages" :key="page" @click="setCurrentPage(page)" :class="{ active: page === currentPage }">{{ page }}</button>
        </div>
      </div>
    </div>

  </div>

  <div v-else>
    Loading...
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import request from "@/utils/request";
import { useRoute } from 'vue-router';
// import Navigation from "@/views/navigation.vue";

const isLiked = ref(false);
const isCollect=ref(false);
const videoData = ref(null);
const videoPlayer = ref(null);
const numberOfEpisodes = ref(0);
const  Descriptions= ref(null)
const episodesPerPage = 25;
const currentPage = ref(1);
const route = useRoute();
const videoName = route.params.name;
console.log("idididid",videoName)
const showDropdown = ref(false);
const username = ref("User"); // 你可以根据你的需要设置默认用户名
const searchQuery = ref('')
import { useRouter } from "vue-router";
const  router = useRouter();
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value; // 切换下拉菜单的显示状态
};

const isLoggedIn = () => {
  return !!username.value; // 根据用户是否存在判断用户是否已登录
};
const search = () => {
  console.log('Search method called');
  // 如果搜索框有内容，执行路由跳转
  if (searchQuery.value.trim() !== '') {
    router.push({name: 'search',params:{searchQuery: searchQuery.value}}); // 这里替换为你要跳转的页面路径
  }
};
const logout = () => {
  // 这里添加注销登录的逻辑，比如清除用户信息等
  console.log('注销登录');
};
//第一集url和短剧的信息
const loadVideoData = async () => {
  try {
    const res = await request.get('/file/Inception',{params: {name: videoName} });
    // const res = await request.get(`/file/${videoId}`);

    videoData.value = res.data.data;
    console.log("[[[",videoData.value)
  } catch (error) {
    console.error('Error loading video data:', error);
  }
};

//每个剧的详情
const loadDescription =async () => {
  try {
    const res=await request.get('/details/finAll', {params: {name: videoName} })
    Descriptions.value=res.data.data
    console.log("res.data.data----------",res.data.data)
  }catch (error){
    console.error('Error loading number of episodes:', error);
  }
}

//获取这个剧有多少集
const loadNumberOfEpisodes = async () => {
  try {
    const res = await request.get('/videos/diversity', { params: { name: videoName } });
    numberOfEpisodes.value = res.data.data;
  } catch (error) {
    console.error('Error loading number of episodes:', error);
  }
};

//点赞数量
const Likes = async () => {
  if (!isLiked.value) {
    // 如果当前未点赞，则进行点赞操作
    // 发送点赞请求...
    // 更新点赞数量...
    const res = await request.get('/details/likes',{params: { name: videoName}});
    Descriptions.value.quantity=res.data.data
    console.log("-------", res.data.data)
    isLiked.value = true;
  } else {
    // 如果当前已经点赞，则取消点赞操作
    // 发送取消点赞请求...
    // 更新点赞数量...
    const res = await request.get('/details/likesDelete',{params: { name: videoName}});
    Descriptions.value.quantity=res.data.data
    console.log("-------", res.data.data)
    isLiked.value = false;
  }

};

//收藏数量
const Collect = async () => {
  if (!isCollect.value){
    const res = await request.get('/details/collect',{params: { name: videoName}});
    Descriptions.value.collect=res.data.data
    console.log("-------", res.data.data)
    isCollect.value=true
  }else {
    const res = await request.get('/details/collectDelete',{params: { name: videoName}});
    Descriptions.value.collect=res.data.data
    isCollect.value=false
  }

};


onMounted(() => {
  loadVideoData();
  loadNumberOfEpisodes();
  loadDescription();
});

const displayedEpisodes = computed(() => {
  const startIndex = (currentPage.value - 1) * episodesPerPage;
  const endIndex = Math.min(startIndex + episodesPerPage, numberOfEpisodes.value);
  return Array.from({ length: endIndex - startIndex }, (_, index) => startIndex + index + 1);
});

const totalPages = computed(() => Math.ceil(numberOfEpisodes.value / episodesPerPage));


const loadEpisode = async (episodeNumber) => {
  console.log("?????????", episodeNumber);
  const startIndex = (currentPage.value - 1) * episodesPerPage;
  const actualEpisodeNumber = startIndex + episodeNumber;
  try {
    const res = await request.get(`/videos/${videoName}/episode/${actualEpisodeNumber}`);
    const url = res.data.data;
    if (videoPlayer.value) {
      videoPlayer.value.src = url;
      videoPlayer.value.load();
      videoPlayer.value.play();
    }
  } catch (error) {
    console.error('Error loading episode:', error);
  }
};

const setCurrentPage = (page) => {
  currentPage.value = page;
};

</script>

<style scoped>
.container {
  /* 样式部分省略，保持不变 */
  /* 顶部导航栏样式 */
  .nav-list {
    position: fixed;
    top: -17px;
    left: 9px;
    width: 98%;
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

  /* 图片样式 */
  .logo-container {
    top: 0px;
    left: 0;
    padding: 0px;
    z-index: 1001; /* 确保图片在导航栏上方 */

  }

  .logo {
    margin-top: 73px;
    width: 1424px;
    height: 480px;
    margin-right: 0px;
    margin-left: -12px;
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
  position: relative;
  border: 2px solid #ccc;
  padding: 10px;
  background-color: black;
  margin-top: 50px;
  display: flex;

}
.navigation-wrapper {
  position: absolute;
  top: 10px; /* 调整 Navigation 的垂直位置 */
  left: 10px; /* 调整 Navigation 的水平位置 */
}
.large-frame{
  background-color: white;
}
.video-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid black; /* 添加按钮边框样式 */
}

.video-container {
  position: relative;
  flex: 1;
}

.custom-video {
  width: 1000px;
  height: 800px;
  background-color: white;
  background-color: rgba(255, 255, 255, 0.6); /* 白色背景，透明度为 0.5 */
}


.button-inner-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-bottom: 10px;
  width: calc(100% - 20px); /* 设置按钮宽度为容器宽度的 20%，减去间距 */
}
.episode-button {

  height: 50px;
  background-color: #333333;
  //border: 2px solid white; /* 添加按钮边框样式 */
  color: white;
  text-align: center;
  font-size: 16px;
  cursor: pointer;
  padding: 20px; /* 增加按钮内边距 */
  line-height: 1; /* 让文本垂直居中 */
  margin-left: 5px;
  border-radius: 2px;
}
.button-inner-container button {
  width: 100%;
  height: 50px;
  background-color: #333333;
  border: none;
  color: white;
  text-align: center;
  font-size: 16px;
  cursor: pointer;
}

.pagination-container {
  //border: 2px solid #ccc; /* 添加大边框样式 */
  padding: 13px; /* 添加内边距 */
  margin-bottom: 10px; /* 调整与按钮边框的间距 */
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

.pagination button {
  margin-right: 5px;
  background-color: #333;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
}

.pagination button.active {
  background-color: #555;
}
.pagination-top-half {
  flex: 1;
}

.button-bottom-half {
  flex: 1;
}

.like-favorite-container {
  border: 1px solid darkgray;
  display: flex;
  justify-content: space-between;
  padding: 10px;
  margin-top: 20px;
}

.like-container, .favorite-container {
  display: flex;
  align-items: center;
}

.like-button, .favorite-button {
  padding: 5px 10px;
  background-color: transparent;
  border: none;
  color: darkgray;
  cursor: pointer;
}

.like-count, .favorite-count {
  margin-left: 5px;
  color: white;
}

.like-button:hover, .favorite-button:hover {
  background-color: darkgray;
  color: white;
}

</style>
