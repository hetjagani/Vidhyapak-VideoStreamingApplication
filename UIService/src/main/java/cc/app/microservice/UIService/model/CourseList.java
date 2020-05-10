package cc.app.microservice.UIService.model;

import java.io.Serializable;
import java.util.List;

public class CourseList implements Serializable {
    private List<Course> list;

    public CourseList() {}

    public CourseList(List<Course> list) {
        this.list = list;
    }

    public List<Course> getList() {
        return list;
    }

    public void setList(List<Course> list) {
        this.list = list;
    }
}
