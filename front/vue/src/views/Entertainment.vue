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
        <a :href="indicate">娱乐新闻</a>
    
      </el-breadcrumb-item>
      <el-breadcrumb-item>
        <a>列表</a>
    </el-breadcrumb-item>
    </el-breadcrumb>
    </div>
        <el-container style="margin-top: 20px; width:1100px">
          <el-aside width="810px">
            <el-card class="box-card">
                <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.id, drama.name)">
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
              <h1 style="margin-bottom: 10px;font-size: 20px;margin-top: 5px;">{{ drama.name }}</h1>
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
          <el-pagination
      style="justify-content: center;margin-top: 80px;margin-right: 120px;"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-size="pageSize"
      background layout="total, prev, pager, next, jumper"
      :total="total"
  ></el-pagination>
        </el-aside>


          <el-main>


            <el-card class="box-card5">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">热门资讯</h3>
             </div>
             <div v-for="(drama, index) in browseDetails.slice(0,7)" :key="index" class="drama-card1" @click="goToDramaDetail(drama.id, drama.name)">
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


    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
            <el-card class="box-card6">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">正在播出</h3>
                </div>
               <div v-for="(drama,index) in loadFindCollectTopDetails" :key="index" class="drama-card10"  @click="gotoCollectTop(drama.name, drama.detailsId)">
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
        <div  @click="gotoloadFindTop10(loadFindTop10Details[0].name,loadFindTop10Details[0].detailsId)">
          <div style="display: flex; align-items: center;margin-bottom: 14px;">
            <i class="num">1</i>
            <img :src="loadFindTop10Details[0].cover" :alt="loadFindTop10Details[0].name" class="drama-image" style="margin-left: 10px; margin-right: 10px; width: 95px; height: 140px;margin-top: -6px;">
            <span style="color: black; font-size: 20px; margin-left: 10px; margin-top: 18px; height: 40px;">{{ loadFindTop10Details[0].name }}</span>
          </div>
        </div>
        <hr style="background-color: black;">
      </div>
      <!-- 循环处理剩余元素 -->
      <div v-for="(drama, index) in loadFindTop10Details.slice(1)" :key="index">
        <div class="drama-card2" @click="gotoloadFindTop10(drama.name,drama.detailsId)">
          <div style="display: flex; align-items: center;">
            <p style="color: #aaa; font-weight: 700; font-size: 20px; margin-left: 5px; margin-top: 8px;">{{ index + 2 }}</p>
            <span style="color: black; font-size: 20px; margin-left: 10px; margin-top: -4px; height: 40px;">{{ drama.name }}</span>
          </div>
        </div>
        <hr v-if="index < loadFindTop10Details.slice(1).length - 1" style="background-color: black;">
      </div>
      <hr style="background-color: black;">
      <h1 style="text-align: center; margin-top: 0px; font-size: 20px; color: black; margin-left: -10px;">更多></h1>
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
             <div v-for="(drama, index) in findUpdateTimePage.slice(0,7)" :key="index" class="drama-card1" @click="goToDramaDetail(drama.id, drama.name)">
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
import { ref,onMounted } from 'vue';
import Navigation from "@/components/navigation.vue";

// const videoName=ref('')

const indicate=ref('/Entertainment')
const shortDramas=ref([])
const browseDetails=ref([])
const findUpdateTimePage=ref([])
const loadFindTop10Details=ref([])
const loadFindCollectTopDetails=ref([])
import { useRouter } from 'vue-router';
const router=useRouter()
const userName=localStorage.getItem('userName')
const total=ref(0)
const pageNum= ref(1);
const pageSize= ref(14);
const sort=ref('娱乐新闻')
const loadFindTop10 =async()=>{
  const  res=await request.get('/news/top10')
  loadFindTop10Details.value=res.data.data
  console.log("==============bfd======",loadFindTop10Details.value);
}
//正在播出
const loadFindCollectTop10=async()=>{
  const res = await request.get('/news/collectTop10')
  loadFindCollectTopDetails.value=res.data.data
  console.log("loadFindCollectTopDetails",loadFindCollectTopDetails.value);
}
const handleSizeChange =(val)=>{
    pageSize.value=val;
    search()
}
const handleCurrentChange =(val)=>{
  pageNum.value=val;
  search()
}
const formatDate=(dateString)=> {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // 补零
        const day = ('0' + date.getDate()).slice(-2); // 补零
        return `${year}-${month}-${day}`;
    }
const search = async () => {
  try {
    const res = await request.get('/news/page', { params: { pageSize: pageSize.value,pageNum: pageNum.value} });
    shortDramas.value = res.data.data.records;

    total.value=res.data.data.total
  } catch (error) {
    console.error('Error searching:', error);
  }
}
const browsePage = async () => {
  try {
    const res = await request.get('/news/browsePage');
    browseDetails.value = res.data.data;
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
//限制字数
const truncateString = (str, maxLength) => {
  if (!str) return "";
  if (str.length <= maxLength) {
    return str;
  } else {
    return str.slice(0, maxLength) + "...";
  }
};
const goToDramaDetail=async(dramaId,name)=>{
    request.post(`/news/NewsClick/${dramaId}`)
    router.push({ name: 'NewsDetails', params: { id: dramaId ,name: name,sort: sort.value,indicate: indicate.value} });
}
//正在播出
const gotoCollectTop=async(name,id)=>{
  request.post(`/news/click/${id}`)
  request.post('/news/click/',{
    id: id,
    username: userName
  })
  router.push({name:'VideoStory',params:{id: id,name: name,sort: sort.value,indicate: indicate.value}})
}
const gotoloadFindTop10=(name,id)=>{
  request.post(`/news/click/${id}`)
  request.post('/news/click/',{
    id: id,
    username: userName
  })
  console.log("------------------",id);
  router.push({ name: 'VideoStory', params: { id: id ,name: name,sort: sort.value,indicate: indicate.value} });
}
onMounted(() => {
  search(); // 页面加载时执行搜索
  browsePage();
  updateTimePage();
  loadFindTop10()
  loadFindCollectTop10()
});

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
    width: 190px;
    height: 270px;
}
.box-card{
    background-color: white;
    width: 800px;
    height: 2370px;
    border-radius: 0px;
}
.box-card5{
    width: 282px;
    height: 820px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
}
.box-card6{
    width: 282px;
    height: 420px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    margin-top: 40px;
    border-radius: 0px;
}
.box-card7{
    width: 282px;
    height: 700px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    margin-top: 40px;
    border-radius: 0px;
    background-color:white; /* 设置背景颜色透明度 */
    margin-top: 20px;
    --el-card-border-color: 0
}
.box-card8{
    width: 282px;
    height: 800px;
    margin-top: -19px;
    margin-left: -14px;
    border-radius: 0px;
    margin-top: 40px;
    border-radius: 0px;
}

.drama-card .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */

  overflow: hidden;
  border-radius: 5px;
  border-radius: 0px;
}
.drama-card img {
    width: 178px;
    height: 140px;
  object-fit: cover;
}

.drama-card{
    
    margin-bottom: -80px;
    position: relative; /* 确保伪元素的定位基于 .drama-card */
}

.drama-card:not(:first-child)::after {
    content: "";
    display: block;
    width: 100%;
    height: 1px; /* 线条的高度 */
    background-color: whitesmoke; /* 线条的颜色 */
    position: absolute;
    bottom: 160px; /* 根据 margin-bottom 调整线条位置 */
    margin-bottom: 0px;
}
.drama-card1 img {
    width: 120px;
    height: 90px;
  object-fit: cover;
}
.drama-card1{
    
    margin-bottom: -90px;
    position: relative; /* 确保伪元素的定位基于 .drama-card */
}
.drama-card2{
  height: 40px;
  margin-bottom: 10px;
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