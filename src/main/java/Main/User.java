package Main;

import java.util.Objects;

public class User implements Comparable<User> {

    public String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public int compareTo(User anotherUser) {

        return this.name.compareTo(anotherUser.name);
//        return anotherUser.name.compareTo(this.name);
    }
}
