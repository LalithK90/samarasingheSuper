package lk.SamarasingheSuper.asset.purchase_order_item.entity;


import com.fasterxml.jackson.annotation.JsonFilter;
import lk.SamarasingheSuper.asset.common_asset.model.enums.LiveDead;
import lk.SamarasingheSuper.asset.item.entity.Item;
import lk.SamarasingheSuper.asset.purchase_order.entity.PurchaseOrder;
import lk.SamarasingheSuper.util.audit.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter( "PurchaseOrderItem" )
public class PurchaseOrderItem extends AuditEntity {

    @Column( nullable = false )
    private String quantity;

    @Column( nullable = false, precision = 10, scale = 2 )
    private BigDecimal buyingPrice;

    @Column( nullable = false, precision = 10, scale = 2 )
    private BigDecimal lineTotal;

    @Enumerated( EnumType.STRING)
    private LiveDead liveDead;

    @ManyToOne
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    private Item item;

}
