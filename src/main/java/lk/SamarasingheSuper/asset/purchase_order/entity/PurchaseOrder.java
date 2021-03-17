package lk.SamarasingheSuper.asset.purchase_order.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lk.SamarasingheSuper.asset.common_asset.model.enums.LiveDead;
import lk.SamarasingheSuper.asset.payment.entity.Payment;
import lk.SamarasingheSuper.asset.purchase_order.entity.enums.PurchaseOrderPriority;
import lk.SamarasingheSuper.asset.purchase_order.entity.enums.PurchaseOrderStatus;
import lk.SamarasingheSuper.asset.purchase_order_item.entity.PurchaseOrderItem;
import lk.SamarasingheSuper.asset.supplier.entity.Supplier;
import lk.SamarasingheSuper.util.audit.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("PurchaseOrder")
public class PurchaseOrder extends AuditEntity {

    private String remark;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderPriority purchaseOrderPriority;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus purchaseOrderStatus;

    @Enumerated(EnumType.STRING)
    private LiveDead liveDead;

    @ManyToOne
    private Supplier supplier;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.PERSIST)
    private List< PurchaseOrderItem > purchaseOrderItems;

    @OneToMany(mappedBy = "purchaseOrder")
    private List< Payment > payments;

    @Transient
    private BigDecimal paidAmount;

    @Transient
    private BigDecimal needToPaid;

    @Transient
    private LocalDateTime grnAt;
}
