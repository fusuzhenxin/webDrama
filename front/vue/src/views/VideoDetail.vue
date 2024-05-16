<template>
    <div class="container" v-if="videoData" style="min-height: 100%; padding-bottom: 100px;background-color: black;">
      <el-container>
        <el-header style="height: 60px;">
          <div class="nav-list">
            <navigation/>
          </div>

        </el-header>
        <el-container>
          <el-aside width="1000px">
            <div class="video-wrapper">
              <div class="video-container">
                <video ref="videoPlayer" controls class="custom-video" >
                  <source :src="videoData.url" type="video/mp4">
                  Your browser does not support the video tag.
                </video>
              </div>
            </div>
          </el-aside>
          <el-container>
            <el-main width="382px">
                <el-text><h1 style="color: white;font-size: 25px;margin-top: -5px;">{{ Descriptions && Descriptions.name }}</h1></el-text>
                <el-text><h1 style="color: white;margin-bottom: 25px;font-size: 17px">主演：{{ Descriptions && Descriptions.actors }}</h1></el-text>

              <el-button  type="" plain style="margin-bottom: 40px;" v-for="(category, index) in Descriptions && Descriptions.classify.split(' ')" :key="index" >
                {{ category }}
              </el-button>
              <p style="color: darkgray">{{Descriptions && Descriptions.description}}</p>

              <div class="like-favorite-container">
                  <div class="like-container">
                      <el-button class="like-button" @click="Likes">{{ isLiked ? '取消点赞' : '点赞' }}</el-button>
                      <span class="like-count">{{Descriptions && Descriptions.quantity}}</span>
              </div>
              <div class="favorite-container">
                  <el-button class="favorite-button" @click="Collect()">{{ isCollect ? '取消收藏' : '收藏' }}</el-button>
                  <span class="favorite-count">{{Descriptions && Descriptions.collect}}</span>
              </div>
              </div>
              <div style="display: flex; justify-content: space-between;">
                    <el-text> <h2 style="color: white">选集</h2></el-text>
                      <el-text><h4 style="color: #8a8b8d;margin-right: 260px;margin-top: 30px">共{{numberOfEpisodes}}集</h4></el-text>
              </div>

              <div class="button-bottom-half">  <!-- 上半部分 -->
                <div class="button-inner-container">
                  <el-button v-for="(episode, index) in displayedEpisodes" :key="index" @click="loadEpisode(index+1)" class="episode-button">
                    {{ episode }}
                  </el-button>
                </div>
              </div>

              <div class="pagination-top-half"> <!-- 下半部分 -->
                <!-- 这里放其他内容 -->
                <div class="pagination">
                  <el-button v-for="page in totalPages" :key="page" @click="setCurrentPage(page)" :class="{ active: page === currentPage }">{{ page }}</el-button>
                </div>
              </div>
            </el-main>
            <el-footer>

            </el-footer>
          </el-container>
        </el-container>
      </el-container>
    </div>
  </template>

  <script setup>
import Navigation from "@/components/navigation.vue";
import { ref, onMounted,computed } from 'vue';
import request from "@/utils/request";
import { useRoute,useRouter } from 'vue-router';

const videoData = ref('');
const route = useRoute();
const router=useRouter();

const videoName = ref('');

console.log("----",videoName.value)
console.log("=====",videoName);
const videoPlayer = ref(null);
const numberOfEpisodes = ref(0);
const  Descriptions= ref(null)
const episodesPerPage = 25;
const currentPage = ref(1);
const isLiked = ref(false);
const isCollect=ref(false);
 //第一集url和短剧的信息
 const loadVideoData = async () => {
    try {
      const res = await request.get('/file/Inception',{params: {name: videoName.value} });
      // const res = await request.get(`/file/${videoId}`);
      videoData.value = res.data.data;
      console.log("[[[",videoData.value)
    } catch (error) {
      console.error('Error loading video data:', error);
    }
  };
  const loadDescription =async () => {
  try {
    const res=await request.get('/details/finAll', {params: {name: videoName.value} })
    Descriptions.value=res.data.data
    console.log("res.data.data----------",res.data.data)
  }catch (error){
    console.error('Error loading number of episodes:', error);
  }
}

//获取这个剧有多少集
const loadNumberOfEpisodes = async () => {
  try {
    const res = await request.get('/videos/diversity', { params: { name: videoName.value } });
    numberOfEpisodes.value = res.data.data;
  } catch (error) {
    console.error('Error loading number of episodes:', error);
  }
};

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
    const res = await request.get(`/videos/${videoName.value}/episode/${actualEpisodeNumber}`);
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


//点赞数量
const Likes = async () => {
         
console.log();
  if (!isLiked.value) {
    // 如果当前未点赞，则进行点赞操作
    // 发送点赞请求...
    // 更新点赞数量...
    const res = await request.get('/details/likes',{params: { name: videoName.value}});
    Descriptions.value.quantity=res.data.data
    console.log("-------", res.data.data)
    isLiked.value = true;
  } else {
    // 如果当前已经点赞，则取消点赞操作
    // 发送取消点赞请求...
    // 更新点赞数量...
    const res = await request.get('/details/likesDelete',{params: { name: videoName.value}});
    Descriptions.value.quantity=res.data.data
    console.log("-------", res.data.data)
    isLiked.value = false;
  }

};

//收藏数量
const Collect = async () => {
  if (!isCollect.value){
    const res = await request.get('/details/collect',{params: { name: videoName.value}});
    Descriptions.value.collect=res.data.data
    console.log("-------", res.data.data)
    isCollect.value=true
  }else {
    const res = await request.get('/details/collectDelete',{params: { name: videoName.value}});
    Descriptions.value.collect=res.data.data
    isCollect.value=false
  }

};
// 在路由变化时获取视频名称并存储到 localStorage 中
router.afterEach(() => {
  videoName.value = route.params.name;
  localStorage.setItem('videoName', videoName.value);
});
  onMounted(() => {
    videoName.value = localStorage.getItem('videoName');
    console.log("localStorage",videoName.value)
    loadVideoData();
    loadDescription();
    loadNumberOfEpisodes();

  });


  </script>

  <style scoped>
  .nav-list {
    position: fixed;
    top: -0px;
    left: 0px;
    width: 100%;
    height: 30px;
    list-style: none;
    background-color: black;
    z-index: 1000;
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
.button-bottom-half {
  flex: 1;
}
.button-inner-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-bottom: 10px;
  width: calc(100% - 20px); /* 设置按钮宽度为容器宽度的 20%，减去间距 */
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
.episode-button {

  height: 50px;
  background-color: #333333;

  color: white;
  text-align: center;
  font-size: 16px;
  cursor: pointer;
  padding: 20px; /* 增加按钮内边距 */
  line-height: 1; /* 让文本垂直居中 */
  margin-left: 5px;
  border-radius: 2px;
}
.pagination-top-half {
  flex: 1;
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

  </style>
