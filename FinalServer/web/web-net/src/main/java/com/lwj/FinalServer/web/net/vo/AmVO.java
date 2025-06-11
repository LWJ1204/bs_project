package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.Am;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class AmVO extends Am{

    @Setter
    @Getter
    public static class Major{
        String label;
        String value;

        public Major(String label, String value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Major{" +
                    "label='" + label + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
    public List<Major> children;

    public AmVO(String label, List<Major> majors) {
        this.setAcademy(label);




        children = majors;
    }

    public AmVO() {
        children=new ArrayList<>();
    }

    public AmVO(String label, String value,String major,String majorid ) {
        this.setAcademy(label);
        this.children=new ArrayList<>();
        this.children.add(new Major(major, majorid));
    }

    @Override
    public String toString() {
        return "AmVO{" +
                "label='" + getAcademy() + '\'' +
                ", Majors=" + children +
                '}';
    }
}
