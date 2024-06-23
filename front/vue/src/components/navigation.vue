<template>

        <el-header style="height: 60px;">
          <el-menu :default-active="activeIndex" style="justify-content: center;background-color: black;border-bottom: 0px solid;" mode="horizontal" @select="handleMenuSelect">
            <img style="width: 150px; height: 80px;margin-top: -5px;" src="@/assets/logo_transparent.png"  />
            <router-link to="/HelloWorld">
            <el-menu-item style="color: white;" index="HelloWorld">首页</el-menu-item>
          </router-link>
          <router-link to="/traversing">
            <el-menu-item  style="color: white;" index="traversing">时空之旅</el-menu-item>
          </router-link>
          <router-link to="/counterattack">
            <el-menu-item style="color: white;" index="counterattack">逆袭</el-menu-item>
          </router-link>
          <router-link to="/sweet">
            <el-menu-item style="color: white;" index="sweet">甜宠</el-menu-item>
          </router-link>
          <router-link to="/rebirth">
            <el-menu-item style="color: white;" index="rebirth">重生</el-menu-item>
          </router-link>
          <router-link to="/Entertainment">
              <el-menu-item style="color: white;" index="korean">娱乐新闻</el-menu-item>
            </router-link>
            <router-link to="/korean">
              <el-menu-item style="color: white;" index="korean">韩剧</el-menu-item>
            </router-link>
            <div class="flex gap-4 items-center" >
              <el-input
                  v-model="searchQuery"
                  style="width: 240px;margin-top: 10px;margin-left: 30px;height: 34px; background-color: black;"
                  size="large"
                  placeholder="搜索"
                  :prefix-icon="Search"
              />
            </div>
            <el-button @click="search" style="width: 34px;height: 34px;margin-top: 10px;margin-left: 10px;border-radius: 2px;background-color: black"><img style="width: 20px;height: 20px;" src="../assets/1.png"></el-button>
            <!-- router-view 用于显示当前路由对应的组件内容 -->

          </el-menu>
        </el-header>


  </template>

<script setup>

import { ref } from 'vue';
import { useRouter } from "vue-router";
const  router = useRouter();
const searchQuery = ref('')
import { Search } from '@element-plus/icons-vue'

const search =() =>{
  if(searchQuery.value.trim() !==''){
    router.push({name:'search',params:{searchQuery:searchQuery.value}});
  }
};
</script>

<style scoped>
.el-input--large .el-input__wrapper{
  background-color: black
}
</style>
