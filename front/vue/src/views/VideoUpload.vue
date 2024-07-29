<template>
  <div class="outer-container" style="background-color: white;">
    <div class="container" style="min-height: 100%; padding-bottom: 100px;">
     
        <el-header style="width: 100%;">
          <div class="nav-list">
            <navigation/>
          </div>
        </el-header>
    <h1>上传视频</h1>
    <form @submit.prevent="submitForm">
      <div>
        <label for="title">剧名:</label>
        <input type="text" v-model="title" id="title" required />
      </div>

      <div>
        <label for="description">描述详情:</label>
        <textarea v-model="description" id="description" required></textarea>
      </div>

      <div>
        <label for="videos">视频上传:</label>
        <input type="file" @change="handleVideoUpload" id="videos" multiple required />
      </div>

      <div>
        <label for="cover">封面上传:</label>
        <input type="file" @change="handleCoverUpload" id="cover" required />
      </div>

      <div>
        <label for="type">类型:</label>
        <select v-model="type" id="type" required>
          <option value="逆袭 亲情 现实 豪门恩怨 家庭">逆袭 亲情 现实 豪门恩怨 家庭</option>
          <option value="逆袭 甜宠 复仇">逆袭 甜宠 复仇</option>
          <option value="虐恋 豪门恩怨">虐恋 豪门恩怨</option>
          <option value="虐恋 豪门恩怨 复仇">虐恋 豪门恩怨 复仇</option>
          <option value="闪婚 欢喜冤家 时空之旅">闪婚 欢喜冤家 时空之旅</option>
          <option value="韩剧">韩剧</option>
          <option value="日剧">日剧</option>
          <option value="大陆剧<">大陆剧</option>
          <!-- 其他类型选项 -->
        </select>
      </div>

      <div>
        <label for="actors">主演:</label>
        <input type="text" v-model="actors" id="actors" required />
      </div>

      <button type="submit">提交</button>
    </form>
</div>
</div>
</template>

<script setup>
import { ref } from 'vue';
import navigation from '@/components/navigation.vue';
import request from '@/utils/request.js'
const title = ref('');
const description = ref('');
const videos = ref([]);
const cover = ref(null);
const type = ref('');
const actors = ref('');
const userName=localStorage.getItem('userName')
const handleVideoUpload = (event) => {
  videos.value = Array.from(event.target.files);
};

const handleCoverUpload = (event) => {
  cover.value = event.target.files[0];
};

const submitForm = async () => {
  const formData = new FormData();
  formData.append('title', title.value);
  formData.append('description', description.value);
  videos.value.forEach((video) => {
    formData.append('files', video);
  });
  formData.append('username',userName)
  formData.append('type',type.value)
  formData.append('actors', actors.value);
  formData.append('cover', cover.value);
  try {
    const response = await request.post('/file/uploadList', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    console.log('上传成功:', response);
  } catch (error) {
    console.error('上传失败:', error);
  }
};
</script>

<style scoped>
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
form {
  display: flex;
  flex-direction: column;
  max-width: 600px;
  margin: auto;
}

div {
  margin-bottom: 1rem;
}

label {
  margin-bottom: 0.5rem;
}

input, textarea, select {
  width: 100%;
  padding: 0.5rem;
  font-size: 1rem;
}
</style>


   