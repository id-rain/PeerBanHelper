package com.ghostchu.peerbanhelper.util;

import com.ghostchu.peerbanhelper.Main;
import com.ghostchu.peerbanhelper.text.Lang;
import com.ghostchu.peerbanhelper.util.network.DFHostNameVerifier;
import com.ghostchu.peerbanhelper.util.network.DFSSLSocketFactory;
import com.ghostchu.peerbanhelper.util.network.IgnoreX509TrustManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.ProxySelector;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.ghostchu.peerbanhelper.text.TextManager.tlUI;

@Slf4j
public class DFHttpUtil {
    private static final int MAX_RESEND = 5;
    private static final CookieManager cookieManager = new CookieManager();
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static SSLSocketFactory DFSSLSocketFactory;
    private static X509TrustManager ignoreTrustManager;
    private static DFHostNameVerifier hostNameVerifier;

    static {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        try {
            DFSSLSocketFactory = new DFSSLSocketFactory();
            ignoreTrustManager = new IgnoreX509TrustManager();
            hostNameVerifier = new DFHostNameVerifier();
        } catch (Exception e) {
            log.error(tlUI(Lang.MODULE_AP_SSL_CONTEXT_FAILURE), e);
        }
    }

    public static OkHttpClient getHttpClient(boolean ignoreSSL, ProxySelector proxySelector) {
        OkHttpClient.Builder builder = Main.getSharedHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .readTimeout(Duration.of(30, ChronoUnit.SECONDS))
                .hostnameVerifier(hostNameVerifier)
                .cookieJar(new JavaNetCookieJar(cookieManager));
        if (ignoreTrustManager != null) {
            builder.sslSocketFactory(DFSSLSocketFactory, ignoreTrustManager);
        }
        if (proxySelector != null) {
            builder.proxySelector(proxySelector);
        }
        return builder.build();
    }

    public static Callback newFutureCallback(CompletableFuture<Response> future) {
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                future.complete(response);
            }
        };
    }

    public static ProgressListener newProgressListener() {
        return (totalBytesTransferred, contentLength, done) -> {
            if (contentLength != -1) {
                var percent = (100 * totalBytesTransferred) / (float) contentLength;
                log.info(tlUI(Lang.DOWNLOAD_PROGRESS_DETERMINED, totalBytesTransferred, contentLength, String.format("%.2f", percent)));
            } else {
                log.info(tlUI(Lang.DOWNLOAD_PROGRESS, totalBytesTransferred));
            }
            if (done) {
                log.info(tlUI(Lang.DOWNLOAD_COMPLETED, totalBytesTransferred));
            }
        };
    }

    public static ResponseBody newProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        return new ResponseBody() {
            private BufferedSource bufferedSource;

            @Override
            public @Nullable MediaType contentType() {
                return responseBody.contentType();
            }

            @Override
            public long contentLength() {
                return responseBody.contentLength();
            }

            @Override
            public @NotNull BufferedSource source() {
                if (bufferedSource == null) {
                    bufferedSource = Okio.buffer(source(responseBody.source()));
                }
                return bufferedSource;
            }

            private Source source(Source source) {
                return new ForwardingSource(source) {
                    long totalBytesRead = 0L;
                    long lastBytesRead = 0L;
                    long lastUpdateTime = System.currentTimeMillis();

                    @Override public long read(Buffer sink, long byteCount) throws IOException {
                        long bytesRead = super.read(sink, byteCount);
                        // read() returns the number of bytes read, or -1 if this source is exhausted.
                        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                        if (System.currentTimeMillis() - lastUpdateTime >= 3_000 && totalBytesRead - lastBytesRead >= 60 * 1024) {
                            lastUpdateTime = System.currentTimeMillis();
                            lastBytesRead = totalBytesRead;
                            progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                        }
                        return bytesRead;
                    }
                };
            }
        };
    }

    public static CompletableFuture<Response> nonRetryableSend(OkHttpClient client, Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        client.newCall(request).enqueue(newFutureCallback(future));
        return future;
    }

    public static CompletableFuture<Response> retryableSend(OkHttpClient client, Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        client.newCall(request).enqueue(newFutureCallback(future));
        return future.handleAsync((r, t) -> tryResend(client, request, 1, r, t), executor)
                .thenCompose(Function.identity());
    }

    public static boolean shouldRetry(Response r, Throwable t, int count) {
        if (count >= MAX_RESEND) {
            return false;
        }
        if (r != null) {
            return r.code() == 500
                    || r.code() == 502
                    || r.code() == 503
                    || r.code() == 504;
        }
        return false;
    }

    public static CompletableFuture<Response> tryResend(OkHttpClient client, Request request,
                                                        int count, Response resp, Throwable t) {
        if (shouldRetry(resp, t, count)) {
            if (resp == null) {
                log.warn("Request to {} failed, retry {}/{}: {}", request.url(), count, MAX_RESEND, t.getClass().getName() + ": " + t.getMessage());
            } else {
                try {
                    log.warn("Request to {} failed, retry {}/{}: {} ", request.url(), count, MAX_RESEND, resp.code() + " - " + resp.body().string());
                } catch (IOException ignored) {
                }
            }
            CompletableFuture<Response> future = new CompletableFuture<>();
            client.newCall(request).enqueue(newFutureCallback(future));
            return future.handleAsync((r, x) -> tryResend(client, request, count + 1, r, x), executor)
                    .thenCompose(Function.identity());
        } else if (t != null) {
            return CompletableFuture.failedFuture(t);
        } else {
            return CompletableFuture.completedFuture(resp);
        }
    }

    public interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}
