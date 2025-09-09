package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "labels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 1000)
    @Column(unique = true)
    private String name;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks;
}
