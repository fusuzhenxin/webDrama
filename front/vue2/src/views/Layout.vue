<template>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;">
    <el-container>
      <el-header >
      <Aside/>
      </el-header>
      <el-container>
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



const dropdownVisible = ref(false);


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
