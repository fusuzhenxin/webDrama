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
  </el-menu>

</template>

<script setup>

const handleSelect = () => {

}
</script>

<style scoped>

</style>
