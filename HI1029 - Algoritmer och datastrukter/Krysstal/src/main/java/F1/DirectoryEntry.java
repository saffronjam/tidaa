package F1;

import java.util.Objects;

public class DirectoryEntry {
    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DirectoryEntry other = (DirectoryEntry) object;
        return Objects.equals(name, other.name);
    }
}
