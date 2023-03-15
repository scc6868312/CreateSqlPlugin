package com.scc.createsqlplugin.config;

import java.io.IOException;

/**
 * @ProjectName ice
 * @Package sqlscript
 * @Description
 * @Author plus
 * @Date 2022/8/9 14:23
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public interface ReadFileCallBack {
    void otherDeal(String line) throws IOException;
}
