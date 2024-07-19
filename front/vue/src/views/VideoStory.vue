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
            <el-row>
              <el-col :span="8">
                <img :src="Description && Description.cover" alt="Placeholder Image" class="card-image">
        
              </el-col>
              <el-col :span="16">
                <div class="card-content">
                  <p style="font-weight: bold 88; font-size: 20px;">{{ Description.name }}</p>
                  <p>剧名：{{ Description.name }}</p>
                  <p>主演：{{ Description.actors }}</p>
                  <div style="display: flex; align-items: center;">
                    <p style="margin-top: -10px">类型:</p>
                    <el-button plain  style="margin-bottom: 25px;margin-left: 15px;height: 25px;" v-for="(category, index) in Description && Description.classify.split(' ')" :key="index" >
                {{ category }}
                </el-button>
                  </div>

                  <span class="episode-text">更新到{{ numberOfEpisodes }}集</span>
                  <p class="description-container">简介：    {{ Description && Description.description }}</p>
                  <el-button  style="padding: 0 28px;background-color: red;height: 40px;" @click="goToEpisode(episode)" type="danger" round >立刻播放</el-button>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <el-row :gutter="20">
      <el-col :span="16">
        <div class="grid-content bg-purple">
            <el-card class="box-card1">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">选集播放</h3>
                </div>
                <div class="episode-grid">
              <div
                v-for="episode in numberOfEpisodes"
                :key="episode"
                class="episode-card"
                @click="goToEpisode(episode)"
              >
                第 {{ episode }} 集
              </div>
              </div>
            </el-card>
        </div>
      </el-col>
    
    </el-row>
    <el-col :span="16"></el-col>
    <el-row :gutter="20">
      <el-col :span="16">
        <div class="grid-content bg-purple">
            <el-card class="box-card2">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">正在热播</h3>
             
                </div>
                <div class="drama-list">
          <div v-for="(drama, index) in shortDramas.slice(0, 5)" :key="index" class="drama-card" @click="goToDramaDetail(drama.detailsId,drama.name)">
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
    <el-row :gutter="20">
      <el-col :span="16">
        <div class="grid-content bg-purple">
            <el-card class="box-card3">
              <div style="display: flex; align-items: center;">
            <h3 style="color: red; font-size: 26px; width: 10px;margin-left: -22px;margin-top: -5px;">|</h3>
             <h3 style="margin-top: -10px;margin-left: 10px;">评论区</h3>
                </div>
                <textarea class="comment_content fl" name="comment_content" style="background-color: #f5f5f5;width: 700px;height: 150px;" placeholder="老弟，整两句"></textarea>
                <div style="display: flex;">
                    <p style="font-size: 18px;">还可以输入200字</p>
                    <el-button style="margin-left: 455px;margin-top: 10px;width:115px;height: 40px;background-color: red;color:white" @click="gotoPublish">发布</el-button>
                </div> 
                <div class="pagination">
                <el-button>首页</el-button>
                <el-button>上一页</el-button>
                <el-button>下一页</el-button>
                <el-button>末页</el-button>  
                </div>
            
            </el-card>
        </div>
      </el-col>
     
    </el-row>
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
                <div v-for="(drama, index) in browseDetails.slice(0,7)" :key="index" class="drama-card2" @click="goToNewsDetails(drama.id, drama.name)">
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
      <div v-for="(drama, index) in loadFindTop10Details.slice(1,5)" :key="index">
        <div class="drama-card3" @click="gotoloadFindTop10(drama.name,drama.detailsId)">
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
                <div v-for="(drama, index) in findUpdateTimePage.slice(0,7)" :key="index" class="drama-card2" @click="goToNewsDetails(drama.id, drama.name)">
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
const browseDetails=ref('')
const findUpdateTimePage=ref('')
const loadFindTop10Details=ref([])
const loadFindCollectTopDetails=ref([])
const pageNum= ref(1);
const pageSize= ref(10);
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
const route=useRoute()
const router=useRouter()
const userName=localStorage.getItem('userName')
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

//评论发布
const gotoPublish=async()=>{
 alert('谢谢你的评论，我们需要审核一下')
}
//正在播出
const loadFindCollectTop10=async()=>{
  const res = await request.get('/news/collectTop10')
  loadFindCollectTopDetails.value=res.data.data
}
//热门视频
const loadFindTop10 =async()=>{
  const  res=await request.get('/news/top10')
  loadFindTop10Details.value=res.data.data
}
const browsePage = async () => {
  try {
    const res = await request.get('/news/browsePage', { params: { pageSize: pageSize.value,pageNum: pageNum.value} });
    browseDetails.value = res.data.data;
    console.log("================================",res.data.data);
  } catch (error) {
    console.error('Error searching:', error);
  }
}
const formatDate=(dateString)=> {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // 补零
        const day = ('0' + date.getDate()).slice(-2); // 补零
        return `${year}-${month}-${day}`;
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
const updateTimePage = async () => {
  try {
    const res = await request.get('/news/updateTimePage', { params: { pageSize: pageSize.value,pageNum: pageNum.value} });
    findUpdateTimePage.value = res.data.data.records;
    console.log("================================",res.data.data);
  } catch (error) {
    console.error('Error searching:', error);
  }
}
const goToEpisode=async(episode)=>{
    // Navigate to the detail page and pass drama ID as route parameter
    router.push({ name: 'videoDetail', params: { id: episode,name: videoName.value } });
}
//热门视频
const gotoloadFindTop10=(name,dramaId)=>{
  request.post(`/news/click/${dramaId}`)
  request.post('/news/click/',{
    id: dramaId,
    username: userName
  })
  console.log("idid ",dramaId);
  router.push({ name: 'VideoStory', params: { id: dramaId ,name: name,sort: sort.value,indicate:indicate.value}});
}
const goToDramaDetail=async(dramaId,name)=>{
  request.post(`/news/click/${dramaId}`)
  request.post('/news/click/',{
    id: dramaId,
    username: userName
  })
  console.log("---",dramaId);
  router.push({ name: 'VideoStory', params: { id: dramaId ,name: name,sort: sort.value,indicate:indicate.value}});
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
const goToNewsDetails=async(dramaId,name)=>{
  request.post(`/news/NewsClick/${dramaId}`)
    router.push({ name: 'NewsDetails', params: { id: dramaId ,name: name,sort: sort.value,indicate: indicate.value} });
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

onMounted(() => {
  updateTimePage()
  browsePage();
  loadFindTop10()
  loadFindCollectTop10()
});
//一个多少集
const loadNumberOfEpisodes = async () => {
  try {
    const res = await request.get('/videos/diversity', { params: { name: videoName.value } });
    numberOfEpisodes.value = res.data.data;
  } catch (error) {
    console.error('Error loading number of episodes:', error);
  }
};
const loadDescription = async () => {
  try {
    const res = await request.get('/details/finAll', { params: { name: videoName.value } });
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
    width: 800px;
    height: 402px;
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
    height: 650px;
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
    height: 500px;
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
.drama-card {
  width: calc(17% - -16px);
  margin-left: 5px;
  margin-bottom: 10px;
  padding: 0px;
  box-sizing: border-box;
}
.drama-card .image-wrapper {
  position: relative; /* 设置父元素为相对定位，使播放按钮的绝对定位相对于图片 */
  width: 136px;
  height: 190px;
  overflow: hidden;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
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
.pagination {
  display: flex;
  justify-content: center;
  margin-left: -50px;
  margin-top: 50px;
}
.description-container{
  color:  #999;
  margin-top: -10px;
  max-height: 100px;
   overflow-y: auto; /* 如果内容超过最大高度，则显示滚动条 */
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
.episode-text {
  font-size: 14px;
  position: absolute;
    left: 10px;
    background-color: rgba(0, 0, 0, .6);
    color: white;
    padding: 3px 10px;
    border-radius: 14px;
    margin-top: 59px;
    margin-left: 87px;
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