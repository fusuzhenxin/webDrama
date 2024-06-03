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
    <div class="demo-progress">
    <el-progress :text-inside="true" :stroke-width="26" :percentage="percentage" />
    <el-progress :text-inside="true" :stroke-width="22" :percentage="totalProgress" status="warning" />
    <button @click="startDownload">Start Download</button>
  </div>
  </template>
  
  <script setup>
import { ref, computed } from 'vue';
import request from "@/utils/request";
import {ElMessage} from "element-plus";
const searchQuery = ref('');
const item = ref('');
const percentage = ref(0);
const taskId = ref(null);
const totalEpisodes = ref(5); // 总集数
const downloadedEpisodes = ref(0); // 已下载的集数
const startDownload = async () => {
  try {
    const res=await request.get('/videos/startDownload',{params:{name:searchQuery.value}})
    taskId.value = res.data
    console.log(res.data);
    listenProgress();
  } catch (error) {
    console.error('Error starting download', error);
  }
};

const listenProgress = () => {
  if (!taskId.value) return;

  const eventSource = new EventSource(`http://localhost:9090/videos/progress?taskId=${taskId.value}`);

  eventSource.onmessage = (event) => {
    const progress = parseFloat(event.data);
    console.log("Progress for current episode:", progress);
    if (progress >= 100) {
      // 更新已下载集数并重置当前集数的进度
      downloadedEpisodes.value += 1;
      percentage.value = 0;
      console.log("Total episodes downloaded:", downloadedEpisodes.value);
      if (downloadedEpisodes.value==5) {
        downloadedEpisodes.value=0
        
      }
      // 如果所有集数已下载完成，关闭 SSE 连接
      if (downloadedEpisodes.value >= totalEpisodes.value) {
        totalProgress.value=0
        // eventSource.close();
        console.log("All episodes downloaded");
      }
    } else {
      percentage.value = progress; // 更新当前集数的进度
    }
  };

  eventSource.onerror = (error) => {
    console.error('Error with SSE', error);
    eventSource.close();
     // 重试机制
     setTimeout(() => {
      listenProgress();
    }, 3000);
  
  };
};

// 计算总进度

const totalProgress = computed(() => {
  return (downloadedEpisodes.value / totalEpisodes.value) * 100;
});

const onButtonClick=async()=>{

  let res;
  if (item.value === "短剧") {
    res=await request.get('http://localhost:9090/videos/startDownload',{params:{name:searchQuery.value}})
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
        value: "陆剧",
        label: "陆剧",
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
  .demo-progress .el-progress--line {
  margin-bottom: 15px;
  max-width: 600px;
}
  </style>
  