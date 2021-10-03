package ro.uaic.info.hello.helloworld.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class RepositoryEntry implements Comparable<RepositoryEntry> {
    private String key;
    private int value;
    private Timestamp timestamp;

    @Override
    public int compareTo(RepositoryEntry repositoryEntry) {
        return this.key.compareTo(repositoryEntry.getKey());
    }
}
