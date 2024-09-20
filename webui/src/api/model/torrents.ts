export interface Torrent {
    id: number;
    infoHash: string;
    name: string;
    size: number;
}
export interface TorrentData {
    /**
     * InfoHash
     */
    infoHash: string;
    /**
     * 种子名称
     */
    name: string;
    /**
     * Peer 连接计数，数据来自 ActiveMonitoring
     */
    peerAccessCount: number;
    /**
     * Peer 封禁计数，数据来自 BanHistory
     */
    peerBanCount: number;
    /**
     * 种子大小
     */
    size: number;
}


/**
 * GeoIP 信息
 *
 * Peer 地理位置信息（子字段无数据时可能为空）
 */
export interface GeoData {
    /**
     * 自治系统信息
     */
    as?: As;
    /**
     * 城市/位置信息
     */
    city?: City;
    /**
     * 国家/地区信息
     */
    country?: Country;
    /**
     * 网络属性信息（由 GeoCN 提供）
     */
    network?: GeoDataNetwork;
}

/**
 * 自治系统信息
 */
export interface As {
    /**
     * 网络地址
     */
    ipAddress?: string;
    /**
     * 自治系统网络信息
     */
    network?: AsNetwork;
    /**
     * 自治系统代码
     */
    number?: number;
    /**
     * 自治系统所属组织
     */
    organization?: string;
}

/**
 * 自治系统网络信息
 */
export interface AsNetwork {
    /**
     * 网络地址信息
     */
    ipAddress?: string;
    /**
     * 前缀长度
     */
    prefixLength?: number;
}

/**
 * 城市/位置信息
 */
export interface City {
    /**
     * 城市 ISO 代码/唯一识别代码
     */
    iso?: number;
    /**
     * 位置信息
     */
    location?: Location;
    /**
     * 城市名称
     */
    name?: string;
}

/**
 * 位置信息
 */
export interface Location {
    /**
     * 精确半径
     */
    accuracyRadius?: number;
    /**
     * 维度
     */
    latitude?: number;
    /**
     * 精度
     */
    longitude?: number;
    /**
     * 时区
     */
    timeZone?: string;
}

/**
 * 国家/地区信息
 */
export interface Country {
    /**
     * ISO代码
     */
    iso?: string;
    /**
     * 国家/地区名称
     */
    name?: string;
}

/**
 * 网络属性信息（由 GeoCN 提供）
 */
export interface GeoDataNetwork {
    /**
     * 服务提供商信息
     */
    isp?: string;
    /**
     * 网络类型，常见值如下，这些数据使用中文在 MMDB 中被硬编码，非后端翻译：
     * 宽带
     * 基站
     * 政企专线
     * 业务平台
     * 骨干网
     * IP专网
     * 网吧
     * 物联网
     * 数据中心
     * （可能增加）
     */
    netType?: string;
}

export interface Response {
    data: Data;
    message: null;
    success: boolean;
}

export interface Data {
    page: number;
    results: ActiveMonitoringRecord[];
    size: number;
    total: number;
}

/**
 * 主动监测记录
 */
export interface ActiveMonitoringRecord {
    /**
     * IP 地址，可能为 IPv4 / IPv6
     */
    address: string;
    /**
     * 客户端名称
     */
    clientName: string;
    /**
     * 下载量，总和
     */
    downloaded: number;
    /**
     * 最后一次下载量记录，偏移
     */
    downloadedOffset: number;
    /**
     * 下载器名称
     */
    downloader: string;
    /**
     * 首次发现于
     */
    firstTimeSeen: number;
    /**
     * 记录主键ID
     */
    id: number;
    /**
     * 最后一次记录的 UTP Flags
     */
    lastFlags: string;
    /**
     * 最后一次见到于，每次发现此 Peer 时，将会更新上面记录的各类数据
     */
    lastTimeSeen: number;
    /**
     * PeerID
     */
    peerId: string;
    torrent: Torrent;
    /**
     * 上传量，总和
     */
    uploaded: number;
    /**
     * 最后一次上传量记录，偏移
     */
    uploadedOffset: number;
}

