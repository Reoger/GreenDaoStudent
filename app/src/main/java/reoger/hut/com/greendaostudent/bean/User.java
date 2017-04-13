package reoger.hut.com.greendaostudent.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * Entity mapped to table "NOTE".
 */
@Entity(indexes = {
        @Index(value = "text, date DESC", unique = true)
})
public class User {

    @Id
    private Long id;

   // @NotNull
    private String text;
    private String comment;
    private java.util.Date date;
@Generated(hash = 207472317)
public User(Long id, String text, String comment, java.util.Date date) {
    this.id = id;
    this.text = text;
    this.comment = comment;
    this.date = date;
}
@Generated(hash = 586692638)
public User() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getText() {
    return this.text;
}
public void setText(String text) {
    this.text = text;
}
public String getComment() {
    return this.comment;
}
public void setComment(String comment) {
    this.comment = comment;
}
public java.util.Date getDate() {
    return this.date;
}
public void setDate(java.util.Date date) {
    this.date = date;
}


}
