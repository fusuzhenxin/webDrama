<template>
    <div>
      <div style="margin: 10px 0">
        <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="userName"></el-input>
        <el-input style="width: 200px" placeholder="请输入邮箱" suffix-icon="el-icon-message" class="ml-5" v-model="email"></el-input>
        <el-input style="width: 200px" placeholder="请输入地址" suffix-icon="el-icon-position" class="ml-5" v-model="address"></el-input>
        <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
        <el-button type="warning" @click="reset">重置</el-button>
      </div>
  
      <div style="margin: 10px 0">
        <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
        <el-popconfirm
            class="ml-5"
            confirm-button-text='确定'
            cancel-button-text='我再想想'
            icon="el-icon-info"
            icon-color="red"
            title="您确定批量删除这些数据吗？"
            @confirm="delBatch"
        >
          <template #reference>
            <el-button type="danger">批量删除 <i class="el-icon-remove-outline"></i></el-button>
          </template>
        </el-popconfirm>
        <el-upload action="http://localhost:9090/api/user/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">
          <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>
        </el-upload>
        <el-button type="primary" @click="exp" class="ml-5">导出 <i class="el-icon-top"></i></el-button>
      </div>
  
      <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="userName" label="用户名" width="140"></el-table-column>
        <el-table-column label="角色">
          <template #default="scope">
            <span v-if="scope.row.userType">
            {{ getRoleName(scope.row.userType) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="nickName" label="昵称" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phonenumber" label="电话"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column label="操作"  width="200" align="center">
          <template #default="scope">
            <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
            <el-popconfirm
                class="ml-5"
                confirm-button-text='确定'
                cancel-button-text='我再想想'
                icon="el-icon-info"
                icon-color="red"
                title="您确定删除吗？"
                @confirm="del(scope.row.id)"
            >
              <template #reference>
                <el-button type="danger">删除 <i class="el-icon-remove-outline"></i></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="padding: 10px 0">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[2, 5, 10, 20]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
  
      <el-dialog title="用户信息" v-model="dialogFormVisible" width="30%" >
        <el-form label-width="80px" size="small">
          <el-form-item label="用户名">
            <el-input v-model="form.userName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="角色">
            <el-select clearable v-model="form.userType" placeholder="请选择角色" style="width: 100%">
              <el-option v-for="item in roles" :key="item.name" :label="item.name" :value="item.roleKey"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phonenumber" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="form.address" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
       
      </el-dialog>

      <el-dialog title="用户信息" v-model="disFormVisible" width="30%" >
        <el-form label-width="80px" size="small">
          <el-form-item label="用户名">
            <el-input v-model="form.userName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="角色">
            <el-select clearable v-model="form.userType" placeholder="请选择角色" style="width: 100%">
              <el-option v-for="item in roles" :key="item.name" :label="item.name" :value="item.roleKey"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phonenumber" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="form.address" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        
          <el-button @click="disFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="edit">确 定</el-button>
       
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import request from '@/utils/request'
  
  const tableData = ref([])
  const total = ref(0)
  const pageNum = ref(1)
  const pageSize = ref(10)
  const userName = ref("")
  const email = ref("")
  const address = ref("")
  const form = reactive({})
  const dialogFormVisible = ref(false)
  const multipleSelection = ref([])
  const disFormVisible=ref(false)
  const roles = ref([])
  
  const load = () => {
    request.get("/user/page", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        username: userName.value,
        email: email.value,
        address: address.value,
      }
    }).then(res => {
        console.log('===',res.data);
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    })
  
    request.get("/role").then(res => {
      roles.value = res.data.data
      console.log('roles',res.data.data);
    })
  }
  const getRoleName = (userType) => {
  const role = roles.value.find(v => v.roleKey === userType);
  return role ? role.name : '';
};
  const save = () => {
    request.post("/user/save", form).then(res => {
      if (res.data.code === '200') {
        ElMessage.success("保存成功")
        dialogFormVisible.value = false
        load()
      }
      if(res.data.code === '-1'){
        ElMessage.warning("用户名已存在")
      }
    })
  }

  const edit = async() =>{
    const res=await request.post('/user/edit',form)
    console.log('res',res.data);
    if(res.data.code ==='200'){
        ElMessage.success('修改成功')
        disFormVisible.value=false;
        load()
    }else{
        ElMessage.error('修改失败')
    }
  }
  
  const handleAdd = () => {

    dialogFormVisible.value = true
    Object.keys(form).forEach(key => form[key] = "")
    console.log(111);
  }
  
  const handleEdit = (row) => {
    Object.assign(form, JSON.parse(JSON.stringify(row)))
    disFormVisible.value = true
  }
  
  const del = (id) => {
    request.delete("/user/" + id).then(res => {
      if (res.data.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error("删除失败")
      }
    })
  }
  
  const handleSelectionChange = (val) => {
    multipleSelection.value = val
  }
  
  const delBatch = () => {
    let ids = multipleSelection.value.map(v => v.id)
    request.post("/user/del/batch", ids).then(res => {
      if (res.code === '200') {
        ElMessage.success("批量删除成功")
        load()
      } else {
        ElMessage.error("批量删除失败")
      }
    })
  }
  
  const reset = () => {
    userName.value = ""
    email.value = ""
    address.value = ""
    load()
  }
  
  const handleSizeChange = (newPageSize) => {
    pageSize.value = newPageSize
    load()
  }
  
  const handleCurrentChange = (newPageNum) => {
    pageNum.value = newPageNum
    load()
  }
  
  const exp = () => {
    window.open("http://localhost:9090/api/user/export")
  }
  
  const handleExcelImportSuccess = () => {
    ElMessage.success("导入成功")
    load()
  }
  
  onMounted(() => {
    load()
  })
  </script>
  
  <style>
  .headerBg {
    background: #eee!important;
  }
  .ml-5 {
    margin-left: 5px;
  }
  </style>
  