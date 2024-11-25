package ubb.scs.map.demosocialnetwork.domain;


import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Long> {
    Long idUser1;
    Long idUser2;
    LocalDateTime date;
    String status;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(idUser1, that.idUser1) && Objects.equals(idUser2, that.idUser2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser1, idUser2);
    }

    public Friendship(Long idUser1, Long idUser2, String status, LocalDateTime date) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.date = date;
        this.status = status;
    }

    public Long getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(Long idUser1) {
        this.idUser1 = idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(Long idUser2) {
        this.idUser2 = idUser2;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
