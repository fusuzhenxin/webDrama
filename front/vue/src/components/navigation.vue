<template>
    <div class="container" style="min-height: 100%; padding-bottom: 100px;">
      <el-container>
        <el-header style="padding: 0px">
          <el-menu :default-active="activeIndex" style="justify-content: center;padding: 0px;background-color: black;border-bottom: 0px solid;" mode="horizontal" @select="handleMenuSelect">
            <router-link to="/HelloWorld">
            <el-menu-item style="color: white;" index="HelloWorld">首页</el-menu-item>
          </router-link>
          <router-link to="/traversing">
            <el-menu-item  style="color: white;" index="traversing">穿越</el-menu-item>
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
            <router-link to="/korean">
              <el-menu-item style="color: white;" index="korean">韩剧</el-menu-item>
            </router-link>
            <div class="flex gap-4 items-center">
              <el-input
                  v-model="searchQuery"
                  style="width: 240px;margin-top: 10px;margin-left: 30px;height: 34px;"
                  size="large"
                  placeholder="搜索"
                  :prefix-icon="Search"
              />
            </div>
            <el-button @click="search" style="width: 34px;height: 34px;margin-top: 10px;margin-left: 10px;border-radius: 2px"><img style="width: 20px;height: 20px;" src="../assets/1.png"></el-button>
            <!-- router-view 用于显示当前路由对应的组件内容 -->

          </el-menu>
        </el-header>
</el-container>

    </div>
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

<style>

</style>
