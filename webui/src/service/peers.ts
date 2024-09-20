import {useEndpointStore} from '@/stores/endpoint'
import urlJoin from 'url-join'
import {getCommonHeader} from './utils'
import type {CommonResponseWithPage} from '@/api/model/common'
import type {ActiveMonitoringRecord, Torrent} from "@/api/model/torrents";
import type {PeersData} from "@/api/model/peers";
import type {BanLog} from "@/api/model/banlogs";


export async function getPeerRecords(): Promise<CommonResponseWithPage<PeersData[]>> {
    const endpointStore = useEndpointStore()
    await endpointStore.serverAvailable

    const url = new URL(urlJoin(endpointStore.endpoint, 'api/peer/{ip}'), location.href)

    return fetch(url, {headers: getCommonHeader()}).then((res) => {
        endpointStore.assertResponseLogin(res)
        return res.json()
    })
}

export async function getPeerAccessHistory(infoHash: string): Promise<CommonResponseWithPage<ActiveMonitoringRecord[]>> {
    const endpointStore = useEndpointStore()
    await endpointStore.serverAvailable

    const url = new URL(urlJoin(endpointStore.endpoint, `api/peer/${infoHash}/accessHistory`), location.href)

    return fetch(url, {headers: getCommonHeader()}).then((res) => {
        endpointStore.assertResponseLogin(res)
        return res.json()
    })
}

export async function getPeerBanHistory(infoHash: string): Promise<CommonResponseWithPage<BanLog[]>> {
    const endpointStore = useEndpointStore()
    await endpointStore.serverAvailable

    const url = new URL(urlJoin(endpointStore.endpoint, `api/peer/${infoHash}/banHistory`), location.href)

    return fetch(url, {headers: getCommonHeader()}).then((res) => {
        endpointStore.assertResponseLogin(res)
        return res.json()
    })
}