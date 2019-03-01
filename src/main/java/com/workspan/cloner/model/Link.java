package com.workspan.cloner.model;

/**
 * User: jeyanthan
 * Date: 2019-02-27
 * Add description here
 */
public class Link {
    Integer from;
    Integer to;

    public Link(Integer from, Integer to){
        this.from = from;
        this.to = to;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (from.hashCode() + to.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Link other = (Link) obj;
        if (this.from == null && this.to == null) {
            if (other.from != null && other.to != null)
                return false;
        } else if (!this.from.equals(other.from) && !this.to.equals(other.to))
            return false;
        return true;
    }

    public Integer getTo() {
        return this.to;
    }

    public Integer getFrom() {
        return this.from;
    }

}
