package cn.newphy.orm.demo.entity;

import java.util.Date;
import javax.persistence.Version;

/**
*
* 描述： 实体Bean
*
* @创建人： liuhui18
*
* @创建时间：2018年05月31日 18:53:50
*
* @Copyright (c) 深圳市小牛科技有限公司-版权所有
*/
public class Attest extends IdEntity {

   private static final long serialVersionUID = 8677553741343790874L;

   /**
    *业务方编号
    */
   private String partnerId;

   /**
    *供应商编号
    */
   private String supplierId;

   /**
    *请求编号
    */
   private Long requestId;

   /**
    *文件编号
    */
   private String fileId;

   /**
    *文件名
    */
   private String fileName;

   /**
    *文件长度
    */
   private Long fileLength;

   /**
    *hash算法
    */
   private String hashAlgorithm;

   /**
    *文件hash
    */
   private String fileHash;

   /**
    *文件创建时间
    */
   private Date fileTime;

   /**
    *存证编号
    */
   private String attestId;

   /**
    *存证状态(0:未存证/1:存证成功/2:存证失败)
    */
   private Integer status;

    /**
     * 版本
     */
   private Integer version;
   /**
    *说明
    */
   private String remark;


   public void setPartnerId(String partnerId){
         this.partnerId = partnerId == null ? null : partnerId.trim();
   }

   public String getPartnerId(){
       return partnerId;
   }

   public void setSupplierId(String supplierId){
         this.supplierId = supplierId == null ? null : supplierId.trim();
   }

   public String getSupplierId(){
       return supplierId;
   }

   public void setRequestId(Long requestId){
         this.requestId = requestId;
   }

   public Long getRequestId(){
       return requestId;
   }

   public void setFileId(String fileId){
         this.fileId = fileId == null ? null : fileId.trim();
   }

   public String getFileId(){
       return fileId;
   }

   public void setFileName(String fileName){
         this.fileName = fileName == null ? null : fileName.trim();
   }

   public String getFileName(){
       return fileName;
   }

   public void setFileLength(Long fileLength){
         this.fileLength = fileLength;
   }

   public Long getFileLength(){
       return fileLength;
   }

   public void setHashAlgorithm(String hashAlgorithm){
         this.hashAlgorithm = hashAlgorithm == null ? null : hashAlgorithm.trim();
   }

   public String getHashAlgorithm(){
       return hashAlgorithm;
   }

   public void setFileHash(String fileHash){
         this.fileHash = fileHash == null ? null : fileHash.trim();
   }

   public String getFileHash(){
       return fileHash;
   }

   public void setFileTime(Date fileTime){
         this.fileTime = fileTime;
   }

   public Date getFileTime(){
       return fileTime;
   }

   public void setAttestId(String attestId){
         this.attestId = attestId == null ? null : attestId.trim();
   }

   public String getAttestId(){
       return attestId;
   }

   public void setStatus(Integer status){
         this.status = status;
   }

   public Integer getStatus(){
       return status;
   }

   public void setRemark(String remark){
         this.remark = remark == null ? null : remark.trim();
   }

   public String getRemark(){
       return remark;
   }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

