package com.lwj.FinalServer.web.net.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MenuItem {
        @Setter
        @Getter
        private String path;
        @Setter
        @Getter
        private String name;
        @Setter
        @Getter
        private String label;
        @Setter
        @Getter
        private String url;

}
