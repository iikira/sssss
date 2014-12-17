package com.szzjcs.commons.thirdapi.qiniu;

import java.io.Serializable;
import java.text.Normalizer;

public class AviInfo implements Serializable{
    private static final long serialVersionUID = 1;

    public static class Format implements Serializable{
        private static final long serialVersionUID = 1;
        private String duration;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "Format{" +
                    "duration='" + duration + '\'' +
                    '}';
        }
    }

    private Format format;

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "AviInfo{" +
                "format=" + format +
                '}';
    }
}
