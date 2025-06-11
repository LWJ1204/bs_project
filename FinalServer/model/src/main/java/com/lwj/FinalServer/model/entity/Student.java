package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 
 * @TableName studenttable
 */
@TableName(value ="studenttable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student extends BaseEntity{
    /**
     * 
     */
    @Schema(description = "学号")
    @TableField(value = "StudentId")
    private String studentid;

    /**
     * 
     */
    @Schema(description = "学生姓名")
    @TableId(value = "StudentName")
    private String studentname;

    /**
     * 
     */
    @Schema(description = "学生性别")
    @TableField(value = "StudentSex")
    private Integer studentsex;

    /**
     * 
     */
    @Schema(description = "出生日期")
    @TableField(value = "StudentBirth")
    private Date studentbirth;

    /**
     * 
     */
    @Schema(description = "学生联系方式")
    @TableField(value = "StudentPhone")
    private String studentphone;

    /**
     * 
     */
    @Schema(description = "所属班级")
    @TableField(value = "ClassId")
    private String classid;

    public Student(Student student){
        this.classid=student.classid;
        this.studentbirth=student.studentbirth;
        this.studentid=student.studentid;
        this.studentname=student.studentname;
        this.studentphone=student.studentphone;
        this.studentsex=student.studentsex;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Student other = (Student) that;
        return (this.getStudentid() == null ? other.getStudentid() == null : this.getStudentid().equals(other.getStudentid()))
            && (this.getStudentname() == null ? other.getStudentname() == null : this.getStudentname().equals(other.getStudentname()))
            && (this.getStudentsex() == null ? other.getStudentsex() == null : this.getStudentsex().equals(other.getStudentsex()))
            && (this.getStudentbirth() == null ? other.getStudentbirth() == null : this.getStudentbirth().equals(other.getStudentbirth()))
            && (this.getStudentphone() == null ? other.getStudentphone() == null : this.getStudentphone().equals(other.getStudentphone()))
            && (this.getClassid() == null ? other.getClassid() == null : this.getClassid().equals(other.getClassid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStudentid() == null) ? 0 : getStudentid().hashCode());
        result = prime * result + ((getStudentname() == null) ? 0 : getStudentname().hashCode());
        result = prime * result + ((getStudentsex() == null) ? 0 : getStudentsex().hashCode());
        result = prime * result + ((getStudentbirth() == null) ? 0 : getStudentbirth().hashCode());
        result = prime * result + ((getStudentphone() == null) ? 0 : getStudentphone().hashCode());
        result = prime * result + ((getClassid() == null) ? 0 : getClassid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", studentid=").append(studentid);
        sb.append(", studentname=").append(studentname);
        sb.append(", studentsex=").append(studentsex);
        sb.append(", studentbirth=").append(studentbirth);
        sb.append(", studentphone=").append(studentphone);
        sb.append(", classid=").append(classid);
        sb.append("]");
        return sb.toString();
    }
}