<template>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;">
    <el-container>
      <el-header >
        <div  style="height: 60px; line-height: 60px; background-color: whitesmoke; margin-bottom: 2px; display: flex;padding: 0px">
          <div style="width: 300px">
            <img src="@/assets/logo.png" alt="" style="width: 40px; position: relative; top: 10px; left: 20px">
            <span style="margin-left: 25px; font-size: 24px">微商城</span>
          </div>
          <div style="flex: 1; text-align: right; padding-right: 20px">
            <el-dropdown size="medium">
        <span class="el-dropdown-link" style="cursor: pointer">
         {{ admin.username }}<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
              <el-dropdown-menu class="dropdown-menu" v-show="dropdownVisible">
                <el-dropdown-item><div style="width: 50px; text-align: center;" @click="logout">退出</div></el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container>

        <el-aside style="width: 200px" >
          <Aside/>
        </el-aside>
        <el-main>
          <div style="background-color: white; padding: 10px">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>

import Aside from "@/components/Aside.vue";

import { ref,onMounted } from 'vue';

const admin = {
  username: "Admin" // Assuming admin data is available
};

const dropdownVisible = ref(false);

const logout = () => {
  // Implement logout functionality
};
const dropdownContainer = ref(null);

onMounted(() => {
  if (dropdownContainer.value) {
    dropdownContainer.value.addEventListener('click', () => {
      dropdownVisible.value = false;
    });
  }
});
</script>

<style scoped>
.container { }
</style>
