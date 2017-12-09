package com.woodsho.absoluteplan.fresco;

import android.net.Uri;
import android.util.Log;

import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.ProducerContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class ImageNetworkFetcherEx extends BaseNetworkFetcher<FetchState> {
    private static final String TAG = "ImageNetworkFetcherEx";
    private static final boolean DEBUG_FETCH_URL = false;
    private static final int NUM_NETWORK_THREADS = 3;
    private static final int MAX_REDIRECTS = 5;
    private static final int MAX_RETRY_COUNT = 3;

    private final ExecutorService mExecutorService;

    public ImageNetworkFetcherEx() {
        mExecutorService = Executors.newFixedThreadPool(NUM_NETWORK_THREADS);
    }

    @Override
    public FetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext context) {
        return new FetchState(consumer, context);
    }

    @Override
    public void fetch(final FetchState fetchState, final Callback callback) {
        if (DEBUG_FETCH_URL) Log.d(TAG, "+++++++++++++++++++ fetch uri : " + fetchState.getUri().toString());
        Runnable rn = new Runnable() {
            private int mRetryCount = 0;
            @Override
            public void run() {
                int redirectCount = 0;
                HttpURLConnection connection = null;
                Uri uri = fetchState.getUri();
                String scheme = uri.getScheme();
                String uriString = fetchState.getUri().toString();
                while (true) {
                    String nextUriString;
                    String nextScheme;
                    InputStream is = null;
                    try {
                        URL url = new URL(uriString);
                        if (DEBUG_FETCH_URL) Log.d(TAG, "fetch url : " + url);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        nextUriString = connection.getHeaderField("Location");
                        nextScheme = (nextUriString == null) ? null : Uri.parse(nextUriString).getScheme();
                        if (nextUriString == null || nextScheme.equals(scheme)) {
                            int code = connection.getResponseCode();
                            String contentType = connection.getContentType();
                            if(code >= 200 && code < 300 && contentType.contains("image/")) {
                                is = connection.getInputStream();
                                if (DEBUG_FETCH_URL) Log.d(TAG, "success url : " + url);
                                if (DEBUG_FETCH_URL) Log.d(TAG, "*********************** " + uri.toString());
                                long t1 = System.currentTimeMillis();
                                callback.onResponse(is, -1);
                                if (DEBUG_FETCH_URL) Log.d(TAG, "*********************** " + uri.toString() + " t2 - t1 : " + (System.currentTimeMillis() - t1));
                            } else {
                                Log.e(TAG, "Unexpected HTTP code " + code + " content type : " + contentType + " url : " + uriString);
                                callback.onFailure(new IOException("Unexpected HTTP code " + code + " content type : " + contentType));
                            }
                            break;
                        }
                        redirectCount += 1;
                        if(redirectCount > MAX_REDIRECTS) {
                            throw new IOException("url has too many redirects");
                        }
                        uriString = nextUriString;
                        scheme = nextScheme;
                        Log.d(TAG, "... next scheme...");
                    } catch (Exception e) {
                        Log.e(TAG, "failed exception : " + e + " url : " + uriString);
                        if (mRetryCount >= MAX_RETRY_COUNT) {
                            callback.onFailure(e);
                            Log.e(TAG, "callback failure ...");
                            break;
                        }
                        mRetryCount += 1;
                        fetch(fetchState, callback, this);
                        break;
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Exception ex) {
                            }
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        if (DEBUG_FETCH_URL) Log.d(TAG, "############################### " + uri.toString());
                    }
                }
            }
        };
        fetch(fetchState, callback, rn);
    }

    private void fetch(final FetchState fetchState, final Callback callback, Runnable rn) {
        final Future<?> future = mExecutorService.submit(rn);
        fetchState.getContext().addCallbacks(
                new BaseProducerContextCallbacks() {
                    @Override
                    public void onCancellationRequested() {
                        if (DEBUG_FETCH_URL) Log.d(TAG, "================================: " + fetchState.getUri().toString());
                        if (future.cancel(false)) {
                            callback.onCancellation();
                        }
                    }
                });
    }

    @Override
    public void onFetchCompletion(FetchState fetchState, int byteSize) {
        super.onFetchCompletion(fetchState, byteSize);
        if (DEBUG_FETCH_URL) Log.d(TAG, "--------------------- fetch uri : " + fetchState.getUri().toString());
    }
}
