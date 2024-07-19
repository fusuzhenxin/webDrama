<template>
    <div class="profile">
      <div class="avatar">
        <img src="https://via.placeholder.com/150" alt="Avatar">
      </div>
      <div class="info">
        <h2>{{ fullName }}</h2>
        <p>年龄: {{ age }}</p>
        <p>职业: {{ occupation }}</p>
        <p>地址: {{ address }}</p>
      </div>
    </div>
  </template>
  
  <script setup>
  const fullName = localStorage.getItem('userName')
  const age = 30;
  const occupation = '软件工程师';
  const address = '北京市海淀区';
  
  // 可以通过接口或其他逻辑从后端获取数据并赋值给上述变量
  // Example: const { fullName, age, occupation, address } = await fetchUserData();
  </script>
  
  <style scoped>
  .profile {
    justify-content: center;
    height: 100vh;
  }
  
  .avatar {
    margin-right: 20px;
  }
  
  .avatar img {
    width: 150px;
    height: 150px;
    border-radius: 50%;
  }
  
  .info {
    text-align: left;
  }
  
  .info h2 {
    margin-bottom: 10px;
  }
  
  .info p {
    margin: 5px 0;
  }
  </style>
  