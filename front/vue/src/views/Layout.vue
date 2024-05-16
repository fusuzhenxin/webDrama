<template>
  <div class="outer-container" style="background-color: black;">
    <div class="container">
      <el-container>
        <el-header style="background-color: black;width: 1160px;margin-left :20px;padding: 0px;">
          <div>
            <navigation />
            <!-- router-view 用于显示当前路由对应的组件内容 -->
          </div>
        </el-header>
        <el-container>
          <el-main style="overflow: hidden;">
            <!-- 身体 -->
            <van-swipe
              class="my-swipe"
              :autoplay="3000"
              indicator-color="white"
              style="background-color: transparent;"
            >
              <!-- 更换图片 -->
              <van-swipe-item>
                <img class="carousel-image" src="@/assets/img.png" alt="Image 1" />
              </van-swipe-item>
              <van-swipe-item>
                <img class="carousel-image" src="@/assets/3.jpg" alt="Image 2" />
              </van-swipe-item>
            </van-swipe>
            <HelloWorld />
          </el-main>
        </el-container>
      </el-container>
    </div>
  </div>
</template>


<script setup>
import Navigation from "@/components/navigation.vue";
import HelloWorld from "@/components/HelloWorld.vue";


// const currentPage = ref(1);



// const handleCurrentChange = () => {
//   // 处理当前页改变事件
// };

// const handleSizeChange = () => {
//   // 处理每页显示条数改变事件
// };
</script>

<style scoped>
body {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: black;
  margin: 0;
}

.outer-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.container {
  min-height: 100%;
  padding-bottom: 100px;
  background-color: black;
  width: 1200px; /* 设置固定宽度 */
}

.nav-list {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 30px;
  list-style: none;
  background-color: gold;
  z-index: 1000;
}

.my-swipe {
  background-color: transparent; /* 设置轮播图容器背景色为透明 */
  width: 100%;
  height: 482px;
  margin-top: -20px;
}

.carousel-image {
  width: 100%; /* 使用百分比宽度，让图片自适应容器 */
  height: auto; /* 使用 auto 高度，让图片自适应容器高度 */
  object-fit: contain; /* 让图片完全适应容器，并保持原始宽高比 */
}
</style>
