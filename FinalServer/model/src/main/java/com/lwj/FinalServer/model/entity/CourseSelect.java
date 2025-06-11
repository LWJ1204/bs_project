package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName courseselecttable
 */
@TableName(value ="courseselecttable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseSelect extends BaseEntity{
    /**
     * 
     */

    @Schema(description = "课程号")
    @TableId(value = "CourseId")
    private String courseid;

    /**
     * 
     */
    @Schema(description = "学号")
    @TableField(value = "StudentId")
    private String studentid;

    /**
     * 
     */
    @Schema(description = "选课状态")
    @TableField(value = "CourseState")
    private Integer coursestate;

    public CourseSelect(CourseSelect cs){
        this.courseid=cs.getCourseid();
        this.coursestate=cs.getCoursestate();
        this.studentid=cs.getStudentid();
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
        CourseSelect other = (CourseSelect) that;
        return (this.getCourseid() == null ? other.getCourseid() == null : this.getCourseid().equals(other.getCourseid()))
            && (this.getStudentid() == null ? other.getStudentid() == null : this.getStudentid().equals(other.getStudentid()))
            && (this.getCoursestate() == null ? other.getCoursestate() == null : this.getCoursestate().equals(other.getCoursestate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCourseid() == null) ? 0 : getCourseid().hashCode());
        result = prime * result + ((getStudentid() == null) ? 0 : getStudentid().hashCode());
        result = prime * result + ((getCoursestate() == null) ? 0 : getCoursestate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", courseid=").append(courseid);
        sb.append(", studentid=").append(studentid);
        sb.append(", coursestate=").append(coursestate);
        sb.append("]");
        return sb.toString();
    }
}