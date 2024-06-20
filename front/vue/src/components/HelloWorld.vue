<template>
  <br>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;background-color: black;">
    <el-container>
      <el-header>
        <h1 style="color: white;margin-left: -20px;font-size: 30px;padding: 0px;margin-bottom: 30px;margin-top: 0px;">推荐热门</h1>
        <div class="drama-list">
          <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.detailsId,drama.name)">
            <div class="image-wrapper">
              <img :src="drama.cover" :alt="drama.name" class="drama-image">
              <!-- 调整播放按钮样式 -->
              <div class="play-button">
                <i class="fas fa-play"></i>
              </div>
            </div>
            <p style="color: white;">{{ drama.name }}</p>
            <p style="color: hsla(0, 0%, 100%, .5);font-size: 14px;">{{ drama.classify }}</p>
          </div>
        </div>
      </el-header>
    </el-container>
   <div>

      <el-main style="margin-top: 700px;padding: 0px;">
        <h1 style="color: white;margin-left: -30px;font-size: 30px;margin-top: 0px;">逆袭</h1>
        <div style="display: flex;">

    
        <div class="drama-list1">
          <div v-for="(drama, index) in VideoData" :key="index" class="drama-card1" @click="goToDramaDetail(drama.detailsId,drama.name)">
            <div class="image-wrapper">
              <img :src="drama.cover" :alt="drama.name" class="drama-image">
              <!-- 调整播放按钮样式 -->
              <div class="play-button">
                <i class="fas fa-play"></i>
              </div>
            </div>
            <p style="color: white;">{{ drama.name }}</p>
            <p style="color: hsla(0, 0%, 100%, .5);font-size: 14px;">{{ drama.classify }}</p>
          </div>
        </div>
        <el-row>
          <el-col :span="12">
  <div class="grid-content bg-purple">
    <el-card class="box-card5">
      <el-text style="margin-top: -25px; font-size: 28px; color: #222; margin-left:10px; color: white;">周排行榜</el-text>
      <hr style="border: 0; border-top: 1px solid rgba(255, 255, 255, 0.3);">
      <!-- 单独处理第一个元素 -->
      <div v-if="loadFindTop10Details.length > 0">
        <div  @click="gotoloadFindTop10(loadFindTop10Details[0].name,loadFindTop10Details[0].detailsId)">
          <div style="display: flex; align-items: center;margin-bottom: 40px;">
            <i class="num">1</i>
            <img :src="loadFindTop10Details[0].cover" :alt="loadFindTop10Details[0].name" class="drama-image" style="margin-left: 10px; margin-right: 10px; width: 130px; height: 200px;margin-top: 15px;">
            <span style="color: #fff; font-size: 20px; margin-left: 10px; margin-top: 18px; height: 40px;">{{ loadFindTop10Details[0].name }}</span>
          </div>
        </div>
        <hr style="border: 0; border-top: 1px solid rgba(255, 255, 255, 0.3);">
      </div>
      <!-- 循环处理剩余元素 -->
      <div v-for="(drama, index) in loadFindTop10Details.slice(1)" :key="index">
        <div class="drama-card2" @click="gotoloadFindTop10(drama.name,drama.detailsId)">
          <div style="display: flex; align-items: center;">
            <p style="color: #aaa; font-weight: 700; font-size: 25px; margin-left: 5px; margin-top: 18px;">{{ index + 2 }}</p>
            <span style="color: #fff; font-size: 20px; margin-left: 10px; margin-top: 3px; height: 40px;">{{ drama.name }}</span>
          </div>
        </div>
        <hr v-if="index < loadFindTop10Details.slice(1).length - 1" style="border: 0; border-top: 1px solid rgba(255, 255, 255, 0.3);">
      </div>
      <hr style="border: 0; border-top: 1px solid rgba(255, 255, 255, 0.3);">
      <h1 style="text-align: center; margin-top: 0px; font-size: 23px; color: white; margin-left: -10px;">更多></h1>
    </el-card>
  </div>
</el-col>




    </el-row>
    </div>
      </el-main>
   </div>
  </div>

</template>

<script setup>
import { ref,onMounted } from 'vue';
import request from "@/utils/request";
import { useRouter } from 'vue-router';
import '@fortawesome/fontawesome-free/css/all.css'; // 在 JavaScript 文件中引入
const loadFindTop10Details=ref([])
const sort=ref('推荐热门')
  const indicate=ref('/')
// Reactive data for short dramas
const shortDramas = ref([]);
const VideoData = ref([]);
// Method to fetch short drama data asynchronously
const loadShortDramas = async (classify) => {
  try {
    // Fetch short drama data
    const res = await request.get('/details/selectTop10', { params: { classify: classify } });
    // If successful, update the short drama data
    shortDramas.value = res.data.data;
    console.log("====",shortDramas.value)
  } catch (error) {
    // Catch and handle errors
    console.error('Error loading short dramas:', error);
  }
};

const loadVideoData = async (classify) => {
  try {
    // Fetch short drama data
    const res = await request.get('/details/selectTopSix', { params: { classify: classify } });
    // If successful, update the short drama data
    VideoData.value = res.data.data;
    console.log("====",VideoData.value)
  } catch (error) {
    // Catch and handle errors
    console.error('Error loading short dramas:', error);
  }
};
loadShortDramas("重生");
loadVideoData("逆袭")

// Router instance
const router = useRouter();

// Method to navigate to drama detail page
const goToDramaDetail = (dramaId,name) => {
  request.post(`/news/click/${dramaId}`)
  // Navigate to the detail page and pass drama ID as route parameter
  router.push({ name: 'VideoStory', params: { id: dramaId ,name: name,sort: sort.value,indicate: indicate.value} });
  
};
const gotoloadFindTop10 =(name,detailsId) =>{
  request.post(`/news/click/${detailsId}`)
  console.log("/////",detailsId);
  router.push({name: 'VideoStory',params: {id: detailsId,name:name,sort: sort.value,indicate: indicate.value}})
}
const loadFindTop10 =async()=>{
  const  res=await request.get('/news/top10')
  loadFindTop10Details.value=res.data.data
  console.log("===loadFindTop10Details===",res.data.data); 
}

onMounted(()=>{
  loadFindTop10()
});
</script>

<style scoped>
/* CSS styles for the drama section */
.drama-sections{
  width: 1200px;
  height: auto;
  margin-left: 100px;
  border-style: solid;
}
h1 {
  margin: 40px 0 0;
  padding: 30px;
}

.drama-list {
  width: 1159px;
  height: auto;
  margin-left: -32px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-left: -24px;
}
.drama-list1 {
  width: 848px;
  height: auto;
  margin-left: -20px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-top: 10px;
  
}
.drama-card {
  width: calc(15% - -13px);
  margin-left: 5px;
  margin-bottom: 10px;
  padding: 0px;
  box-sizing: border-box;
}
.drama-card1 {
  width: calc(29% - -1px);
   margin-left: 10px;
   margin-bottom: 20px;
   padding: 10px;
   box-sizing: border-box;
 }

.drama-card .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */
  width: 184px;
  height: 266px;
  overflow: hidden;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.drama-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.drama-card1 .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */
  width: 280px;
  height: 410px;
  overflow: hidden;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.drama-card1 img {
  width: 95%;
  height: 100%;
  object-fit: cover;
}
/* 调整播放按钮样式 */
.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-wrapper:hover .play-button {
  opacity: 1;
}

.play-button i {
  color: white;
  font-size: 24px;
}
.box-card5{
  background-color: rgba(27, 25, 25, 0.7); /* 设置背景颜色透明度 */
    width: 300px;
    height: 1040px;
    margin-left: 50px;
    margin-top: 20px;
    --el-card-border-color: 0
    
}
.drama-card2{
  height: 40px;
  margin-bottom: 20px;
  text-align: center;
  
}
.num{
  line-height: 29px;
    text-align: center;
    width: 36px;
    height: 35px;
    background-color: #ff443f;
    color: #fff;
    position: absolute;
    margin-left: 10px;
    margin-top: -149px;
}
</style>
