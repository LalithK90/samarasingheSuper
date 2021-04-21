package lk.samarasingher_super.asset.item.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import lk.samarasingher_super.asset.brand.entity.Brand;
import lk.samarasingher_super.asset.category.entity.Category;
import lk.samarasingher_super.asset.common_asset.model.enums.LiveDead;
import lk.samarasingher_super.asset.item.entity.enums.ItemStatus;
import lk.samarasingher_super.asset.item.entity.enums.Measurement;
import lk.samarasingher_super.asset.ledger.entity.Ledger;
import lk.samarasingher_super.asset.purchase_order_item.entity.PurchaseOrderItem;
import lk.samarasingher_super.asset.supplier_item.entity.SupplierItem;
import lk.samarasingher_super.util.audit.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("Item")
public class Item extends AuditEntity {

    @Size( min = 5, message = "Your name cannot be accepted" )
    private String name;

    @NotEmpty
    private String rop;

    @Column( unique = true )
    private String code;

    @Column( nullable = false, precision = 10, scale = 2 )
    private BigDecimal sellPrice;
//Ahanna oni ai me kiyala
    @Enumerated( EnumType.STRING )
    private ItemStatus itemStatus;

    @Enumerated(EnumType.STRING)
    private LiveDead liveDead;

    @Enumerated(EnumType.STRING)
    private Measurement measurement;

    @Column
    private String ammount;

    //Ahanna oni ai me kiyala
    @ManyToOne
    private Category category;

    @OneToMany( mappedBy = "item" )
    private List< SupplierItem > supplierItem;

    @OneToMany( mappedBy = "item" )
    @JsonBackReference
    private List< Ledger > ledgers;

    @ManyToOne
    private Brand brand;

    @OneToMany( mappedBy = "item" )
    private List< PurchaseOrderItem > purchaseOrderItems;
}
