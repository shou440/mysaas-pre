package com.xd;

//import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;
import org.weixin4j.loader.ITokenLoader;
import org.weixin4j.model.base.Token;

public class MyTokenLoader  implements ITokenLoader {

    private Token token = null;

    @Override
    public Token get() {
        return this.token != null && !StringUtils.isEmpty(this.token.getAccess_token()) && !this.token.isExprexpired() ? this.token : null;
    }

    @Override
    public void refresh(Token token) {
        if (null != token && !StringUtils.isEmpty(token.getAccess_token())) {
            if (token.getCreate_time() <= 0L) {
                throw new IllegalStateException("createtime can not be zero");
            } else if (token.isExprexpired()) {
                throw new IllegalStateException("access_token is exprexpired");
            } else {
                this.token = token;
            }
        } else {
            throw new IllegalStateException("access_token is null or empty");
        }
    }
}
