<template>
  <el-menu
    :default-active="$route.path"
    router
    :default-openeds="opens"
    class="el-menu-demo"
    mode="vertical"
    background-color="#545c64"
    text-color="#fff"
    active-text-color="#ffd04b"
    style="height: 100vh; width: 200px; border-radius: 0;"
    @select="handleSelect"
  >
    <img class="main-img" src="@/assets/logo_transparent.png" />
    <div v-for="item in menus" :key="item.id">
      <template v-if="item.path">
        <el-menu-item :index="item.path">
          <template #title>
            <i :class="item.icon"></i>
            <span>{{ item.menuName }}</span>
          </template>
        </el-menu-item>
      </template>
      <template v-else>
        <el-sub-menu :index="item.id + ''">
          <template #title>
            <i :class="item.icon"></i>
            <span>{{ item.menuName }}</span>
          </template>
          <el-menu-item
            v-for="subItem in item.children"
            :key="subItem.id"
            :index="subItem.path"
          >
            <template #title>
              <span>{{ subItem.menuName }}</span>
            </template>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </div>
  </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue';

// 从 localStorage 获取菜单数据
const menus = ref(JSON.parse(localStorage.getItem("menus")) || []);

// 计算默认打开的菜单项索引
const opens = computed(() => {
  return menus.value.filter(item => !item.path).map(item => item.id + '');
});

const handleSelect = (index) => {
  // 处理菜单项选择的逻辑
  console.log("Selected:", index);
};
</script>

<style scoped>
.el-menu-demo {
  position: fixed;
  top: 0;
  left: 0;
}

.main-img {
  width: 200px; 
  height: 200px;
  margin-top: 0;
  margin-bottom: -20px;
}

/* 确保菜单项字体颜色正常显示 */
.el-menu-item,
.el-submenu {
  color: #fff; /* 确保菜单项和子菜单字体颜色为白色 */
}

.el-menu-item:hover,
.el-menu-item.is-active {
  color: #ffd04b; /* 设置选中和悬停时的字体颜色 */
}

/* 确保图标显示正确 */
.el-menu-item i,
.el-submenu i {
  margin-right: 10px; /* 图标与文本之间的间隔 */
}
</style>
