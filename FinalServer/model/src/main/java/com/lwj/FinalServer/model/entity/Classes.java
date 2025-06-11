package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @TableName classtable
 */
@TableName(value ="classtable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Classes extends BaseEntity {
    /**
     *
     */

    @Schema(description = "班级id")
    @TableId(value = "ClassId", type = IdType.AUTO)
    private Integer classid;

    /**
     *
     */
    @Schema(description = "班级名称")
    @TableField(value = "ClassName")
    private String classname;

    /**
     *
     */
    @Schema(description = "班级人数")
    @TableField(value = "ClassNum")
    private Integer classnum;

    /**
     *
     */
    @Schema(description = "所属专业")
    @TableField(value = "AMId")
    private String amid;

    /**
     *
     */
    @Schema(description = "班主任")
    @TableField(value = "TeacherId")
    private String teacherid;

    /**
     *
     */
    @Schema(description = "班长")
    @TableField(value = "StudentId")
    private String studentid;

    @Schema(description = "班级已有人数")
    @TableField(value = "SelectNum")
    private Integer selectNum;


    public Classes(Classes t){
        this.classid=t.classid;
        this.amid=t.amid;
        this.classname=t.classname;
        this.classnum=t.classnum;
        this.studentid=t.studentid;
        this.teacherid=t.teacherid;
        this.selectNum=t.selectNum;
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
        Classes other = (Classes) that;
        return (this.getClassid() == null ? other.getClassid() == null : this.getClassid().equals(other.getClassid()))
                && (this.getClassname() == null ? other.getClassname() == null : this.getClassname().equals(other.getClassname()))
                && (this.getClassnum() == null ? other.getClassnum() == null : this.getClassnum().equals(other.getClassnum()))
                && (this.getAmid() == null ? other.getAmid() == null : this.getAmid().equals(other.getAmid()))
                && (this.getTeacherid() == null ? other.getTeacherid() == null : this.getTeacherid().equals(other.getTeacherid()))
                && (this.getStudentid() == null ? other.getStudentid() == null : this.getStudentid().equals(other.getStudentid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getClassid() == null) ? 0 : getClassid().hashCode());
        result = prime * result + ((getClassname() == null) ? 0 : getClassname().hashCode());
        result = prime * result + ((getClassnum() == null) ? 0 : getClassnum().hashCode());
        result = prime * result + ((getAmid() == null) ? 0 : getAmid().hashCode());
        result = prime * result + ((getTeacherid() == null) ? 0 : getTeacherid().hashCode());
        result = prime * result + ((getStudentid() == null) ? 0 : getStudentid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", classid=").append(classid);
        sb.append(", classname=").append(classname);
        sb.append(", classnum=").append(classnum);
        sb.append(", amid=").append(amid);
        sb.append(", teacherid=").append(teacherid);
        sb.append(", studentid=").append(studentid);
        sb.append("]");
        return sb.toString();
    }
}