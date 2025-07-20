<template>
  <router-view />
</template>

<script>
export default {
  name: 'App',
  components: {
  },
  methods: {
    resize: function () {
      /*const vh = window.innerHeight * 0.01;
      document.documentElement.style.setProperty('--vh', `${vh}px`);*/
      const pHeight = window.innerHeight;
      document.documentElement.style.setProperty('--pHeight', `${pHeight}px`);

      /*
      * 修复移动端 vh 高度问题
      * https://github.com/Faisal-Manzer/postcss-viewport-height-correction
      * */
      return this.setViewportProperty(document.documentElement)
    },
    setViewportProperty: function (doc) {
      let customViewportCorrectionVariable = 'vh';

      let prevClientHeight;
      let customVar = '--' + ( customViewportCorrectionVariable || 'vh' );
      function handleResize() {
        let clientHeight = doc.clientHeight;
        if (clientHeight === prevClientHeight) return;
        requestAnimationFrame(function updateViewportHeight(){
          doc.style.setProperty(customVar, (clientHeight * 0.01) + 'px');
          prevClientHeight = clientHeight;
        });
      }
      handleResize();
      return handleResize;
    }
  },
  mounted() {
    // this.upPageSize()
    window.addEventListener('resize', this.resize());
    this.resize();
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resize());
  },
}
</script>

<style>
@import "@/style/base.css";
</style>
<style scoped>
body{
  background: #dadce0;
}
</style>
