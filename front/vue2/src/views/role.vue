<template>
    <div>
      <div style="margin: 10px 0">
        <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
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
        <el-table-column prop="name" label="名称" width="140"></el-table-column>
        <el-table-column prop="roleKey" label="唯一标识"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column label="操作"  width="300" align="center">
          <template #default="scope">
            <el-button type="info" @click="selectMenu(scope.row)">分配菜单 <i class="el-icon-menu"></i></el-button>
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
          <el-form-item label="名称">
            <el-input v-model="form.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="唯一标识">
            <el-input v-model="form.roleKey" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
       
      </el-dialog>

      <el-dialog title="用户信息" v-model="disFormVisible" width="30%" >
        <el-form label-width="80px" size="small">
          <el-form-item label="名称">
            <el-input v-model="form.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="唯一标识">
            <el-input v-model="form.roleKey" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
          <el-button @click="disFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="edit">确 定</el-button>
       
      </el-dialog>
    </div>
    <el-dialog title="菜单分配" v-model="menuDialogVis" width="30%">
      <el-tree
          :props="treeProps"
          :data="menuData"
          show-checkbox
          node-key="id"
          ref="treeRef"
          :default-expanded-keys="expands"
          :default-checked-keys="checks">
         <template #default="{ data }">
            <span><i :class="data.icon"></i> {{ data.menuName }}</span>
         </template>
      </el-tree>
        <el-button @click="menuDialogVis = false">取消</el-button>
        <el-button type="primary" @click="saveRoleMenu">确定</el-button>

    </el-dialog>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted,nextTick  } from 'vue'
  import { ElMessage } from 'element-plus'
  import request from '@/utils/request'
  import { useRouter } from 'vue-router'
  const router=useRouter()
  const tableData = ref([])
  const total = ref(0)
  const pageNum = ref(1)
  const pageSize = ref(10)
  const name = ref("")
  const form = reactive({})
  const dialogFormVisible = ref(false)
  const multipleSelection = ref([])
  const disFormVisible=ref(false)
  const ids=ref([]);
  const menuData = ref([]);
  const treeProps = { label: 'name' };
  const expands = ref([]);
  const checks = ref([]);
  const roleId = ref(0);
  const roleKey = ref('');
  const menuDialogVis = ref(false)
  const treeRef = ref(null);  // Define treeRef for the tree component
  const selectMenu = async (role) => {
  roleId.value = role.id;
  roleKey.value = role.roleKey;

  // 请求菜单数据
  const menuRes = await request.get("/menu");
  menuData.value = menuRes.data.data;
  expands.value = menuData.value.map(v => v.id);

  // 打开菜单对话框
  menuDialogVis.value = true;

  // 请求角色菜单数据
  const roleMenuRes = await request.get("/role/roleMenu/" + roleId.value);
  checks.value = roleMenuRes.data.data;

  // Ensure tree component is updated
  await nextTick();

  console.log('Tree Ref:', treeRef.value);
   console.log('Tree Ref:', treeRef.value);

  if (treeRef.value) {
    treeRef.value.setCheckedKeys(checks.value);
    ids.value.forEach(id => {
      if (!checks.value.includes(id)) {
        treeRef.value.setChecked(id, false);
      }
    });
  } else {
    console.error('Tree Ref is null or undefined');
  }
};
const saveRoleMenu = async () => {
  try {
    const checkedKeys = treeRef.value ? treeRef.value.getCheckedKeys() : [];
    console.log('checkedKeys',checkedKeys)
    const res = await request.post(`/role/roleMenu/${roleId.value}`, checkedKeys);
    if (res.data.code === '200') {
      ElMessage.success("绑定成功")
      menuDialogVis.value = false
      
      if (roleKey.value === 'ROLE_ADMIN') {
        await request.get('/user/logout')
        // 清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('userName')
        localStorage.removeItem('menus')
        localStorage.removeItem('userType')
        // 重定向到登录页面
        router.push('/login')
      }
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (error) {
    console.error("Error saving role menu:", error)
    ElMessage.error("操作失败")
  }
}
  const load = () => {
    request.get("/role/page", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: name.value,
      }
    }).then(res => {
        console.log('===',res.data);
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    })

    request.get("/menu/ids").then(r => {
    ids.value = r.data.data;
  });

  }
  const save = () => {
    request.post("/role/save", form).then(res => {
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
    const res=await request.post('/role/edit',form)
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
    request.delete("/role/" + id).then(res => {
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
    request.post("/role/del/batch", ids).then(res => {
      if (res.code === '200') {
        ElMessage.success("批量删除成功")
        load()
      } else {
        ElMessage.error("批量删除失败")
      }
    })
  }
  
  const reset = () => {
    name.value = ""
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
  