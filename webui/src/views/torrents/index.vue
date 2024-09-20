<template>
  <a-space direction="vertical" fill>
    <a-typography-text style="font-size: 1.2em">
      {{ t('page.torrents.description') }}
    </a-typography-text>
    <torrentsTable />
  </a-space>
</template>

<script setup lang="ts">
import torrentsTable from './components/TorrentsTable.vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
</script>

<style scoped>
.align-right {
  display: flex;
  justify-content: flex-end;
}
</style>