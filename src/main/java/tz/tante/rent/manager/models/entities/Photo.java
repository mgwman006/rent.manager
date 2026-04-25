package tz.tante.rent.manager.models.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class Photo
{
    @Id
    @Column(name = "photo_id")
    String photoId;

    @Column(name = "photo_path")
    String photoPath;


}
