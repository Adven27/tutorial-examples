package javaeetutorial.batch.messagesender.items;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "TEXT")
    private String text;
}