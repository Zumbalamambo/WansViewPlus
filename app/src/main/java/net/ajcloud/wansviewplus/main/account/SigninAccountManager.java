package net.ajcloud.wansviewplus.main.account;

import android.text.TextUtils;

import net.ajcloud.wansviewplus.entity.SigninAccountInfo;
import net.ajcloud.wansviewplus.entity.SigninAccountInfoDao;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.SigninBean;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;

/**
 * Created by mamengchao on 2018/05/30.
 * Function:登录账号信息管理类
 */
public class SigninAccountManager {

    private static SigninAccountManager instance;
    private SigninAccountInfoDao signinAccountInfoDao;

    private SigninAccountManager() {
        signinAccountInfoDao = MainApplication.getApplication().getDaoSession().getSigninAccountInfoDao();
    }

    public static SigninAccountManager getInstance() {
        if (instance == null) {
            instance = new SigninAccountManager();
        }
        return instance;
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
            recentLoginAccount.setSignToken(null);
            recentLoginAccount.setAccessExpiresIn(0);
            recentLoginAccount.setRefreshExpiresIn(0);
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
            String encodeSign = CipherUtil.naclEncodeLocal(bean.signToken, salt);
            bean.accessToken = encodeAccess;
            bean.refreshToken = encodeRefresh;
            bean.signToken = encodeSign;
            info = new SigninAccountInfo(mail, encodePwd, salt, bean);
            signinAccountInfoDao.insert(info);
        } else {
            String salt = info.getSalt();
            String encodePwd = CipherUtil.naclEncodeLocal(password, salt);
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            String encodeSign = CipherUtil.naclEncodeLocal(bean.signToken, salt);
            info.setIsRecent(true);
            info.setAccessToken(encodeAccess);
            info.setRefreshToken(encodeRefresh);
            info.setSignToken(encodeSign);
            info.setAccessExpiresIn(System.currentTimeMillis() / 1000 + bean.accessExpiresIn);
            info.setRefreshExpiresIn(System.currentTimeMillis() / 1000 + bean.refreshExpiresIn);
            info.setTokenType(bean.tokenType);
            info.setScope(bean.tokenType);
            info.setPassword(encodePwd);
            info.setSalt(salt);
            signinAccountInfoDao.update(info);
        }
    }

    /**
     * 刷新当前登录账号token信息
     */
    public void refreshCurrentAccount(SigninBean bean) {
        SigninAccountInfo info = getCurrentAccount();
        if (info != null) {
            String salt = info.getSalt();
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            String encodeSign = CipherUtil.naclEncodeLocal(bean.signToken, salt);
            info.setAccessToken(encodeAccess);
            info.setRefreshToken(encodeRefresh);
            info.setSignToken(encodeSign);
            info.setAccessExpiresIn(bean.accessExpiresIn + System.currentTimeMillis() / 1000);
            info.setRefreshExpiresIn(bean.refreshExpiresIn + System.currentTimeMillis() / 1000);
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

    public long getCurrentAccountAccessTokenTime() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return 0;
        }
        return recentLoginAccount.getAccessExpiresIn();
    }

    public long getCurrentAccountRefreshTokenTime() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return 0;
        }
        return recentLoginAccount.getRefreshExpiresIn();
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

    public String getCurrentSignToken() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return null;
        }
        String signToken = recentLoginAccount.getSignToken();
        String salt = recentLoginAccount.getSalt();
        if (TextUtils.isEmpty(signToken) || TextUtils.isEmpty(salt)) {
            return null;
        }
        return CipherUtil.naclDecodeLocal(signToken, salt);
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
        if (TextUtils.isEmpty(gesture)) {
            currentAccount.setGesture("");
        } else {
            String salt = currentAccount.getSalt();
            String gesturePwd = CipherUtil.naclEncodeLocal(gesture, salt);
            currentAccount.setGesture(gesturePwd);
        }
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
            recentLoginAccount.setSignToken(null);
            recentLoginAccount.setAccessExpiresIn(0);
            recentLoginAccount.setRefreshExpiresIn(0);
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
                long expiresIn = recentLoginAccount.getRefreshExpiresIn();
                return System.currentTimeMillis() / 1000 < expiresIn;
            }
        }
    }
}
