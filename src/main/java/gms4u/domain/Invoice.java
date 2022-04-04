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
import java.time.ZonedDateTime;

/**
 * The Invoice entity.\n@author A true hipster
 */
@ApiModel(description = "The Invoice entity.\n@author A true hipster")
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "fieldName")
    @Column(name = "issue_date")
    private LocalDate issueDate;

    /**
     * fieldName
     */
    @ApiModelProperty(value = "fieldName")
    @Column(name = "invoice_date")
    private ZonedDateTime invoiceDate;

    @Column(name = "invoice_total", precision = 21, scale = 2)
    private BigDecimal invoiceTotal;

    @Column(name = "paid", precision = 21, scale = 2)
    private BigDecimal paid;

    @Column(name = "balance", precision = 21, scale = 2)
    private BigDecimal balance;
    @Min(0)
    @Max(100)
    @Column(name = "discount", precision = 21, scale = 2)
    private BigDecimal discount;
    @OneToOne(mappedBy = "invoice")
    private Booking booking;

    @Column(name = "reference")
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(ZonedDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Invoice invoiceDate(ZonedDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
        return this;
    }

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public Invoice invoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
        return this;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Invoice booking(Booking booking) {
        this.booking = booking;
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return id != null && id.equals(((Invoice) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", invoiceDate='" + getInvoiceDate() + "'" +
            ", invoiceTotal=" + getInvoiceTotal() +
            "}";
    }
}
