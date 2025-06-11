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
 * @TableName classroomtable
 */
@TableName(value ="classroomtable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Classroom extends BaseEntity{
    /**
     * 
     */

    @Schema(description = "教室id")
    @TableId(value = "ClassRoomId")
    private String classroomid;

    /**
     * 
     */
    @Schema(description ="教室门牌")
    @TableField(value = "ClassRoomName")
    private String classroomname;

    /**
     * 
     */
    @Schema(description = "楼层")
    @TableField(value = "ClassRoomLou")
    private String classroomlou;

    /**
     * 
     */
    @Schema(description = "区域")
    @TableField(value = "ClassRoomQu")
    private String classroomqu;

    /**
     * 
     */
    @Schema(description = "层")
    @TableField(value = "ClassRoomCeng")
    private Integer classroomceng;

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
        Classroom other = (Classroom) that;
        return (this.getClassroomid() == null ? other.getClassroomid() == null : this.getClassroomid().equals(other.getClassroomid()))
            && (this.getClassroomname() == null ? other.getClassroomname() == null : this.getClassroomname().equals(other.getClassroomname()))
            && (this.getClassroomlou() == null ? other.getClassroomlou() == null : this.getClassroomlou().equals(other.getClassroomlou()))
            && (this.getClassroomqu() == null ? other.getClassroomqu() == null : this.getClassroomqu().equals(other.getClassroomqu()))
            && (this.getClassroomceng() == null ? other.getClassroomceng() == null : this.getClassroomceng().equals(other.getClassroomceng()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getClassroomid() == null) ? 0 : getClassroomid().hashCode());
        result = prime * result + ((getClassroomname() == null) ? 0 : getClassroomname().hashCode());
        result = prime * result + ((getClassroomlou() == null) ? 0 : getClassroomlou().hashCode());
        result = prime * result + ((getClassroomqu() == null) ? 0 : getClassroomqu().hashCode());
        result = prime * result + ((getClassroomceng() == null) ? 0 : getClassroomceng().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", classroomid=").append(classroomid);
        sb.append(", classroomname=").append(classroomname);
        sb.append(", classroomlou=").append(classroomlou);
        sb.append(", classroomqu=").append(classroomqu);
        sb.append(", classroomceng=").append(classroomceng);
        sb.append("]");
        return sb.toString();
    }
}