package com.caicai.testchart;

/**
 * Created by cai on 2017/2/13.
 */

public class Element {

    private String name;
    private int ka, kb, la, lb;

    public Element(String name, int ka, int kb, int la, int lb) {
        this.name = name;
        this.ka = ka;
        this.kb = kb;
        this.la = la;
        this.lb = lb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKa() {
        return ka;
    }

    public void setKa(int ka) {
        this.ka = ka;
    }

    public int getKb() {
        return kb;
    }

    public void setKb(int kb) {
        this.kb = kb;
    }

    public int getLa() {
        return la;
    }

    public void setLa(int la) {
        this.la = la;
    }

    public int getLb() {
        return lb;
    }

    public void setLb(int lb) {
        this.lb = lb;
    }

    @Override
    public String toString() {
        return
                "name='" + name + '\'' +
                        ", ka=" + ka +
                        ", kb=" + kb +
                        ", la=" + la +
                        ", lb=" + lb;
    }


}
