package cn.aoe.sb.netty.common;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * @author zhaoruiqing
 * @version 1.0
 * @since 2022/8/24
 */
class Tests {
    @Test
    void test(){
        String aaaa = new String("aaa");
        String bbbb = new String("aaa");
        Assert.isTrue(aaaa.equals(bbbb),"等于");
    }
}
