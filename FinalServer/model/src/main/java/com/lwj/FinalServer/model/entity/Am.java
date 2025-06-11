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
 * @TableName amtable
 */
@TableName(value ="amtable")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Am extends BaseEntity{
    /**
     * 
     */
    @TableId(value = "AMId")
    @Schema(description = "专业id")
    private String amid;

    /**
     * 
     */
    @Schema(description = "学院名称")
    @TableField(value = "Academy")
    private String academy;

    /**
     * 
     */
    @Schema(description = "专业名称")
    @TableField(value = "Major")
    private String major;


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
        Am other = (Am) that;
        return (this.getAmid() == null ? other.getAmid() == null : this.getAmid().equals(other.getAmid()))
            && (this.getAcademy() == null ? other.getAcademy() == null : this.getAcademy().equals(other.getAcademy()))
            && (this.getMajor() == null ? other.getMajor() == null : this.getMajor().equals(other.getMajor()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAmid() == null) ? 0 : getAmid().hashCode());
        result = prime * result + ((getAcademy() == null) ? 0 : getAcademy().hashCode());
        result = prime * result + ((getMajor() == null) ? 0 : getMajor().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", amid=").append(amid);
        sb.append(", academy=").append(academy);
        sb.append(", major=").append(major);
        sb.append("]");
        return sb.toString();
    }
}