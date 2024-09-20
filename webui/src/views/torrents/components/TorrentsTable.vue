<template>
  <a-table
      :stripe="true"
      :columns="columns"
      :data="list"
      :loading="tableLoading"
      :pagination="{
      total,
      current,
      pageSize,
      showPageSize: true,
      baseSize: 4,
      bufferSize: 1
    }"
      column-resizable
      size="medium"
      class="torrents-table"
      @page-change="changeCurrent"
      @page-size-change="changePageSize"
  >
    <template #name="{ record }">
      <a-space fill direction="vertical">
        <a-typography-text>
          {{ record.name }}
        </a-typography-text>
      </a-space>
    </template>
    <template #size="{ record }">
        <p>{{ formatFileSize(record.size) }}</p>
    </template>
    <template #action>
      <a-button-group>
        <a-button>{{ t("page.torrents.torrentsTable.column.action.info") }}</a-button>
        <a-button>{{ t("page.torrents.torrentsTable.column.action.banHistory") }}</a-button>
        <a-button>{{ t("page.torrents.torrentsTable.column.action.accessHistory") }}</a-button>
      </a-button-group>
    </template>
  </a-table>
</template>
<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {useAutoUpdatePlugin} from '@/stores/autoUpdate'
import {useEndpointStore} from '@/stores/endpoint'
import {usePagination} from 'vue-request'
import {formatFileSize} from '@/utils/file'
import {useI18n} from 'vue-i18n'
import {getTorrents} from "@/service/torrents";

const forceLoading = ref(true)
const endpointState = useEndpointStore()
const {t, d} = useI18n()
const {data, total, current, loading, pageSize, changeCurrent, changePageSize, refresh} =
    usePagination(
        getTorrents,
        {
          defaultParams: [
            {
              page: 1,
              pageSize: 10
            }
          ],
          pagination: {
            currentKey: 'page',
            pageSizeKey: 'pageSize',
            totalKey: 'data.total'
          },
          cacheKey: (params) =>
              `${endpointState.endpoint}-torrents-${params?.[0].page || 1}-${params?.[0].pageSize || 10}`,
          onAfter: () => {
            forceLoading.value = false
          }
        },
        [useAutoUpdatePlugin]
    )

watch([pageSize, current], () => {
  forceLoading.value = true
})

watch(() => endpointState.endpoint, refresh)

const tableLoading = computed(() => {
  return forceLoading.value || loading.value || !list.value
})

const columns = [
  {
    title: () => t('page.torrents.torrentsTable.column.name'),
    slotName: 'name',
    ellipsis: true,
  },
  {
    title: () => t('page.torrents.torrentsTable.column.size'),
    slotName: 'size',
    width: 150
  },
  {
    title: () => t('page.torrents.torrentsTable.column.action'),
    slotName: 'action',
    width: 300,
    tooltip: true
  }
]
const list = computed(() => data.value?.data.results)
</script>

<style scoped>
.red {
  color: rgb(var(--red-5));
}

.green {
  color: rgb(var(--green-5));
}
</style>
