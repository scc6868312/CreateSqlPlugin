package com.scc.createsqlplugin.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Oracle extends BaseDatabase {
    @XmlAttribute
    private String serviceName;



    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
