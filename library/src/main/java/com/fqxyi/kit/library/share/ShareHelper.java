package com.fqxyi.kit.library.share;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分享入口类
 */
public class ShareHelper {

    private static final String TAG = "ShareHelper";

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    private Builder builder;

    private WXShareHelper wxShareHelper;
    private SMShareHelper smShareHelper;
    private QQShareHelper qqShareHelper;
    private WBShareHelper wbShareHelper;

    public ShareHelper(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    /**
     * 分享到微信或朋友圈
     * @param isTimeLine true 朋友圈 false 微信
     */
    public void shareWX(final Activity activity, final boolean isTimeLine, final ShareDataBean shareDataBean, final IShareCallback shareCallback) {
        if (fixedThreadPool.isShutdown()) {
            return;
        }
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (wxShareHelper == null) {
                    wxShareHelper = new WXShareHelper(activity, builder.getWxAppId(), builder.getWxAppSecret());
                }
                wxShareHelper.share(isTimeLine, shareDataBean, shareCallback);
            }
        });
    }

    /**
     * 分享到短信
     */
    public void shareShortMessage(final Activity activity, final ShareDataBean shareDataBean, final IShareCallback shareCallback) {
        if (fixedThreadPool.isShutdown()) {
            return;
        }
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (smShareHelper == null) {
                    smShareHelper = new SMShareHelper(activity);
                }
                smShareHelper.share(shareDataBean, shareCallback);
            }
        });
    }

    /**
     * 复制
     */
    public void shareCopy(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            // 将文本数据复制到剪贴板
            clipboardManager.setText(shareDataBean.shareUrl);
            if (shareCallback != null) {
                shareCallback.onSuccess();
            }
        } else {
            if (shareCallback != null) {
                shareCallback.onError("clipboardManager == null");
            }
        }
    }

    /**
     * 刷新
     */
    public void shareRefresh(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {

    }

    /**
     * 分享到QQ
     */
    public void shareQQ(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {
        if (qqShareHelper == null) {
            qqShareHelper = new QQShareHelper(activity, builder.getQqAppId());
        }
        qqShareHelper.share(shareDataBean, shareCallback);
    }

    /**
     * 分享到新浪微博
     */
    public void shareWB(final Activity activity, final ShareDataBean shareDataBean, final IShareCallback shareCallback) {
        if (fixedThreadPool.isShutdown()) {
            return;
        }
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (wbShareHelper == null) {
                    wbShareHelper = new WBShareHelper(activity, builder.getWbAppId(), builder.getWbRedirectUrl());
                }
                wbShareHelper.share(shareDataBean, shareCallback);
            }
        });
    }

    /**
     * 分享到微信小程序
     */
    public void shareWxMiniProgram(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {

    }

    /**
     * 分享到支付宝小程序
     */
    public void shareAlipayMiniProgram(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {

    }

    /**
     * 收藏
     */
    public void shareCollection(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {

    }

    /**
     * 查看全部
     */
    public void shareShowAll(Activity activity, ShareDataBean shareDataBean, IShareCallback shareCallback) {

    }

    /**
     * qq登录和分享以及微博登录都需要在其当前的activity的onActivityResult中调用该方法
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (qqShareHelper != null) {
            qqShareHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 微博分享需要在其当前的activity中的onNewIntent中调用该方法
     */
    public void onNewIntent(Intent intent) {
        if (wbShareHelper != null) {
            wbShareHelper.onNewIntent(intent);
        }
    }

    /**
     * 微信分享，在微信回调到WXEntryActivity的onResp方法中调用
     *
     * @param success 表示是否分享成功
     */
    public void sendShareBackBroadcast(Context context, boolean success) {
        Intent intent = new Intent(WXShareHelper.ACTION_WX_SHARE_RECEIVER);
        intent.putExtra(WXShareHelper.KEY_WX_SHARE_CALLBACK, success);
        context.sendBroadcast(intent);
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (wxShareHelper != null) {
            wxShareHelper.onDestroy();
            wxShareHelper = null;
        }
        if (qqShareHelper != null) {
            qqShareHelper.onDestroy();
            qqShareHelper = null;
        }
        if (wbShareHelper != null) {
            wbShareHelper.onDestroy();
            wbShareHelper = null;
        }
    }

    public static final class Builder {
        private String qqAppId;

        private String wxAppId;
        private String wxAppSecret;

        private String wbAppId;
        private String wbRedirectUrl;

        public String getQqAppId() {
            return qqAppId;
        }

        public Builder setQqAppId(String qqAppId) {
            this.qqAppId = qqAppId;
            return this;
        }

        public String getWxAppId() {
            return wxAppId;
        }

        public Builder setWxAppId(String wxAppId) {
            this.wxAppId = wxAppId;
            return this;
        }

        public String getWxAppSecret() {
            return wxAppSecret;
        }

        public Builder setWxAppSecret(String wxAppSecret) {
            this.wxAppSecret = wxAppSecret;
            return this;
        }

        public String getWbAppId() {
            return wbAppId;
        }

        public Builder setWbAppId(String wbAppId) {
            this.wbAppId = wbAppId;
            return this;
        }

        public String getWbRedirectUrl() {
            return wbRedirectUrl;
        }

        public Builder setWbRedirectUrl(String wbRedirectUrl) {
            this.wbRedirectUrl = wbRedirectUrl;
            return this;
        }

        public ShareHelper build() {
            return new ShareHelper(this);
        }
    }

}
