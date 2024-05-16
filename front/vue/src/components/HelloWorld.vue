<template>
  <br>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;">
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
          </div>
        </div>
      </el-header>
    </el-container>
   <div>

      <el-main style="margin-top: 700px">
        <h1 style="color: white;margin-left: -30px;font-size: 30px;margin-top: 0px;">逆袭</h1>
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
          </div>
        </div>
      </el-main>
   </div>
  </div>

</template>

<script setup>
import { ref } from 'vue';
import request from "@/utils/request";
import { useRouter } from 'vue-router';
import '@fortawesome/fontawesome-free/css/all.css'; // 在 JavaScript 文件中引入


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
    const res = await request.get('/details/selectTop8', { params: { classify: classify } });
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
  // Navigate to the detail page and pass drama ID as route parameter
  router.push({ name: 'videoDetail', params: { detailsId: dramaId ,name: name} });
  
};

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
  width: 1190px;
  height: auto;
  margin-left: -32px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;

}
.drama-list1 {
  width: 860px;
  height: auto;
  margin-left: -40px;
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
   width: calc(32% - 17px);
   margin-left: 10px;
   margin-bottom: 20px;
   padding: 10px;
   box-sizing: border-box;
 }

.drama-card .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */
  width: 190px;
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
  width: 100%;
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
</style>
