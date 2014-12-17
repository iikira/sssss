package com.szzjcs.commons.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtilsTest {

    @Test
    public void testIntersection() throws Exception {
        List<TestBean> list1 = new ArrayList<>();
        TestBean bean = new TestBean();
        bean.setUrl("1.jpg");
        list1.add(bean);
        bean = new TestBean();
        bean.setUrl("2.jpg");
        list1.add(bean);
        bean = new TestBean();
        bean.setUrl("3.jpg");
        list1.add(bean);

        List<TestBean> list2 = new ArrayList<>();
        bean = new TestBean();
        bean.setId(34);
        bean.setUrl("1.jpg");
        list2.add(bean);
        bean = new TestBean();
        bean.setUrl("5.jpg");
        list2.add(bean);
        bean = new TestBean();
        bean.setUrl("3.jpg");
        list2.add(bean);

        System.out.println(CollectionUtils.intersection(list1, list2,"id", "url"));
    }

    @Test
    public void testDifference() throws Exception {
        List<TestBean> list1 = new ArrayList<>();
        TestBean bean = new TestBean();
        bean.setUrl("1.jpg");
        list1.add(bean);
        bean = new TestBean();
        bean.setUrl("2.jpg");
        list1.add(bean);
        bean = new TestBean();
        bean.setUrl("3.jpg");
        list1.add(bean);

        List<TestBean> list2 = new ArrayList<>();
        bean = new TestBean();
        bean.setId(34);
        bean.setUrl("1.jpg");
        list2.add(bean);
        bean = new TestBean();
        bean.setUrl("5.jpg");
        list2.add(bean);
        bean = new TestBean();
        bean.setUrl("3.jpg");
        list2.add(bean);

        System.out.println(CollectionUtils.difference(list1, list2, "url"));
    }

    public static class TestBean{
        private int id;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "TestBean{" +
                    "id=" + id +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}