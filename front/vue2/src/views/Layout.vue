<template>
  <div class="common-layout">
    
    <el-container>
      <el-aside width="200px" >
        <Aside />
      </el-aside>
      <el-main style="margin-left: 0px;">
        <el-header style="width: 100%;">
          <el-dropdown class="custom-dropdown">
          <span class="dropdown-label">{{ userName }}</span>
          <template #dropdown>
      <el-dropdown-menu>
      <el-dropdown-item @click="logout">登出</el-dropdown-item>
      <el-dropdown-item @click="personal">我的信息</el-dropdown-item>
      <el-dropdown-item @click="password">修改密码</el-dropdown-item>
    </el-dropdown-menu>
  </template>
</el-dropdown>
        </el-header>
      
        <router-view />
        
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import Aside from "@/components/Aside.vue";
import { ref, onMounted } from 'vue';
import request from '@/utils/request'
import { useRouter } from "vue-router";
const router=useRouter()
const dropdownVisible = ref(false);
const dropdownContainer = ref(null);
const userName=localStorage.getItem("userName")
const logout = async() => {
  //await 的作用是确保在进行后续操作之前，登出请求已经完成。
  await request.get('/user/logout')
  // 清除本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('userName')
  localStorage.removeItem('menus')
  localStorage.removeItem('userType')
  // 重定向到登录页面
  router.push('/login')
}
const personal = () =>{
  router.push('/personal')
}
const password = () =>{
  router.push('/password')
}
onMounted(() => {
  if (dropdownContainer.value) {
    dropdownContainer.value.addEventListener('click', () => {
      dropdownVisible.value = false;
    });
  }
});
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
}
.dropdown-label {
  color: black;
  margin-top: 5px;
  margin-left: 1200px;
  /* Adjust other styles if needed */
}
</style>
