package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@TableName(value ="user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class mUser extends BaseEntity {
    @TableId(value = "Id")
    @Schema(description = "用户id")
    private String id;

    /**
     *
     */
    @Schema(description = "UserName")
    @TableField(value = "UserName")
    private String username;

    /**
     *
     */
    @Schema(description = "Password")
    @TableField(value = "PassWord")
    private String password;

    @Schema(description = "身份")
    @TableField(value = "Role")
    private Integer role;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof mUser user)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getRole(), user.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getUsername(), getPassword(), getRole());
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", UserName='" + username + '\'' +
                ", PassWord='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
