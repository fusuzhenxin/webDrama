<template>

  <el-menu
    :default-active="$route.path" router
    class="el-menu-demo"
    mode="horizontal"
    
    background-color="#545c64"
    text-color="#fff"
    active-text-color="#ffd04b"
    style="height: 70px;margin-top: -10px;border-radius: 5px;"
    @select="handleSelect"
  >
    <el-menu-item index="/HelloWorld">  
      <i class="el-icon-eleme"></i>
      <span>首页</span>
    </el-menu-item>

    <el-sub-menu index="file">
      <template #title>
        <i class="el-icon-question"></i>
        <span>视频文件管理</span>
      </template>
       <el-menu-item index="/fileList">文件列表</el-menu-item>
    </el-sub-menu>

    <el-sub-menu index="details">
        <template #title>
          <i class="el-icon-user"></i>
          <span>视频详情管理</span>
        </template>

        <el-menu-item index="/detailsList">视频详情列表</el-menu-item>
    </el-sub-menu>
      <el-sub-menu index="admin">
        <template #title>
          <i class="el-icon-user"></i>
          <span>管理员管理</span>
        </template>
        <el-menu-item index="/adminList">管理员列表</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="news">
        <template #title>
          <i class="el-icon-user"></i>
          <span>娱乐新闻管理</span>
        </template>
        <el-menu-item index="/newsList">娱乐新闻列表</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="crawler">
        <template #title>
          <i class="el-icon-user"></i>
          <span>视频图片爬取管理</span>
        </template>

        <el-menu-item index="/crawlerList">视频详情列表</el-menu-item>
      </el-sub-menu>
      <el-avatar style="margin-left:750px"
        src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
      />
      <el-dropdown style="width: 10px">
        <span style="margin-top: 15px; justify-content: flex-end;height: 10PX;">
      <span style="color: white">DDD</span>
      <el-icon class="el-icon--right">
        <arrow-down />
      </el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>登出</el-dropdown-item>
        <el-dropdown-item>我的信息</el-dropdown-item>
        <el-dropdown-item>Action 3</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  </el-menu>

</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue'
const handleSelect = () => {

}
</script>

<style scoped>
.demo-type {
margin-left: 100px;
}
.demo-type > div {
  flex: 1;
  text-align: center;
}

.demo-type > div:not(:last-child) {
  border-right: 1px solid var(--el-border-color);
}
.example-showcase .el-dropdown-link {
 color: black;
}
</style>
