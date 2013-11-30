package de.raptor2101.BattleWorldsKronos.Connector.Data.Entities;

import java.util.Date;

public class Message {
  private int mMessageId;
  private int mAuthorId;
  private String mAuthorName;
  private Date mTimestamp;
  private String mMessage;
  private int mLastMessageId;
  private boolean mIsSystemMessage;
  
  private boolean mIsReaded;
  private boolean mIsDiscarded;
  private boolean mIsDeleted;
  public int getMessageId() {
    return mMessageId;
  }
  public void setMessageId(int messageId) {
    this.mMessageId = messageId;
  }
  public int getAuthorId() {
    return mAuthorId;
  }
  public void setAuthorId(int authorId) {
    this.mAuthorId = authorId;
  }
  public String getAuthorName() {
    return mAuthorName;
  }
  public void setAuthorName(String authorName) {
    this.mAuthorName = authorName;
  }
  public Date getTimestamp() {
    return mTimestamp;
  }
  public void setTimestamp(Date timestamp) {
    this.mTimestamp = timestamp;
  }
  public String getMessage() {
    return mMessage;
  }
  public void setMessage(String message) {
    this.mMessage = message;
  }
  public int getLastMessageId() {
    return mLastMessageId;
  }
  public void setLastMessageId(int lastMessageId) {
    this.mLastMessageId = lastMessageId;
  }
  public boolean isSystemMessage() {
    return mIsSystemMessage;
  }
  public void setIsSystemMessage(boolean isSystemMessage) {
    this.mIsSystemMessage = isSystemMessage;
  }
  public boolean isReaded() {
    return mIsReaded;
  }
  public void setIsReaded(boolean isReaded) {
    this.mIsReaded = isReaded;
  }
  public boolean isDiscarded() {
    return mIsDiscarded;
  }
  public void setIsDiscarded(boolean isDiscarded) {
    this.mIsDiscarded = isDiscarded;
  }
  public boolean isDeleted() {
    return mIsDeleted;
  }
  public void setIsDeleted(boolean isDeleted) {
    this.mIsDeleted = isDeleted;
  }
}
