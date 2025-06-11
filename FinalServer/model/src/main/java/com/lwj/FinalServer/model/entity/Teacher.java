package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 
 * @TableName teachertable
 */
@TableName(value ="teachertable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Teacher extends BaseEntity {
    /**
     * 
     */

    @Schema(description = "工号")
    @TableId(value = "TeacherID")
    private String teacherid;

    /**
     * 
     */
    @Schema(description = "教师姓名")
    @TableField(value = "TeacherName")
    private String teachername;

    /**
     * 
     */
    @Schema(description = "教师性别")
    @TableField(value = "TeacherSex")
    private Integer teachersex;

    /**
     * 
     */
    @Schema(description = "联系方式")
    @TableField(value = "TeacherPhone")
    private String teacherphone;

    /**
     * 
     */
    @Schema(description = "出生日期")
    @TableField(value = "TeacherBirth")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date teacherbirth;

    /**
     * 
     */
    @Schema(description = "所属专业")
    @TableField(value = "AMId")
    private String amid;

    /**
     * 
     */
    @Schema(description = "教师职称")
    @TableField(value = "TeacherTitle")
    private Integer teachertitle;

    public Teacher(Teacher teacher) {
        this.teacherid=teacher.getTeacherid();
        this.teachersex=teacher.getTeachersex();
        this.amid=teacher.getAmid();
        this.teacherbirth=teacher.getTeacherbirth();
        this.teachername=teacher.getTeachername();
        this.teacherphone=teacher.getTeacherphone();
        this.teachertitle=teacher.getTeachertitle();
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
        Teacher other = (Teacher) that;
        return (this.getTeacherid() == null ? other.getTeacherid() == null : this.getTeacherid().equals(other.getTeacherid()))
            && (this.getTeachername() == null ? other.getTeachername() == null : this.getTeachername().equals(other.getTeachername()))
            && (this.getTeachersex() == null ? other.getTeachersex() == null : this.getTeachersex().equals(other.getTeachersex()))
            && (this.getTeacherphone() == null ? other.getTeacherphone() == null : this.getTeacherphone().equals(other.getTeacherphone()))
            && (this.getTeacherbirth() == null ? other.getTeacherbirth() == null : this.getTeacherbirth().equals(other.getTeacherbirth()))
            && (this.getAmid() == null ? other.getAmid() == null : this.getAmid().equals(other.getAmid()))
            && (this.getTeachertitle() == null ? other.getTeachertitle() == null : this.getTeachertitle().equals(other.getTeachertitle()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTeacherid() == null) ? 0 : getTeacherid().hashCode());
        result = prime * result + ((getTeachername() == null) ? 0 : getTeachername().hashCode());
        result = prime * result + ((getTeachersex() == null) ? 0 : getTeachersex().hashCode());
        result = prime * result + ((getTeacherphone() == null) ? 0 : getTeacherphone().hashCode());
        result = prime * result + ((getTeacherbirth() == null) ? 0 : getTeacherbirth().hashCode());
        result = prime * result + ((getAmid() == null) ? 0 : getAmid().hashCode());
        result = prime * result + ((getTeachertitle() == null) ? 0 : getTeachertitle().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", teacherid=").append(teacherid);
        sb.append(", teachername=").append(teachername);
        sb.append(", teachersex=").append(teachersex);
        sb.append(", teacherphone=").append(teacherphone);
        sb.append(", teacherbirth=").append(teacherbirth);
        sb.append(", amid=").append(amid);
        sb.append(", teachertitle=").append(teachertitle);
        sb.append("]");
        return sb.toString();
    }
}