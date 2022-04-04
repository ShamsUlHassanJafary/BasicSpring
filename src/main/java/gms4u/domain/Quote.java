package gms4u.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The Quote entity.\n@author A true hipster
 */
@ApiModel(description = "The Quote entity.\n@author A true hipster")
@Entity
@Table(name = "quote")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Quote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * fieldName
     */
    @ApiModelProperty(value = "fieldName")
    @Column(name = "quote_date")
    private LocalDate quoteDate;

    @Column(name = "quote_total", precision = 21, scale = 2)
    private BigDecimal quoteTotal;

    @Min(0)
    @Max(100)
    @Column(name = "discount", precision = 21, scale = 2)
    private BigDecimal discount;

    @OneToOne(mappedBy = "quote")
//    @JsonIgnore
    private Booking booking;

    @Column(name = "reference")
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
    }

    public Quote quoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
        return this;
    }

    public BigDecimal getQuoteTotal() {
        return quoteTotal;
    }

    public void setQuoteTotal(BigDecimal quoteTotal) {
        this.quoteTotal = quoteTotal;
    }

    public Quote quoteTotal(BigDecimal quoteTotal) {
        this.quoteTotal = quoteTotal;
        return this;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Quote booking(Booking booking) {
        this.booking = booking;
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quote)) {
            return false;
        }
        return id != null && id.equals(((Quote) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Quote{" +
            "id=" + getId() +
            ", quoteDate='" + getQuoteDate() + "'" +
            ", quoteTotal=" + getQuoteTotal() +
            "}";
    }
}
