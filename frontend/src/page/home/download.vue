<template>
  <div class="download gap1">
    <h2>下载本子</h2>
    <el-form :model="form" label-width="auto" @submit.preven="downloadAlbum">
      <el-form-item label="下载本子ID">
        <div class="downloadController">
          <el-input v-model="form.downloadAlbumId" class="albumId" clearable />
          <el-button type="primary" @click="downloadAlbum">下载</el-button>
        </div>
      </el-form-item>
    </el-form>
    <div class="gap-row">
      <h2>后台任务</h2>
      <div class="gap-row">
        <el-button circle @click="loadTask(false)" ><el-icon><Refresh /></el-icon></el-button>
        <el-text size="small">自动刷新</el-text>
        <el-switch v-model="autoRefresh" @change="loadTask()" />
      </div>
    </div>
    <div class="downloadBox gap1">
      <div class="downloadItem gap05" v-for="(item,i) in taskList" :key="i">
        <div class="line gap05">
          <el-tag effect="dark" v-if="item.a_id!=null">{{ item.a_id }}</el-tag>
          <el-tag effect="dark" v-else type="warning">非下载后台任务</el-tag>
          <el-text tag="ins">{{ item.name }}</el-text>
          <el-tag effect="plain">{{ item.pid }}</el-tag>
        </div>
        <div class="line gap05">
          <el-tag v-if="item.running===true" type="success" effect="dark">运行中</el-tag>
          <el-tag v-else-if="item.running===false && item.manualStop===false" type="danger" effect="dark">已停止</el-tag>
          <el-tag v-else type="warning" effect="dark">手动停止</el-tag>

          <el-popover width="95%" popper-class="p" v-if="item?.logLastNLine!=null && item?.logLastNLine?.length>0" :teleported="false">
            <template #reference>
              <el-badge :value="item?.logLastNLine?.length" is-dot >
                <el-text class="logText" line-clamp="5">{{ item.log_lastLine }}</el-text>
              </el-badge>
            </template>
            <div class="line gap-row completeLogBox">
              <el-button size="small" @click="copyLastLine(item?.logLastNLine)">复制</el-button>
              <el-segmented size="small" v-model="logShowComponent" :options="logShowComponentOptions" />
              <div class="runCommand">
                <span>jmcomic {{ item.a_id }} --option="./option.yml"</span>
                <el-icon @click="copyRunCommand(item.a_id)"><DocumentCopy /></el-icon>
              </div>
            </div>
            <div class="lastLine">
              <pre v-for="(logLine,i) in item?.logLastNLine" :key="i" v-if="logShowComponent==='pre'">{{ logLine }}</pre>
              <div class="nowrap" v-if="logShowComponent==='text'" v-for="(logLine,i) in item?.logLastNLine" :key="i" >{{ logLine }}</div>
            </div>
          </el-popover>
          <el-popover width="95%" popper-class="p" v-else-if="item?.log_full!=null" :teleported="false">
            <template #reference>
              <el-badge :value="item?.logLastNLine?.length" is-dot color="green">
                查看完整日志
              </el-badge>
            </template>
            <div >
              <div class="line gap-row completeLogBox">
                <el-button size="small" @click="copyText(item?.log_full)">复制</el-button>
              </div>
              <pre class="lastLine">{{ item?.log_full }}</pre>
            </div>
          </el-popover>
          <el-text tag="p" line-clamp="5" v-else >{{ item.log_lastLine }}</el-text>
        </div>
        <div class="line buttonLine gap05">
          <div class="left">
            <el-tag :type="item.running?'success':'info'" effect="dark" round >
              {{ formatRunTime(item.time, item.endTime) }}
            </el-tag>
          </div>
          <el-button-group>
            <el-button type="success" class="gap05" v-if="item.running===false && item.a_id!=null && typeof item.a_id === 'number'" size="small" @click="downloadAlbum(item.a_id)">
              <div class="gap05"><el-icon><RefreshLeft /></el-icon>重新下载</div>
            </el-button>
            <el-button type="danger" class="gap05" v-if="item.running===true" size="small" @click="stopTask(item.id)">
              <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>手动停止</div>
            </el-button>
            <el-button type="info" class="gap05" v-if="item.running===false" size="small" @click="delTask(item.id)">
              <div class="gap05"><el-icon><Delete /></el-icon>删除记录</div>
            </el-button>
            <el-button type="warning" class="gap05" disabled v-if="item.manualStop===true && item.running===false" size="small">
              <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>已手动停止</div>
            </el-button>
<!--            <el-tag type="danger" effect="dark" v-if="item.manualStop===true && item.running===false">
              <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>已手动停止</div>
            </el-tag>-->
          </el-button-group>
<!--          <div class="right gap-row">
            <el-button type="danger" class="gap05" v-if="item.running===false" size="small">
              <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>重新下载</div>
            </el-button>
            <el-button type="danger" class="gap05" v-if="item.running===true" size="small">
              <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>手动停止</div>
            </el-button>
            <el-button type="info" class="gap05" v-if="item.running===false" @click="delTask(item.id)"  size="small">
              <div class="gap05"><el-icon><Delete /></el-icon>删除记录</div>
            </el-button>
            <el-button type="info" class="gap05" disabled v-if="item.manualStop===true" size="small">
              <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>已手动停止</div>
            </el-button>
          </div>-->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
    // import {computed} from "vue";
    import API from "@/config/axios/axiosInstance.js";
    import {ElMessage, ElNotification} from "element-plus";
    import useClipboard from 'vue-clipboard3';
    import {CopyDocument} from "@element-plus/icons-vue";

    export default {
      name: "download",
      computed: {
        CopyDocument() {
          return CopyDocument
        }
      },
      //引入模块
      components: {},
      //父级传入数据
      props: {
        // inData: Object,
      },
      data() {
        return {
          //双向绑定处理
          // tabPosition: computed({
          //   get: () => this.inData,
          //   set: val => {
          //     this.$emit("update:inData", val);
          //   }
          // }),
          form: {
            downloadAlbumId: "",
          },
          // status: running | stop | stop(Manual)
          taskList: [
          ],
          autoRefresh: false,

          logShowComponent: 'text',
          logShowComponentOptions: ['pre','text'],
        }
      },
      //方法
      methods: {
        loadTask: function () {
          API.post("/api/download").then(res=>{
            let data = API.isSucess(res);
            if(data){
              this.taskList = data;
              if(this.autoRefresh===true){
                this.autoLoadTask();
              }
            }else {
              API.showDefErrMessage(res);
            }
          })
        },
        downloadAlbum: function (aId) {
          if(!aId){
            aId = this.form.downloadAlbumId;
          }
          if(typeof aId === "number"){
            aId = aId.toString();
          }else if(typeof aId !== "string") {
            aId = this.form.downloadAlbumId;
          }
          if(aId && aId.trim){
            aId = aId.trim();
          }
          if(!/^\d+$/.test(aId)){
            API.TipError("id只能是数字");
            return;
          }
          API.post("/api/download/d",{
            "id": aId
          }).then(res=>{
            let data = API.isSucess(res);
            if(data){
              //this.taskList = data;
              ElNotification({
                title: "已开始下载",
                message: "请稍后刷新任务列表查看下载详情",
                type: 'success',
                duration: 10000,
              })
              this.autoRefresh = true;
              this.loadTask();
              //this.loadTask(true);
            }else {
              API.showDefErrMessage(res)
            }
          })
        },
        delTask: function (id) {
          API.post("/api/download/del", {
            "id": id,
          }).then(res=>{
            let data = API.isSucess(res);
            if(data){
              API.TipSuccess("任务已删除")
              this.loadTask();
            }else {
              API.showDefErrMessage(res)
            }
          })
        },
        autoLoadTask: function () {
          let willNext = false;
          let that = this;
          for (const task of that.taskList) {
            if(task.running === true){
              willNext = true;
              break;
            }
          }
          this.autoRefresh = !!willNext
          if(willNext){
            setTimeout(()=>{
              //that.autoRefresh = true;
              that.loadTask();
              //that.loadTask(true);
            },1000)
          }
        },
        formatRunTime: function (time, endTime) {
          if(!endTime){
            endTime = new Date().getTime()
          }
          let runTime = endTime - time;
          if(isNaN(runTime) || runTime < 0){
            return "--";
          }
          runTime = Math.round(runTime / 1000)

          return this.formUseTime(runTime)
        },
        formUseTime: function (useTime) {
          //let unit = [60, 60, 60, 24]
          let unit = [60, 60, 60]// , 24
          let unitStr = ["s", "m", "h", "d"]

          let buff = []
          for (let i = 0; i < unit.length; i++) {
            if(useTime>=unit[i]){
              buff.push( useTime % unit[i] );
              useTime = Math.floor(useTime / unit[i])
            }
          }
          if(useTime>=0){
            buff.push(useTime);
          }
          let str = "";
          let k = 0;// 跳过0位置
          let n = buff.length - 1;
          for (let i = n; i >= 0 && i > (n - 2 - k); i--) { // 最多显示两位
            if(buff[i] === 0){
              k++;
              continue;
            }
            str += buff[i] + unitStr[i] + " "
          }
          return str.trim();
        },

        copyText: async function (text) {
          const { toClipboard } = useClipboard()

          /*const copy = async () => {
            try {
              await toClipboard(text)
              ElNotification({
                title: '已复制',
                message: '日志已复制',
                type: 'success',
              })
            } catch (e) {
              console.error(e)
              ElNotification({
                title: '复制失败',
                message: '日志复制失败',
                type: 'error',
              })
            }
          }*/
          try {
            await toClipboard(text)
            /*ElNotification({
              title: '已复制',
              message: '日志已复制',
              type: 'success',
            })*/

            ElMessage.success('已复制')
          } catch (e) {
            console.error(e)
            /*ElNotification({
              title: '复制失败',
              message: '日志复制失败',
              type: 'error',
            })*/
            ElMessage.error('复制失败')
          }
        },
        copyLastLine: function (logs) {
          /*
          let buff = "";
          for (let i = 0; i < logs.length; i++) {
            buff += logs[i];
            buff += "\r\n";
          }
          this.copyText(buff);
          */
          if(logs == null || logs.length === 0){
            /*ElNotification({
              title: '复制失败',
              message: '日志复制失败',
              type: 'error',
            })*/
            ElMessage.error('日志复制失败')
          }else{
            this.copyText(logs.join("\r\n"))
          }
        },
        copyRunCommand: function (aId) {
          this.copyText(`jmcomic ${aId} --option="./option.yml"`);
        },

        showToDo:function () {
          ElMessage("功能未实现")
        },
        stopTask: function (id) {
          API.post("/api/download/stop", {
            "id": id,
          }).then(res=>{
            let data = API.isSucess(res);
            if(data){
              API.TipSuccess("任务已删除")
              this.loadTask();
            }else {
              API.showDefErrMessage(res)
            }
          })
        }
      },
      //启动事件
      mounted() {
        this.loadTask();
      },
      //销毁
      beforeUnmount() {
      }
    }
</script>
<style scoped>
@import "@/style/download.css";
</style>