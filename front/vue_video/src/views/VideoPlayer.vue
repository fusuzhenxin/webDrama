<template>
  <div>
    <video ref="videoPlayer" class="video-js vjs-default-skin" controls preload="auto" width="640" height="264">
      <source :src="videoSource" type="video/mp4" />
    </video>
  </div>
</template>

<script>
import videojs from 'video.js';
import 'video.js/dist/video-js.css';

export default {
  name: 'VideoPlayer',
  props: {
    videoSource: {
      type: String,
      required: true
    }
  },
  mounted() {
    // 创建Video.js播放器
    this.player = videojs(this.$refs.videoPlayer, {
      // 设置选项
    }, function onPlayerReady() {
      // 播放器准备好后的回调函数
    });
  },
  beforeUnmount() {
    // 销毁播放器
    if (this.player) {
      this.player.dispose();
    }
  }
};
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>
