<template>
    <div class="common-layout">
        <div class="outer-container" style="background-color: #fafafa;">
            <div class="container" style="min-height: 100%; padding-bottom: 100px;">
      <el-container>
        <el-header style="width: 100%;">
            <div class="nav-list">
            <navigation/>
            </div>
        </el-header>
        <div style="margin-top: 30px;">
        <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>
        <a :href="indicate">{{ sort }}</a>
    
      </el-breadcrumb-item>
      <el-breadcrumb-item>
        <a>{{ videoName }}</a>
    </el-breadcrumb-item>
    </el-breadcrumb>
    </div>
        <el-container style="margin-top: 20px; width:1100px">
          <el-aside width="810px">
            <el-card class="box-card">
           
                <p style="font-weight: bold; font-size: 20px;">{{ Description.name }}</p>
                <div style="display: flex;align-items: center;margin-top: -25px;margin-left: 15px;">
                <p>{{  Description && Description.updateTime }}</p>
                <p style="margin-left: 30px;">浏览：{{ Description && Description.browse }}</p>
                <p style="margin-left: 30px;">来源：{{ Description && Description.source }}</p>
                </div>
                <p class="description-container">{{ getSubstringBeforeQuestionMark(Description && Description.description) }}</p>
                <img :src="Description && Description.cover" alt="Placeholder Image" class="card-image">
                <div class="card-content">
            
                  <p class="description-container">{{ getSubstringAfterFirstPeriod(Description && Description.description) }}</p>
                  </div>

          </el-card>

          <el-row>
            <el-row :gutter="20">
      <el-col :span="16">
        <div class="grid-content bg-purple">
            <el-card class="box-card4">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">猜你喜欢</h3>
                </div>
                <div class="drama-list">
          <div v-for="(drama, index) in shortDramas.slice(0, 10)" :key="index" class="drama-card" @click="goToDramaDetail(drama.detailsId,drama.name)">
            <div class="image-wrapper">
              <img :src="drama.cover" :alt="drama.name" class="drama-image">
              <!-- 调整播放按钮样式 -->
              <div class="play-button">
                <i class="fas fa-play"></i>
              </div>
            </div>
            <p style="color: black;">{{ drama.name }}</p>
            <p style="color: #b8b8b8;font-size: 12px;margin-top: -10px;">{{ drama.classify }}</p>
          </div>
        </div>
            </el-card>
        </div>
      </el-col>

    </el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
            <el-card class="box-card1">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">为你推荐</h3>
                </div>

                <div v-for="(drama, index) in News" :key="index" class="drama-card1" @click="goToNewsDetail(drama.id, drama.name)">
        <el-row style="margin-bottom: 100px">
          <el-col :span="3">
            <div class="grid-content bg-purple" style="width: 400px" >
              <!-- 封面图像 -->
              <div class="image-wrapper">
                <img :src="drama.cover" :alt="drama.name" class="drama-image">
              </div>
            </div>
          </el-col>
          <el-col :span="21">
            <div class="grid-content bg-purple-light" style="text-align: lef;margin-left: 100px;">
              <h1 style="margin-bottom: 10px;font-size: 20px;margin-top: 0px;">{{ drama.name }}</h1>
              <span style="margin-top: -50px;">{{ truncateString(drama.description, 70) }}</span>
              <div style="display: flex;align-items: center;">
              <p>{{ drama.source }}</p>
              <p style="margin-left: 20px;">{{ drama.updateTime }}</p>
              </div>
              </div>
          </el-col>

        </el-row>
        </div>
            </el-card>
        </div>
        
      </el-col>
    </el-row>
        </el-aside>
          <el-main>
            <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
            <el-card class="box-card5">
                <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">热门资讯</h3>
             </div>
             <div v-for="(drama, index) in browseDetails.slice(0,7)" :key="index" class="drama-card2" @click="goToNewsDetail(drama.id, drama.name)">
        <el-row style="margin-bottom: 100px">
          <el-col :span="3">
            <div class="grid-content bg-purple" style="width: 10px" >
              <!-- 封面图像 -->
              <div class="image-wrapper">
                <img :src="drama.cover" :alt="drama.name" class="drama-image">
              </div>
            </div>
          </el-col>
          <el-col :span="21">
            <div class="grid-content bg-purple-light" style="text-align: lef;margin-left: 100px;">
              <span style="margin-top: -50px;font-size: 13px;">{{ truncateString(drama.description, 20) }}</span>
              <div style="display: flex;align-items: center;">
                <p style="font-size: 13px;">{{ formatDate(drama.updateTime) }}</p>
              </div>
              </div>
          </el-col>

        </el-row>
        </div>
            </el-card>
        </div>
        
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
            <el-card class="box-card6">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">正在播出</h3>
                </div>
                <div v-for="(drama,index) in loadFindCollectTopDetails" :key="index" class="drama-card10"  @click="gotoCollectTop(drama.name, drama.id)">
                <span>{{ drama.name }}</span>
               </div>
            </el-card>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
            <el-card class="box-card7">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">热门视频</h3>
                </div>
                      <!-- 单独处理第一个元素 -->
      <div v-if="loadFindTop10Details.length > 0">
        <div  @click="gotoloadFindTop10(loadFindTop10Details[0].name,loadFindTop10Details[0].id)">
          <div style="display: flex; align-items: center;margin-bottom: 14px;">
            <i class="num">1</i>
            <img :src="loadFindTop10Details[0].cover" :alt="loadFindTop10Details[0].name" class="drama-image" style="margin-left: 10px; margin-right: 10px; width: 95px; height: 140px;margin-top: -6px;">
            <span style="color: black; font-size: 20px; margin-left: 10px; margin-top: 18px; height: 40px;">{{ loadFindTop10Details[0].name }}</span>
          </div>
        </div>
        <hr style="background-color: black;">
      </div>
      <!-- 循环处理剩余元素 -->
      <div v-for="(drama, index) in loadFindTop10Details.slice(0,6)" :key="index">
        <div class="drama-card3" @click="gotoloadFindTop10(drama.name,drama.id)">
          <div style="display: flex; align-items: center;">
            <p style="color: #aaa; font-weight: 700; font-size: 20px; margin-left: 5px; margin-top: 8px;">{{ index + 2 }}</p>
            <span style="color: black; font-size: 20px; margin-left: 10px; margin-top: -4px; height: 40px;">{{ drama.name }}</span>
          </div>
        </div>
        <hr v-if="index < loadFindTop10Details.slice(1).length - 1" style="background-color: black;">
      </div>
            </el-card>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
            <el-card class="box-card8">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">最新资讯</h3>
                </div>
                <div v-for="(drama, index) in findUpdateTimePage.slice(0,7)" :key="index" class="drama-card2" @click="goToNewsDetail(drama.id, drama.name)">
        <el-row style="margin-bottom: 100px">
          <el-col :span="3">
            <div class="grid-content bg-purple" style="width: 10px" >
              <!-- 封面图像 -->
              <div class="image-wrapper">
                <img :src="drama.cover" :alt="drama.name" class="drama-image">
              </div>
            </div>
          </el-col>
          <el-col :span="21">
            <div class="grid-content bg-purple-light" style="text-align: lef;margin-left: 100px;">
              <span style="margin-top: -50px;font-size: 13px;">{{ truncateString(drama.description, 20) }}</span>
              <div style="display: flex;align-items: center;">
              <p style="margin-top: 5px;font-size: 13px;">浏览:{{ drama.browse }}</p>
              
              </div>
              </div>
          </el-col>

        </el-row>
        </div>
            </el-card>
        </div>
      </el-col>
    </el-row>
          </el-main>
        </el-container>
      </el-container>
      </div>
      </div>
    </div>
  </template>

<script setup>
import request from "@/utils/request";
import { ref,onMounted,watch } from 'vue';
import Navigation from "@/components/navigation.vue";
const Description = ref('');
const videoName=ref('')
const sort=ref('')
const indicate=ref('')
const numberOfEpisodes=ref('')
const shortDramas=ref('')
const News=ref('')
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
const route=useRoute()
const router=useRouter()
const pageNum= ref(1);
const pageSize= ref(10);
const browseDetails=ref([])
const findUpdateTimePage=ref([])
const loadFindTop10Details=ref([])
const loadFindCollectTopDetails=ref([])
const formatDate=(dateString)=> {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // 补零
        const day = ('0' + date.getDate()).slice(-2); // 补零
        return `${year}-${month}-${day}`;
    }

const browsePage = async () => {
  try {
    const res = await request.get('/news/browsePage', { params: { pageSize: pageSize.value,pageNum: pageNum.value} });
    browseDetails.value = res.data.data.records;
    console.log("================================",res.data.data);
  } catch (error) {
    console.error('Error searching:', error);
  }
}
const updateTimePage = async () => {
  try {
    const res = await request.get('/news/updateTimePage', { params: { pageSize: pageSize.value,pageNum: pageNum.value} });
    findUpdateTimePage.value = res.data.data.records;
    console.log("================================",res.data.data);
  } catch (error) {
    console.error('Error searching:', error);
  }
}
const initVideoName = () => {
  if (route.params.name) {
    videoName.value = route.params.name;
    sort.value=route.params.sort;
    indicate.value=route.params.indicate
    console.log("------",indicate.value);
    localStorage.setItem('videoName', videoName.value);
    localStorage.setItem('sort', sort.value);
    localStorage.setItem('indicate', indicate.value);
  } else {
    videoName.value = localStorage.getItem('videoName');
    sort.value = localStorage.getItem('sort');
    indicate.value = localStorage.getItem('indicate');
  }
};
//正在播出
const loadFindCollectTop10=async()=>{
  const res = await request.get('/news/collectTop10')
  loadFindCollectTopDetails.value=res.data.data
}
const getSubstringBeforeQuestionMark = (str) => {
    const index = str.indexOf('。');
    return index !== -1 ? str.substring(0, index) : str;
};
const getSubstringAfterFirstPeriod = (str) => {  
const index = str.indexOf('。');
return index !== -1 ? str.substring(index + 1) : str;
};


//限制字数
const truncateString = (str, maxLength) => {
  if (!str) return "";
  if (str.length <= maxLength) {
    return str;
  } else {
    return str.slice(0, maxLength) + "...";
  }
};
const search = async () => {
  try {
    const res = await request.get('/news/page', { params: { pageSize: pageSize.value,pageNum: pageNum.value} });
    News.value = res.data.data.records;

    console.log("================================",res.data.data.records);
  } catch (error) {
    console.error('Error searching:', error);
  }
}
onMounted(() => {
  search(); // 页面加载时执行搜索
  browsePage();
  updateTimePage()
  loadFindCollectTop10()
});
const goToDramaDetail=async(dramaId,name)=>{

  router.push({ name: 'VideoStory', params: { id: dramaId ,name: name,sort: sort.value,indicate:indicate.value}});
}
//正在播出
const gotoCollectTop=async(name,id)=>{
  router.push({name:'VideoStory',params:{id: id,name: name,sort: sort.value,indicate: indicate.value}})
}
const goToNewsDetail=async(dramaId,name)=>{

router.push({ name: 'NewsDetails', params: { id: dramaId ,name: name,sort: sort.value,indicate:indicate.value}});
}
//热门视频
const gotoloadFindTop10=(name,id)=>{
  router.push({ name: 'VideoStory', params: { id: id ,name: name,sort: sort.value,indicate:indicate.value}});
}
const fetchData = async () => {
  initVideoName();
  if (videoName.value) {
    await loadDescription();
    await loadNumberOfEpisodes();
  } else {
    console.error('No video name provided');
  }
};

watch(() => route.params.name, fetchData);

onMounted(fetchData);
onMounted(()=>{
  loadFindTop10()
})
//一个多少集
const loadNumberOfEpisodes = async () => {
  try {
    const res = await request.get('/videos/diversity', { params: { name: videoName.value } });
    numberOfEpisodes.value = res.data.data;
  } catch (error) {
    console.error('Error loading number of episodes:', error);
  }
};
//根据新闻标题获取内容
const loadDescription = async () => {
  try {
    const res = await request.get('/news/finAll', { params: { name: videoName.value } });
    Description.value = res.data.data;
   
  } catch (error) {
    console.error('Error loading description:', error);
  }
};
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

loadShortDramas('逆袭')
//热门视频
const loadFindTop10 =async()=>{
  const  res=await request.get('/news/top10')
  loadFindTop10Details.value=res.data.data
}
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
.card-image{
    width: 760px;
    height: 450px;
    
}
.box-card{
    width: 800px;
    height: auto;
    border-radius: 0px;
    box-shadow: none;
}
.box-card1{
    width: 800px;
    height: auto;
    margin-top: 30px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card2{
    width: 800px;
    height: auto;
    margin-top: 30px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card3{
    width: 800px;
    height: 450px;
    margin-top: 30px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card4{
    width: 800px;
    height: auto;
    margin-top: 30px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card5{
    width: 282px;
    height: 820px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card6{
    width: 282px;
    height: 420px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    margin-top: 40px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card7{
    width: 282px;
    height: 600px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    margin-top: 40px;
    border-radius: 0px;
    box-shadow: none;
}
.box-card8{
    width: 282px;
    height: 800px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    margin-top: 40px;
    border-radius: 0px;
    box-shadow: none;
}
.episode-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px; /* 间距 */
}
.episode-card {
  flex: 0 0 calc(10% - 10px); /* 10% 减去间距 */
  text-align: center;
  padding: 10px;
  background-color: whitesmoke;
  border-radius: 4px;
  cursor: pointer;
}
.drama-list {
  width: 770px;
  height: auto;
  margin-left: 0px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;

}
.drama-card .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */
  width: 136px;
  height: 190px;
  overflow: hidden;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}
.drama-card {
  width: calc(17% - -16px);
  margin-left: 5px;
  margin-bottom: 10px;
  padding: 0px;
  box-sizing: border-box;
}
.drama-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
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
.custom-textarea .el-textarea__inner {
  background-color: black; /* 设置你想要的背景颜色 */
  color: black; /* 可选：设置文本颜色 */
}
.card-content{
  margin-top: -10px;
    margin-left: 0px;
}
.card-image{
    justify-items: center;
    align-items: center;
}
.drama-card1 .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */

  overflow: hidden;
  border-radius: 5px;
  border-radius: 0px;
}
.drama-card1 img {
    width: 178px;
    height: 140px;
  object-fit: cover;
}
.drama-card1{
    
    margin-bottom: -80px;
    position: relative; /* 确保伪元素的定位基于 .drama-card */
}

.drama-card1:not(:first-child)::after {
    content: "";
    display: block;
    width: 100%;
    height: 1px; /* 线条的高度 */
    background-color: whitesmoke; /* 线条的颜色 */
    position: absolute;
    bottom: 160px; /* 根据 margin-bottom 调整线条位置 */
    margin-bottom: 0px;
}
.drama-card2 img {
    width: 120px;
    height: 90px;
  object-fit: cover;
}
.drama-card2{
    
    margin-bottom: -90px;
    position: relative; /* 确保伪元素的定位基于 .drama-card */
}
/* 热门视频 */
.drama-card3{
  height: 40px;
  margin-bottom: 10px;
  text-align: center;
  
}
/* 第一名的标志 */
.num{
  line-height: 29px;
    text-align: center;
    width: 36px;
    height: 35px;
    background-color: #ff443f;
    color: #fff;
    position: absolute;
    margin-left: 10px;
    margin-top: -111px;
}
/* 正在播出 */
.drama-card10 {
  cursor: pointer;
  padding: 10px;
 
}

/* 使用 :nth-child 伪类选择器来实现斑马线效果 */
.drama-card10:nth-child(odd) {
  background-color: #f9f9f9; /* 浅色背景 */
}

.drama-card10:nth-child(even) {
  background-color: #ffffff; /* 白色背景 */
}

.drama-card10:hover {
  background-color: #e0e0e0; /* 悬停时的背景色 */
}
</style>