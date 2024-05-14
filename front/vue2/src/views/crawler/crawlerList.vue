<template>
    <div class="container" style="min-height: 100%; padding-bottom: 100px;">
    
      <el-row :gutter="20">
        <el-col :span="10">
          <div class="grid-content bg-purple">
            <el-input
              v-model="searchQuery"
              placeholder="请输入内容"
              size="small"
              class="input"
              style="width: 400px;height: 50px;margin-bottom: 60px;"
            ></el-input>
          </div>
        </el-col>
        <el-col :span="5">
        <el-select v-model="item" placeholder="请选择" style="width: 200px;height: 80px;margin-left: 100px;" size="max">
      <el-option
        v-for="option in options"
        :key="option.value"
        :label="option.label"
        :value="option.value"
      ></el-option>
    </el-select>
    </el-col>

      </el-row>
      <el-button type="primary"  @click="onButtonClick" style="width: 200px;height: 50px;" size="large">下载</el-button>
    </div>
  </template>
  
  <script setup>
import { ref } from 'vue';
import request from "@/utils/request";
import {ElMessage} from "element-plus";
const searchQuery = ref('');
const item = ref('');


const onButtonClick=async()=>{

  let res;
  if (item.value === "短剧") {
    res=await request.get('/videos/saves',{params:{name:searchQuery.value}})
    return; // 现在先返回，不发起任何请求
  }
     res=await request.get('/videos/save',{params:{name:searchQuery.value,classify: item.value}});
    if (res.data.code) {
        ElMessage.success("成功")
    }
}
    const options = [
      {
        value: "韩剧",
        label: "韩剧",
      },
      {
        value: "日剧",
        label: "日剧",
      },
      {
        value: "港剧",
        label: "港剧",
      },
      {
        value: "美剧",
        label: "美剧",
      },
      {
        value: "短剧",
        label: "短剧",
      }
    ];
  </script>
  
  <style scoped>
  .input {}
  .bg-purple {
    
  }
  .container {}
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  </style>
  