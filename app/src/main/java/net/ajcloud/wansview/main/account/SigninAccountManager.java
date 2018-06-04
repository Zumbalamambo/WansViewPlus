package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.text.TextUtils;

import net.ajcloud.wansview.entity.SigninAccountInfo;
import net.ajcloud.wansview.entity.SigninAccountInfoDao;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.support.core.bean.SigninBean;

/**
 * Created by mamengchao on 2018/05/30.
 * Function:登录账号信息管理类
 */
public class SigninAccountManager {

    private Context context;
    private SigninAccountInfoDao signinAccountInfoDao;

    public SigninAccountManager(Context context) {
        this.context = context;
        signinAccountInfoDao = MainApplication.getApplication().getDaoSession().getSigninAccountInfoDao();
    }

    private static SigninAccountInfoDao getDao() {
        return MainApplication.getApplication().getDaoSession().getSigninAccountInfoDao();
    }

    /**
     * 保存当前登录账号
     */
    public void saveCurrentAccount(String mail, SigninBean bean) {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount != null) {
            recentLoginAccount.setIsRecent(false);
            recentLoginAccount.setAccessToken(null);
            recentLoginAccount.setRefreshToken(null);
            recentLoginAccount.setExpiresIn(null);
            recentLoginAccount.setTokenType(null);
            recentLoginAccount.setScope(null);
            signinAccountInfoDao.update(recentLoginAccount);
        }

        SigninAccountInfo info = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.Mail.eq(mail))
                .unique();
        if (info == null) {
            info = new SigninAccountInfo(mail, bean);
            signinAccountInfoDao.insert(info);
        } else {
            info.setIsRecent(true);
            info.setAccessToken(bean.accessToken);
            info.setRefreshToken(bean.refreshToken);
            info.setExpiresIn(bean.expiresIn);
            info.setTokenType(bean.tokenType);
            info.setScope(bean.tokenType);
            signinAccountInfoDao.update(info);
        }
    }

    public String getCurrentAccountMail() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : recentLoginAccount.getMail();
    }

    public String getCurrentAccountAccessToken() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : recentLoginAccount.getAccessToken();
    }

    public String getCurrentAccountRefreshToken() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : recentLoginAccount.getRefreshToken();
    }

    /**
     * 清除登录信息，登出时用
     */
    public void clearAccountSigninInfo() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount != null) {
            recentLoginAccount.setAccessToken(null);
            recentLoginAccount.setRefreshToken(null);
            recentLoginAccount.setExpiresIn(null);
            recentLoginAccount.setTokenType(null);
            recentLoginAccount.setScope(null);
            signinAccountInfoDao.update(recentLoginAccount);
        }
    }

    /**
     * 判断是否自动登录
     */
    public boolean isAutoSignin() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return false;
        } else {
            if (TextUtils.isEmpty(recentLoginAccount.getAccessToken())) {
                return false;
            } else {
                return true;
            }
        }
    }
}
