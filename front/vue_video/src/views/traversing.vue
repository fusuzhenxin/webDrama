<template>
  <div>
    <VideoPlayer :videoSource="videoURL" />
  </div>
</template>

<script>
import VideoPlayer from './VideoPlayer.vue';

export default {
  components: {
    VideoPlayer
  },
  data() {
    return {
      videoURL: 'http://localhost:9090/files/3ddad62a24d44d56bfe3fcadad78517a.mp4'
    };
  }
};
</script>
