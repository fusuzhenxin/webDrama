<template>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;">
    <el-container>
      <el-header style="padding: 0px">
        <div class="nav-list">

        <el-menu  :default-active="activeIndex" style="justify-content: center;padding: 0px;background-color: black;" mode="horizontal" >
          <router-link to="/HelloWorld">
            <el-menu-item style="color: white" index="HelloWorld">首页</el-menu-item>
          </router-link>
          <router-link to="/traversing">
            <el-menu-item style="color: white" index="traversing">穿越</el-menu-item>
          </router-link>
          <router-link to="/counterattack">
            <el-menu-item style="color: white" index="counterattack">逆袭</el-menu-item>
          </router-link>
          <router-link to="/sweet">
            <el-menu-item  style="color: white" index="sweet">甜宠</el-menu-item>
          </router-link>
          <router-link to="/rebirth">
            <el-menu-item  style="color: white" index="rebirth">重生</el-menu-item>
          </router-link>
          <router-link to="/korean">
            <el-menu-item  style="color: white" index="korean">韩剧</el-menu-item>
          </router-link>
          <!-- router-view 用于显示当前路由对应的组件内容 -->

        </el-menu>
        </div>
      </el-header>
      <el-container>
        <el-main>      <!-- 身体 -->
          <div class="search-page">
            <!-- 搜索框和按钮 -->
            <li class="search-box">
              <div class="search-containers">
                <input type="text" placeholder="搜索" v-model="searchQuery" @keyup.enter="search">
                <button @click="search"><img src="../assets/1.png"></button>
              </div>
            </li>
          </div>

        </el-main>
<el-footer>

    <!-- 显示搜索结果的内容 -->
    <div v-if="shortDramas.length > 0">
      <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.id, drama.name)">
        <!-- ... -->
      </div>
    </div>
    <!-- 默认内容 -->
    <div v-else>
      <!-- 这里可以添加一些其他内容作为默认显示 -->
        <br>
      <h1>没有这</h1>
          <div v-for="(drama, index) in videoData" :key="index" class="drama-card" @click="goToDramaDetail(drama.id,drama.name)">
          <el-row>
          <el-col :span="7">
            <div class="grid-content bg-purple" style="width: 400px" >
              <!-- 封面图像 -->
              <div class="image-wrapper">
                <img :src="drama.cover" :alt="drama.name" class="drama-image">
              </div>
            </div>
          </el-col>
          <el-col :span="17">
            <div class="grid-content bg-purple-light" >
                <h1 style="margin-bottom: 40px">{{ drama.name }}</h1>
                <el-button  type="danger" plain style="margin-bottom: 40px;" v-for="(category, index) in drama.classify.split(' ')" :key="index" >
                {{ category }}
                </el-button>
                <br>
                <el-span style="font-size: 20px">{{drama.description}}</el-span>
              </div>
          </el-col>
        </el-row>
        </div>
      </div>

  <div v-for="(drama, index) in shortDramas" :key="index" class="drama-card" @click="goToDramaDetail(drama.id, drama.name)">
        <el-row style="margin-bottom: 100px">
          <el-col :span="7">
            <div class="grid-content bg-purple" style="width: 400px" >
              <!-- 封面图像 -->
              <div class="image-wrapper">
                <img :src="drama.cover" :alt="drama.name" class="drama-image">
              </div>
            </div>
          </el-col>
          <el-col :span="17">
            <div class="grid-content bg-purple-light" style="text-align: left">
              <h1 style="margin-bottom: 40px">{{ drama.name }}</h1>
              <el-button  type="danger" plain style="margin-bottom: 40px;" v-for="(category, index) in drama.classify.split(' ')" :key="index" >
                {{ category }}
              </el-button>
              <br>
                <span style="font-size: 20px">{{drama.description}}</span>
              </div>
          </el-col>

        </el-row>
        </div>

  <el-pagination
      style="justify-content: center"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[2, 5, 10, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
  ></el-pagination>
</el-footer>

      </el-container>
    </el-container>


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
const  videoData=ref([]);
const total=ref(0)
const pageNum= ref(1);
const pageSize= ref(10);

//根据搜索框输入的名字来查询短剧
// const search = async () => {
//   try {
//     const res = await request.get('/details/search', { params: { name: searchQuery.value } });
//     shortDramas.value = res.data.data;

//   } catch (error) {
//     console.error('Error searching:', error);
//   }
// }
//根据搜索框输入的名字来查询短剧
const search = async () => {
  try {
    const res = await request.get('/details/page', { params: { pageSize: pageSize.value,pageNum: pageNum.value,name: searchQuery.value } });
    shortDramas.value = res.data.data.records;

    total.value=res.data.data.total
    console.log("================================",res.data.data.records);
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

const handleSizeChange =(val)=>{
    pageSize.value=val;
    search();
}
const handleCurrentChange =(val)=>{
  pageNum.value=val;
 search();
}
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
.search-page {
  margin: -20px;
  padding: 129px;
  background-color: whitesmoke;

}
.search-containers {
  display: flex;
}
.search-containers input {
  padding: 5px;
  margin-right: 10px;
  margin-top: -26px;
  height: 40px;
  width: 400px;
  margin-left: 321px;
  border: 1px solid #ccc;
  font-family: '宋体', Arial, sans-serif;
}

.search-containers button {
  width: 51px;
  margin-top: -25px;
  padding: 5px;
  background-color: white;
  color: #e1442e;
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

.image-wrapper {
  width: 184px; /* 调整宽度 */
  height: 264px;
  margin-right: 20px; /* 调整封面图像和详情之间的间距 */
}

.drama-image{
  border-radius: 10px;
  width: 184px;
  height: 264px;
  margin-left: 120px;

}
.drama-card{
  margin-bottom: 50px;
}
</style>
