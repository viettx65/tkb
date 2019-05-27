/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.domain;

import com.dvd.ckp.utils.Constants;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author dmin
 */
@Entity
@Table(name = "params")
@NamedQueries({
    @NamedQuery(name = "Param.fillAllParam", query = "FROM Param")
    ,
    @NamedQuery(name = "Param.fillParamActive", query = "FROM Param p WHERE p.status=1 ORDER BY p.createDate DESC")
    ,
    @NamedQuery(name = "Param.fillParamByKey", query = "FROM Param p WHERE p.status=1 AND p.paramKey = :paramKey")
})
public class Param implements Serializable {

    private Long paramId;
    private Long paramValue;
    private String paramKey;
    private String paramName;
    private Integer status;
    private Date createDate;
    private String paramKeyName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "param_id")

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    @Column(name = "param_value")
    public Long getParamValue() {
        return paramValue;
    }

    public void setParamValue(Long paramValue) {
        this.paramValue = paramValue;
    }

    @Column(name = "param_key")
    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    @Column(name = "param_name")
    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "create_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Transient
    public String getParamKeyName() {
        return paramKeyName;
    }

    public void setParamKeyName(String paramKeyName) {
        this.paramKeyName = Constants.getParamFromType(paramKey);
    }

}
