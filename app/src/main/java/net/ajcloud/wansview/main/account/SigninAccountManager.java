package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.text.TextUtils;

import net.ajcloud.wansview.entity.SigninAccountInfo;
import net.ajcloud.wansview.entity.SigninAccountInfoDao;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.support.core.bean.SigninBean;
import net.ajcloud.wansview.support.core.cipher.CipherUtil;
import net.ajcloud.wansview.support.event.RefreshTokenEvent;

import org.greenrobot.eventbus.EventBus;

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
    public void saveCurrentAccount(String mail, String password, SigninBean bean) {
        //清除最近登陆账号信息
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount != null) {
            recentLoginAccount.setIsRecent(false);
            recentLoginAccount.setAccessToken(null);
            recentLoginAccount.setRefreshToken(null);
            recentLoginAccount.setExpiresIn(0);
            recentLoginAccount.setTokenType(null);
            recentLoginAccount.setScope(null);
            signinAccountInfoDao.update(recentLoginAccount);
        }

        SigninAccountInfo info = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.Mail.eq(mail))
                .unique();

        if (info == null) {
            String salt = CipherUtil.getRandomSalt();
            String encodePwd = CipherUtil.naclEncodeLocal(password, salt);
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            bean.accessToken = encodeAccess;
            bean.refreshToken = encodeRefresh;
            info = new SigninAccountInfo(mail, encodePwd, salt, bean);
            signinAccountInfoDao.insert(info);
        } else {
            String salt = info.getSalt();
            String encodePwd = CipherUtil.naclEncodeLocal(password, salt);
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            info.setIsRecent(true);
            info.setAccessToken(encodeAccess);
            info.setRefreshToken(encodeRefresh);
            info.setExpiresIn(bean.expiresIn);
            info.setTokenType(bean.tokenType);
            info.setScope(bean.tokenType);
            info.setPassword(encodePwd);
            info.setSalt(salt);
            signinAccountInfoDao.update(info);
        }
    }

    /**
     * 保存当前登录账号
     */
    public void refreshCurrentAccount(SigninBean bean) {
        SigninAccountInfo info = getCurrentAccount();
        if (info != null) {
            String salt = info.getSalt();
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            info.setAccessToken(encodeAccess);
            info.setRefreshToken(encodeRefresh);
            info.setExpiresIn(bean.expiresIn);
            info.setTokenType(bean.tokenType);
            info.setScope(bean.tokenType);
            signinAccountInfoDao.update(info);
        }
    }

    public SigninAccountInfo getCurrentAccount() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount;
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
        if (recentLoginAccount == null) {
            return null;
        }
        String accessToken = recentLoginAccount.getAccessToken();
        String salt = recentLoginAccount.getSalt();
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(salt)) {
            return null;
        }
        return CipherUtil.naclDecodeLocal(accessToken, salt);
    }

    public String getCurrentAccountRefreshToken() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return null;
        }
        String refreshToken = recentLoginAccount.getRefreshToken();
        String salt = recentLoginAccount.getSalt();
        if (TextUtils.isEmpty(refreshToken) || TextUtils.isEmpty(salt)) {
            return null;
        }
        return CipherUtil.naclDecodeLocal(refreshToken, salt);
    }

    public String getCurrentAccountSalt() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return null;
        }
        String salt = recentLoginAccount.getSalt();
        if (TextUtils.isEmpty(salt)) {
            return null;
        }
        return recentLoginAccount.getSalt();
    }

    public String getCurrentAccountPassword() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return null;
        }
        String password = recentLoginAccount.getPassword();
        String salt = recentLoginAccount.getSalt();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(salt)) {
            return null;
        }
        return CipherUtil.naclDecodeLocal(password, salt);
    }

    public String getCurrentAccountGesture() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return null;
        }
        String gesture = recentLoginAccount.getGesture();
        String salt = recentLoginAccount.getSalt();
        if (TextUtils.isEmpty(gesture) || TextUtils.isEmpty(salt)) {
            return null;
        }
        return CipherUtil.naclDecodeLocal(gesture, salt);
    }

    public void setCurrentAccountGesture(String gesture) {
        SigninAccountInfo currentAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        String salt = currentAccount.getSalt();
        String gesturePwd = CipherUtil.naclEncodeLocal(gesture, salt);
        currentAccount.setGesture(gesturePwd);
        signinAccountInfoDao.update(currentAccount);
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
            recentLoginAccount.setExpiresIn(0);
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
//                long expiresIn = recentLoginAccount.getExpiresIn();
//                if (System.currentTimeMillis() > expiresIn) {
//                    return false;
//                } else {
//                    if ((System.currentTimeMillis() + 48 * 3600) > expiresIn) {
//                        EventBus.getDefault().post(new RefreshTokenEvent());
//                    }
                    return true;
//                }
            }
        }
    }
}
