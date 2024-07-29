<template>
  <div class="container" style=" padding-bottom: 100px;">
    <div style="margin-bottom: 20px">
      <el-input style="width: 240px;margin-right: 20px;" placeholder="请输入剧名" v-model="name"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="loadAdminPage"><i class="el-icon-search"></i> 搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset" ><i class="el-icon-refresh"></i> 重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button style="margin-left: 5px" type="primary" @click="dialogVisible=true"><i class="el-icon-search"></i> 新增</el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <template v-slot:reference>
          <el-button type="danger" >批量删除 <i class="el-icon-remove-outline"></i></el-button>
        </template>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" style="width: 100%;" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column prop="id" label="编号" width="180"></el-table-column>
      <el-table-column prop="name" label="剧名" width="180"></el-table-column>
      <el-table-column prop="userId" label="用户名"></el-table-column>
      <el-table-column prop="content" label="内容"></el-table-column>
      <el-table-column prop="createtime" label="创建时间"></el-table-column>
      <el-table-column prop="updatetime" label="更新时间"></el-table-column>
      <el-table-column label="操作" width="250">
        <template v-slot="scope">
          <el-button type="primary" @click="handEdit(scope.row)">编辑</el-button>
          <el-popconfirm
              style="margin-left: 5px"
              title="您确定删除这行数据吗？"
              @confirm="del(scope.row.id)"
          >
            <template v-slot:reference>
              <el-button type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="信息"  v-model="dialogVisible" width="40%" :before-close="handleClose">

      <el-form label-width="120px"  style="width: 80%; margin: 0 auto">
        <el-form-item prop="name" label="剧名">
          <el-input v-model:modelValue="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="content" label="内容">
          <el-input v-model:modelValue="form.content" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="userId" label="用户名">
          <el-input v-model:modelValue="form.content" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>



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
import {onMounted, ref} from 'vue';
import request from "@/utils/request";
import {ElMessage} from "element-plus";
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(5);
const dialogVisible =ref(false)
const tableData=ref([]);
const multipleSelection = ref([]);
const username=ref('');
const form = ref({
  username: '',
  phone: ''
});


onMounted(()=>{
  loadAdminPage();
})

const save = async () => {
  // 添加保存逻辑
  const res =await request.post("/comments/save",form.value);
  if (res.data.code === '200'){
    ElMessage.success("添加成功")
    dialogVisible.value=false
    loadAdminPage()
  }else {
    ElMessage.error("添加失败")
  }
};

const del = async  (id) =>{
  const res= await request.delete(`/comments/${id}`);
  if (res.data.code === '200'){
    ElMessage.success('删除成功');
    loadAdminPage();
  }else {
    ElMessage.error("删除失败")
  }
}

const reset = () => {
  // 添加重置逻辑
  username.value='';
  loadAdminPage()
};
const loadAdminPage = async () =>{
  try {
    const res= await request.get('/comments/page',{params: {pageNum: pageNum.value,pageSize: pageSize.value,username: username.value}})
    tableData.value = res.data.data.records
    total.value = res.data.data.total
    console.log("----",res.data.data)
    console.log("----",total.value)
  }catch (error){
    console.error('error',error)
  }

};


const  handEdit =(row) =>{
  form.value=JSON.parse(JSON.stringify(row))
  dialogVisible.value=true

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

const delBatch = async () => {
  if (!multipleSelection.value.length) {
    ElMessage.error("请选择需要删除的数据");
    return;
  }

  const ids = multipleSelection.value.map(v => v.id);  // [{}, {}, {}] => [1,2,3]
  console.log("++++",ids)
  try {
    const res = await request.post("/comments/del/batch", ids);
    if (res.data.code === '200') {
      ElMessage.success('批量删除成功');
      loadAdminPage();
    } else {
      ElMessage.error("批量删除失败");
    }
  } catch (error) {
    console.error('批量删除错误:', error);
    ElMessage.error("批量删除失败，请重试");
  }
};

</script>

<style scoped>
.container { }
</style>
