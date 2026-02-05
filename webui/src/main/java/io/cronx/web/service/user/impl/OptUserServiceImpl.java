package io.cronx.web.service.user.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.cronx.web.component.i18n.I18nAuthMsgKeys;
import io.cronx.web.mapper.OptUserMapper;
import io.cronx.web.model.entity.OptUserDO;
import io.cronx.web.model.fo.user.LoginFO;
import io.cronx.web.model.vo.user.LoginDataVO;
import io.cronx.web.service.security.JwtService;
import io.cronx.web.service.user.OptUserService;
import io.cronx.web.service.user.model.LoginMO;
import io.cronx.web.service.user.model.PasswordInfo;
import io.cronx.web.util.I18nUtil;
import io.cronx.web.webconfig.ConsoleConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OptUserServiceImpl implements OptUserService {

    @Resource
    private ConsoleConfig consoleConfig;

    @Resource
    private OptUserMapper userMapper;

    @Resource
    private JwtService    jwtService;

    @Override
    public LoginMO login(LoginFO loginFO) {
        return loginByPrimaryAccount(loginFO);
    }

    private LoginMO loginByPrimaryAccount(LoginFO loginFO) {
        OptUserDO user = userMapper.queryByAccountName(loginFO.getAccount());

        if (user == null) {
            return LoginMO.builder().success(false).errMsg(I18nUtil.getMessage(I18nAuthMsgKeys.USER_NOT_EXIST.name())).build();
        }

        if (user.isLoginLocked() && !isAccountLockTimeExpire(user)) {
            LoginDataVO loginDataVO = getLoginLimitationData(user);
            return LoginMO.builder().success(false).errMsg(loginDataVO.getLoginFailMsg()).build();
        }

        if (isUserLocked(user) || isErrorPasswd(user, loginFO)) {
            LoginDataVO loginDataVO = getLoginLimitationData(user);
            return LoginMO.builder().success(false).errMsg(loginDataVO.getLoginFailMsg()).build();
        }

        return loginDone(user);
    }

    private LoginMO loginDone(OptUserDO user) {
        long nowMs = System.currentTimeMillis();
        this.userMapper.updateLoginLimitInfo(new Date(nowMs), 0, false, user.getId());
        return LoginMO.builder().success(true).username(user.getUsername()).token(this.jwtService.genJwtToken(user)).build();
    }

    protected boolean isErrorPasswd(OptUserDO user, LoginFO loginFO) {
        return !BCryptOneWayCryptService.getInstance().isMatch(PasswordInfo.builder().encryptPassword(user.getPassword()).plainPassword(loginFO.getPassword()).build());
    }

    protected boolean isUserLocked(OptUserDO user) {
        return user.isLoginLocked() && !isAccountLockTimeExpire(user);
    }

    protected boolean isAccountLockTimeExpire(OptUserDO userDO) {
        return System.currentTimeMillis() - userDO.getLastTryLoginTime().getTime() > Integer.parseInt(consoleConfig.getResetLoginLimitationWaitTimeMin()) * 60 * 1000;
    }

    protected boolean isExceedLoginFailCount(OptUserDO userDO) {
        return userDO.getLoginFailCount() + 1 >= Integer.parseInt(consoleConfig.getRetryLoginMaxCount());
    }

    public LoginDataVO getLoginLimitationData(OptUserDO userDO) {
        if (isAccountLockTimeExpire(userDO)) {
            return resetLoginLimit(userDO);
        } else if (userDO.isLoginLocked()) {
            return loginLimiting(userDO);
        } else if (isExceedLoginFailCount(userDO)) {
            return loginLimitBeyondMaxRetry(userDO);
        } else {
            return getLoginDataWhenNotLimited(userDO);
        }
    }

    /** After wait for a period of time, reset login limitation */
    private LoginDataVO resetLoginLimit(OptUserDO userDO) {
        userDO.setLoginLocked(false);
        userDO.setLoginFailCount(0);
        return getLoginDataWhenNotLimited(userDO);
    }

    private LoginDataVO loginLimitBeyondMaxRetry(OptUserDO userDO) {
        long nowMs = System.currentTimeMillis();

        userMapper.updateLoginLimitInfo(new Date(nowMs), userDO.getLoginFailCount() + 1, true, userDO.getId());

        long needWaitSeconds = Integer.parseInt(consoleConfig.getResetLoginLimitationWaitTimeMin()) * 60L -
                               (System.currentTimeMillis() - userDO.getLastTryLoginTime().getTime()) / 1000;
        String errMsg = I18nUtil.getMessage(I18nAuthMsgKeys.ACCOUNT_LOCKED.name(), String.valueOf(userDO.getLoginFailCount() + 1), String.valueOf(needWaitSeconds));

        return LoginDataVO.builder().lastTryLoginTimeMs(nowMs).locked(true).loginFailCount(Integer.parseInt(consoleConfig.getRetryLoginMaxCount())).loginFailMsg(errMsg).build();
    }

    private LoginDataVO loginLimiting(OptUserDO userDO) {
        long needWaitSeconds = Integer.parseInt(consoleConfig.getResetLoginLimitationWaitTimeMin()) * 60L -
                               (System.currentTimeMillis() - userDO.getLastTryLoginTime().getTime()) / 1000;
        String errMsg = I18nUtil.getMessage(I18nAuthMsgKeys.ACCOUNT_LOCKED.name(), consoleConfig.getRetryLoginMaxCount(), String.valueOf(needWaitSeconds));
        return LoginDataVO.builder()
            .lastTryLoginTimeMs(userDO.getLastTryLoginTime().getTime())
            .locked(userDO.isLoginLocked())
            .loginFailCount(userDO.getLoginFailCount())
            .loginFailMsg(errMsg)
            .build();
    }

    private LoginDataVO getLoginDataWhenNotLimited(OptUserDO userDO) {
        LoginDataVO loginDataVO = new LoginDataVO();
        long nowMs = System.currentTimeMillis();

        userMapper.updateLoginLimitInfo(new Date(nowMs), userDO.getLoginFailCount() + 1, false, userDO.getId());

        loginDataVO.setLocked(false);
        loginDataVO.setLastTryLoginTimeMs(nowMs);
        loginDataVO.setLoginFailCount(userDO.getLoginFailCount() + 1);
        loginDataVO.setLoginFailMsg(I18nUtil
            .getMessage(I18nAuthMsgKeys.ERROR_PASSWORD.name(), String.valueOf(userDO.getLoginFailCount() + 1), consoleConfig.getRetryLoginMaxCount()));
        return loginDataVO;
    }
}
