<script setup lang="ts">
import ApplicationViewport from '@/view/ApplicationViewport.vue';
</script>

<template>
  <ApplicationViewport title="Home" authenticated>
    <div class="container">
      <strong>Coming soon</strong>
      <p>
        Start with
        <a
          href="http://letmegooglethat.com/?q=How+to+make+a+home+page+in+vue"
          target="_blank"
          rel="noopener noreferrer">
          GrooveConductor
        </a>
      </p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.container {
  text-align: center;
  margin-top: 15%;

  & strong {
    font-size: 3rem;
    line-height: 26px;
    padding-bottom: 50px;
  }

  & p {
    font-size: 2rem;
    line-height: 2rem;
    color: #8c8c8c;
    margin: 1em 0;
  }

  & a {
    text-decoration: none;
    color: #3880ff;
  }
}
</style>
