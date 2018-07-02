package com.fqxyi.social.library.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.fqxyi.social.library.R;
import com.fqxyi.social.library.dialog.ISocialType;
import com.fqxyi.social.library.dialog.ItemClickListener;
import com.fqxyi.social.library.dialog.SocialDialog;
import com.fqxyi.social.library.dialog.SocialTypeBean;
import com.fqxyi.social.library.share.IShareCallback;
import com.fqxyi.social.library.share.ShareDataBean;
import com.fqxyi.social.library.share.ShareHelper;
import com.fqxyi.social.library.share.WBShareHelper;
import com.fqxyi.social.library.util.SocialUtil;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends Activity {

    private static final String TAG = "ShareActivity";

    //分享弹框
    SocialDialog socialDialog;
    //数据源-分享类型
    List<SocialTypeBean> socialTypeBeans;
    //数据源-分享数据
    ShareDataBean shareDataBean;
    //分享入口类
    ShareHelper shareHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //初始化分享类型
        socialTypeBeans = (ArrayList<SocialTypeBean>) getIntent().getSerializableExtra("SocialTypeBean");
        //初始化分享数据
        shareDataBean = (ShareDataBean) getIntent().getSerializableExtra("ShareDataBean");
        //创建分享入口类
        shareHelper = SocialUtil.get().getShareHelper();
    }

    public void share(View view) {
        if (socialDialog == null) {
            socialDialog = new SocialDialog(this);
        }
        socialDialog.initSocialType(socialTypeBeans);
        socialDialog.setItemClickListener(new ItemClickListener() {
            @Override
            public void click(SocialTypeBean socialTypeBean, int position) {
                initItemClick(socialTypeBean, shareCallback);
            }
        });
        socialDialog.show();
    }

    IShareCallback shareCallback = new IShareCallback() {
        @Override
        public void onSuccess(String msg, String response) {
            Toast.makeText(ShareActivity.this, "onSuccess, msg =" + msg + ", response = " + response, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String msg) {
            Toast.makeText(ShareActivity.this, "onError, msg = " + msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(String msg) {
            Toast.makeText(ShareActivity.this, "onCancel, msg = " + msg, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 具体的item点击逻辑
     */
    private void initItemClick(SocialTypeBean socialTypeBean, IShareCallback shareCallback) {
        if (socialTypeBean == null) {
            return;
        }
        switch (socialTypeBean.type) {
            case ISocialType.SOCIAL_WX_SESSION: //微信
                shareHelper.shareWX(this, false, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_WX_TIMELINE: //朋友圈
                shareHelper.shareWX(this, true, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_SMS: //短信
                shareHelper.shareShortMessage(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_COPY: //复制
                shareHelper.shareCopy(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_REFRESH: //刷新
                shareHelper.shareRefresh(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_QQ: //QQ
                shareHelper.shareQQ(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_WB: //微博
                shareHelper.shareWB(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_WX_MINIPROGRAM: //微信小程序
                shareHelper.shareWxMiniProgram(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_ALIPAY_MINIPROGRAM: //支付宝小程序
                shareHelper.shareAlipayMiniProgram(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_COLLECTION: //收藏
                shareHelper.shareCollection(this, shareDataBean, shareCallback);
                break;
            case ISocialType.SOCIAL_SHOW_ALL: //查看全部
                shareHelper.shareShowAll(this, shareDataBean, shareCallback);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareHelper != null) {
            shareHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (shareHelper != null) {
            shareHelper.onNewIntent(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socialDialog = null;
        if (shareHelper != null) {
            shareHelper.onDestroy();
            shareHelper = null;
        }
    }

}