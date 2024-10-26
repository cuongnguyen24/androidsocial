package com.utt.tt21.cc_modulelogin.detailstatus;

public class Comment {
    private String content;
    private String userId;
    private String userName;
    private String commentId;
    private String statusId; // Thêm thuộc tính statusId

    // Constructor
    public Comment(String content, String userId, String userName, String commentId, String statusId) {
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.commentId = commentId;
        this.statusId = statusId; // Gán giá trị cho statusId
    }

    // Getter
    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getStatusId() { // Thêm phương thức getter cho statusId
        return statusId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
