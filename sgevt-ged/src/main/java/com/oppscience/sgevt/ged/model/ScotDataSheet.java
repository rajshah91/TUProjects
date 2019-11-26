package com.oppscience.sgevt.ged.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class ScotDataSheet  {

  private String sirenCP;
  private String status;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date approbationDate;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date collectingDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date modificationDate;
  private String comment;

  private List<ScotFileMetadata> filesMetadata;

  public String getSirenCP() {
    return sirenCP;
  }

  public void setSirenCP(String sirenCP) {
    this.sirenCP = sirenCP;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getApprobationDate() {
    return approbationDate;
  }

  public void setApprobationDate(Date approbationDate) {
    this.approbationDate = approbationDate;
  }

  public Date getCollectingDate() {
    return collectingDate;
  }

  public void setCollectingDate(Date collectingDate) {
    this.collectingDate = collectingDate;
  }

  public Date getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(Date modificationDate) {
    this.modificationDate = modificationDate;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public List<ScotFileMetadata> getFilesMetadata() {
    return filesMetadata;
  }

  public void setFilesMetadata(List<ScotFileMetadata> filesMetadata) {
    this.filesMetadata = filesMetadata;
  }

}
