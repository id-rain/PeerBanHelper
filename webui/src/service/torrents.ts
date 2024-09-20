import {useEndpointStore} from '@/stores/endpoint'
import urlJoin from 'url-join'
import {getCommonHeader} from './utils'
import type {CommonResponse, CommonResponseWithPage} from '@/api/model/common'
import type {ActiveMonitoringRecord, Torrent} from "@/api/model/torrents";


export async function getTorrents(params: {
    page: number
    pageSize?: number
}): Promise<CommonResponseWithPage<Torrent[]>> {
    const endpointStore = useEndpointStore()
    await endpointStore.serverAvailable


    const url = new URL(urlJoin(endpointStore.endpoint, 'api/torrent'), location.href)
    url.searchParams.set('page', String(params.page))
    if (params.pageSize) {
        url.searchParams.set('pageSize', String(params.pageSize))
    }

    return fetch(url, {headers: getCommonHeader()}).then((res) => {
        endpointStore.assertResponseLogin(res)
        return res.json()
    })
}

export async function getTorrentAccessHistory(infoHash: string): Promise<CommonResponseWithPage<ActiveMonitoringRecord[]>> {
    const endpointStore = useEndpointStore()
    await endpointStore.serverAvailable

    const url = new URL(urlJoin(endpointStore.endpoint, `api/torrent/${infoHash}/accessHistory`), location.href)

    return fetch(url, {headers: getCommonHeader()}).then((res) => {
        endpointStore.assertResponseLogin(res)
        return res.json()
    })
}

