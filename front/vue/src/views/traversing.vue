<template>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;">
    <el-container>
      <el-header style="padding: 0px">
        <div class="nav-list">
          <navigation/>
        </div>
      </el-header>
      <el-main style="background-color: whitesmoke">
        <div class="drama-sections">

          <br>
          <h1>逆袭</h1>
          <div class="drama-section">
            <br>
            <div class="drama-list">
              <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.id,drama.name)">
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
          </div>
        </div>
      </el-main>
      <el-footer>Footer</el-footer>
    </el-container>
  </div>
</template>
<script setup>
import Navigation from "@/components/navigation.vue";

import { ref } from 'vue';
import request from "@/utils/request";
import { useRouter } from 'vue-router';
import '@fortawesome/fontawesome-free/css/all.css'; // 在 JavaScript 文件中引入


// Reactive data for short dramas
const shortDramas = ref([]);
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
loadShortDramas("穿越");

// Router instance
const router = useRouter();

// Method to navigate to drama detail page
const goToDramaDetail = (dramaId,name) => {
  console.log("---",name);
  console.log("idid ",dramaId);
  // Navigate to the detail page and pass drama ID as route parameter
  router.push({ name: 'videoDetail', params: { id: dramaId ,name: name} });
};

</script>

<style scoped>
/* CSS styles for the drama section */
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
.drama-sections{
  width: 1100px;
  height: auto;
  justify-items: center;
  background-color: white;
  border-bottom: 10px;

}
h1 {
  margin: 40px 0 0;
  padding: 30px;
}

.drama-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;

}

.drama-card {
  width: calc(20% - 20px);
  margin-left: 10px;
  margin-bottom: 20px;
  padding: 10px;
  box-sizing: border-box;
}

.drama-card .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */
  width: 175px;
  height: 255px;
  overflow: hidden;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.drama-card img {
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
