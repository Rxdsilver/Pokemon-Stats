package com.hippo.objects.stats;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Combination {

    private int size;
    private List<String> names;

    public Combination(int size, List<String> names) {
        this.size = size;
        this.names = names;
    }

    public int getSize() {
        return size;
    }

    public List<String> getNames() {
        return names;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Combination that = (Combination) o;
        return Objects.equals(names, that.names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(names);
    }
}
