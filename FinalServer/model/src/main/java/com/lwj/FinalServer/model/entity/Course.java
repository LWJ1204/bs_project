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
 * @TableName coursetable
 */
@TableName(value ="coursetable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course extends BaseEntity {
    /**
     * 
     */

    @Schema(description = "课程")
    @TableId(value = "CourseId")
    private String courseid;

    /**
     * 
     */
    @Schema(description = "课程名称")
    @TableField(value = "CourseName")
    private String coursename;

    /**
     * 
     */
    @Schema(description = "授课教师Id")
    @TableField(value = "TeacherId")
    private String teacherid;

    /**
     * 
     */
    @Schema(description = "授课教室Id")
    @TableField(value = "ClassRoomId")
    private String classroomid;

    /**
     * 
     */
    private Double coursevalue;

    /**
     * 
     */
    @Schema(description = "结课形式")
    @TableField(value = "CourseFinish")
    private String coursefinish;

    /**
     * 
     */
    @Schema(description = "开课人数")
    @TableField(value = "CourseNum")
    private Integer coursenum;

    /**
     * 
     */
    @Schema(description = "开课时间")
    @TableField(value = "CourseTime")
    private String coursetime;

    /**
     * 
     */
    @Schema(description ="开课年份")
    @TableField(value = "CourseYear")
    private String courseyear;

    /**
     * 
     */
    @Schema(description = "开课学期")
    @TableField(value = "CourseSemester")
    private Integer coursesemester;

    /**
     * 
     */
    @Schema(description = "所属专业")
    @TableField(value = "Academy")
    private String academy;

    public Course(Course c){
        this.academy=c.getAcademy();
        this.classroomid=c.getClassroomid();
        this.coursefinish=c.getCoursefinish();
        this.courseid=c.getCourseid();
        this.coursename=c.getCoursename();
        this.coursenum=c.getCoursenum();
        this.coursesemester=c.getCoursesemester();
        this.coursetime=c.getCoursetime();
        this.coursevalue=c.getCoursevalue();
        this.courseyear=c.getCourseyear();
        this.teacherid=c.getTeacherid();
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
        Course other = (Course) that;
        return (this.getCourseid() == null ? other.getCourseid() == null : this.getCourseid().equals(other.getCourseid()))
            && (this.getCoursename() == null ? other.getCoursename() == null : this.getCoursename().equals(other.getCoursename()))
            && (this.getTeacherid() == null ? other.getTeacherid() == null : this.getTeacherid().equals(other.getTeacherid()))
            && (this.getClassroomid() == null ? other.getClassroomid() == null : this.getClassroomid().equals(other.getClassroomid()))
            && (this.getCoursevalue() == null ? other.getCoursevalue() == null : this.getCoursevalue().equals(other.getCoursevalue()))
            && (this.getCoursefinish() == null ? other.getCoursefinish() == null : this.getCoursefinish().equals(other.getCoursefinish()))
            && (this.getCoursenum() == null ? other.getCoursenum() == null : this.getCoursenum().equals(other.getCoursenum()))
            && (this.getCoursetime() == null ? other.getCoursetime() == null : this.getCoursetime().equals(other.getCoursetime()))
            && (this.getCourseyear() == null ? other.getCourseyear() == null : this.getCourseyear().equals(other.getCourseyear()))
            && (this.getCoursesemester() == null ? other.getCoursesemester() == null : this.getCoursesemester().equals(other.getCoursesemester()))
            && (this.getAcademy() == null ? other.getAcademy() == null : this.getAcademy().equals(other.getAcademy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCourseid() == null) ? 0 : getCourseid().hashCode());
        result = prime * result + ((getCoursename() == null) ? 0 : getCoursename().hashCode());
        result = prime * result + ((getTeacherid() == null) ? 0 : getTeacherid().hashCode());
        result = prime * result + ((getClassroomid() == null) ? 0 : getClassroomid().hashCode());
        result = prime * result + ((getCoursevalue() == null) ? 0 : getCoursevalue().hashCode());
        result = prime * result + ((getCoursefinish() == null) ? 0 : getCoursefinish().hashCode());
        result = prime * result + ((getCoursenum() == null) ? 0 : getCoursenum().hashCode());
        result = prime * result + ((getCoursetime() == null) ? 0 : getCoursetime().hashCode());
        result = prime * result + ((getCourseyear() == null) ? 0 : getCourseyear().hashCode());
        result = prime * result + ((getCoursesemester() == null) ? 0 : getCoursesemester().hashCode());
        result = prime * result + ((getAcademy() == null) ? 0 : getAcademy().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", courseid=").append(courseid);
        sb.append(", coursename=").append(coursename);
        sb.append(", teacherid=").append(teacherid);
        sb.append(", classroomid=").append(classroomid);
        sb.append(", coursevalue=").append(coursevalue);
        sb.append(", coursefinish=").append(coursefinish);
        sb.append(", coursenum=").append(coursenum);
        sb.append(", coursetime=").append(coursetime);
        sb.append(", courseyear=").append(courseyear);
        sb.append(", coursesemester=").append(coursesemester);
        sb.append(", academy=").append(academy);
        sb.append("]");
        return sb.toString();
    }
}