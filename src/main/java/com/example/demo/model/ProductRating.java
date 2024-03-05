package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ProductRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName="id")
    private User user;
    private int rating;
    private String comment;
    private int reportVotes;
    private LocalDateTime dateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getReportVotes() {
        return reportVotes;
    }

    public void setReportVotes(int reportVotes) {
        this.reportVotes = reportVotes;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ProductRating(Long id, Long productId, User user, int rating, String comment, int reportVotes, LocalDateTime dateTime) {
        this.id = id;
        this.productId = productId;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.reportVotes = reportVotes;
        this.dateTime = dateTime;
    }

    public ProductRating() {
    }
}
