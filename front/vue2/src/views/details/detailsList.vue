<template>
  <div class="container" style="min-height: 100%; padding-bottom: 100px;">
    <div style="margin-bottom: 20px">
      <el-input style="width: 240px;margin-right: 20px;" placeholder="请输入剧名" v-model="name"></el-input>
      <el-input style="width: 240px" placeholder="请输入分类" v-model="classify"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="loadFilePage"><i class="el-icon-search"></i> 搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset" ><i class="el-icon-refresh"></i> 重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button style="margin-left: 5px" type="primary" @click="dialogVisible = true"><i class="el-icon-search"></i> 新增</el-button>

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
      <el-table-column prop="detailsId" label="编号" width="100"></el-table-column>
      <el-table-column prop="name" label="剧名" width="120"></el-table-column>
      <el-table-column prop="actors" label="主演" width="100"></el-table-column>
      <el-table-column prop="cover" label="封面" width="130px">
        <template v-slot="scope">
          <el-image :src="scope.row.cover" :preview-src-list="[scope.row.cover]"></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" width="300px">
        <template v-slot="{ row }">
          <span>{{ truncateString(row.description, 20) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="classify" label="分类" width="150px"></el-table-column>
      <el-table-column prop="quantity" label="点赞数"></el-table-column>
      <el-table-column prop="collect" label="收藏数"></el-table-column>
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


    <el-dialog title="信息" v-model="dialogVisible" width="40%" :before-close="handleClose">
      <el-form label-width="120px"  style="width: 80%; margin: 0 auto">
        <el-form-item label="剧名" prop="username">
          <el-input  v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类"   prop="classify">
          <el-input v-model="form.classify" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
  
          <el-input
            v-model="form.description"
            style="width: 300px"
            :autosize="{ minRows: 2, maxRows: 4 }"
            type="textarea"
          
        />
        </el-form-item>
        
        <el-form-item label="主演" prop="actors">
          <el-input v-model="form.actors" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="cover" label="食品图片">
          <el-upload action="http://localhost:9090/file/cover" ref="coverUpload" @success="handleImgUploadSuccess">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
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
const tableData=ref('');
const multipleSelection = ref([]);
const name=ref('');
const coverUpload=ref('')
const classify=ref('');
const form = ref({
  name:'',
  classify: '',
  description: '',
  actors:'',
  cover: ''
})

const handleClose = (done) => {
  if (confirm('确定要关闭对话框吗？')) {
    done(); // 关闭对话框
  } else {
    // 阻止关闭对话框
  }
};
onMounted(()=>{
  loadFilePage();
})
const truncateString = (str, maxLength) => {
  if (str.length <= maxLength) {
    return str;
  } else {
    return str.slice(0, maxLength) + '...';
  }
};
const save = async () => {
  // 添加保存逻辑
  const res=await request.post('/details/save',form.value);
  if (res.data.code === '200'){
    ElMessage.success("添加成功")
    dialogVisible.value=false
    loadFilePage()
  }else {
    ElMessage.error("添加失败")
  }
};
const handleImgUploadSuccess = (response) => {
  // 上传成功后，处理返回的响应数据
    form.value.cover = response; // 假设返回的数据中包含图片地址
    coverUpload.value.clearFiles(); // 清空已上传的文件

};



// 其他方法

const del = async  (id) =>{
  const res= await request.delete(`/details/${id}`);
  if (res.data.code === '200'){
    ElMessage.success('删除成功');
    loadFilePage();
  }else {
    ElMessage.error("删除失败")
  }
}

const reset = () => {
  // 添加重置逻辑
  name.value='';
  classify.value='';
  loadFilePage()
};
const loadFilePage = async () => {
  try {
    const res = await request.get('/details/paging', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: name.value,
        classify: classify.value
      }
    })
    tableData.value = res.data.data.records
    total.value = res.data.data.total
    console.log("----", res.data.data)
    console.log("----", total.value)
  } catch (error) {
    console.error('error', error)
  }
};
const handleSizeChange = (val) => {
  pageSize.value = val;
  loadFilePage()
};

const handleCurrentChange = (val) => {
  pageNum.value = val;
  loadFilePage()
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
    const res = await request.post("/details/del/batch", ids);
    if (res.data.code === '200') {
      ElMessage.success('批量删除成功');
      loadFilePage();
    } else {
      ElMessage.error("批量删除失败");
    }
  } catch (error) {
    console.error('批量删除错误:', error);
    ElMessage.error("批量删除失败，请重试");
  }
};

const handEdit=(row)=>{
  form.value=JSON.parse(JSON.stringify(row))
  dialogVisible.value=true

}

</script>

<style scoped>
.container { }
</style>
