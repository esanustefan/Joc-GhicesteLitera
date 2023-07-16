package org.example;

import java.util.Objects;

public class Jucator extends Entity<Long>{
    private String username;

    public Jucator(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jucator jucator)) return false;
        return Objects.equals(getUsername(), jucator.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }

    @Override
    public String toString() {
        return "Jucator{" +
                "username='" + username + '\'' +
                '}';
    }
}
