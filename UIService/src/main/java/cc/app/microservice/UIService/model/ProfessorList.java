package cc.app.microservice.UIService.model;

import java.io.Serializable;
import java.util.List;

public class ProfessorList implements Serializable {

    private List<Professor> list;

    public ProfessorList() {}

    public ProfessorList(List<Professor> list) {
        this.list = list;
    }

    public List<Professor> getList() {
        return list;
    }

    public void setList(List<Professor> list) {
        this.list = list;
    }
}
