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
      <el-button type="primary"  @click="onButtonClick" style="width: 200px;height: 50px;" size="large">批量下载</el-button>
      <el-button type="warning"  @click="loadAdminPage" style="width: 200px;height: 50px;" size="large">搜索</el-button>
    </div>
    <div class="demo-progress">
    <el-progress :text-inside="true" :stroke-width="26" :percentage="percentage" />
    <el-text>[正在下载：]</el-text>
    <el-progress :text-inside="true" :stroke-width="22" :percentage="totalProgress" status="warning" />
  </div>

  <div class="container" style="min-height: 100%; padding-bottom: 100px;">


    <el-table 
    :data="tableData" 
    style="width: 100%;" 
    @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column prop="id" label="编号" width="180"></el-table-column>
      <el-table-column prop="name" label="剧名" width="180"></el-table-column>
      <el-table-column prop="classify" label="分类" width="180"></el-table-column>
      <el-table-column prop="cover" label="封面" width="180px">
      
        <template v-slot="scope">
          <el-image :src="scope.row.cover"></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" width="800px">
        <template v-slot="{ row }">
          <!-- <span>{{ truncateString(row.description, 20) }}</span> -->
          <span>{{row.description}}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template v-slot="scope">
          <el-button type="primary" @click="handEdit(scope.row)">下载</el-button>
        </template>
      </el-table-column>
    </el-table>


    <el-pagination
        style="justify-content: center;margin-top: 20px"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2, 5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
    ></el-pagination>
  </div>
  </template>
  
  <script setup>
import { ref, computed,onMounted } from 'vue';
import request from "@/utils/request";
import {ElMessage} from "element-plus";
const searchQuery = ref('');
const item = ref('');
const percentage = ref(0);
const taskId = ref(localStorage.getItem('taskId') || null);
const totalEpisodes = ref(5); // 总集数
const downloadedEpisodes = ref(0); // 已下载的集数
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(5);
const tableData=ref([]);
const multipleSelection = ref([]);
const form = ref({
  username: '',
  phone: ''
});


onMounted(()=>{
  loadAdminPage();
  if(taskId.value){
    listenProgress();
  }
})


// const truncateString = (str, maxLength) => {
//   if (!str) return "";
//   if (str.length <= maxLength) {
//     return str;
//   } else {
//     return str.slice(0, maxLength) + "...";
//   }
// };

const  loadAdminPage= async () =>{
  try {
    if (!item.value) {
      ElMessage.warning("请选择一个分类");
      return;
    }
    if (item.value === "短剧") {
      const res= await request.get('/crawler/page',{params: {pageNum: pageNum.value,pageSize: pageSize.value,name:searchQuery.value}})
    tableData.value = res.data.data.records
    total.value = res.data.data.total
    return; // 现在先返回，不发起任何请求
  }
    const res=await request.get('http://localhost:9090/crawler/saveListAcquire',{params:{name:searchQuery.value,classify: item.value}})
    tableData.value = res.data.data
      console.log(res.data);
  }catch (error){
    console.error('error',error)
  }

};

// const loadAdminPages = async () =>{
//   try {
//     const res= await request.get('/videos/acquire',{params: {name: searchQuery.value}})
//     tableData.value = res.data.data
//     console.log("----",res.data.data)
//     console.log("----",total.value)
//   }catch (error){
//     console.error('error',error)
//   }

// };
const  handEdit =async(row) =>{
  form.value=JSON.parse(JSON.stringify(row))
  try{
    if (!item.value) {
      ElMessage.warning("请选择一个分类");
      return;
    }
    let res;
    if (item.value === "短剧") {
    res=await request.get('http://localhost:9090/crawler/startDownloads',{params:{name:form.value.name}})
    taskId.value = res.data
    localStorage.setItem('taskId',taskId.value);
      console.log(res.data);
      listenProgress();
    return; // 现在先返回，不发起任何请求
  }
    res=await request.get('/crawler/startDownloadOnyWay',{params:{name:form.value.name,classify:form.value.classify}})
    taskId.value=res.data
    localStorage.setItem('taskId',taskId.value);
    console.log("=====================",res.data);
    listenProgress();
  }catch(error){
    console.log('error');
  }
  console.log(form.value.name);

}


const handleSizeChange = (val) => {
  pageSize.value = val;
  loadAdminPage()
};

const handleCurrentChange = (val) => {
  pageNum.value = val;
  loadAdminPage()
};

const handleSelectionChange = (val) => {
  // 添加表格选中项变化逻辑
  console.log(val)
  multipleSelection.value = val
};


const listenProgress = () => {
  if (!taskId.value) return;

  const eventSource = new EventSource(`http://localhost:9090/crawler/progress?taskId=${taskId.value}`);

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
    //   setTimeout(() => {
    //   listenProgress();
    // }, 3000);
  
  };
};

// 计算总进度

const totalProgress = computed(() => {
const threadCount=(downloadedEpisodes.value / totalEpisodes.value) * 100;
return threadCount;
});

//批量下载
const onButtonClick=async()=>{
  if (!item.value) {
    ElMessage.warning("请选择一个分类");
      return;
    }
  let res;
  if (item.value === "短剧") {
    res=await request.get('http://localhost:9090/crawler/startDownloads',{params:{name:searchQuery.value}})
    taskId.value = res.data
    localStorage.setItem('taskId',taskId.value);
      console.log(res.data);
      listenProgress();
    return; // 现在先返回，不发起任何请求
  }
  // res=await request.get('/videos/save',{params:{name:searchQuery.value,classify: item.value}});
     res=await request.get('/crawler/startDownload',{params:{name:searchQuery.value,classify: item.value}});
     taskId.value = res.data
     localStorage.setItem('taskId',taskId.value);
      console.log(res.data);
      listenProgress();
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
.example-showcase .el-loading-mask {
  z-index: 9;
}
  </style>
  